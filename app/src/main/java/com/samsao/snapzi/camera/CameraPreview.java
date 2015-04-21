package com.samsao.snapzi.camera;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Build;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.OrientationEventListener;
import android.view.Surface;
import android.view.TextureView;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.samsao.snapzi.util.PhotoUtil;

import java.io.IOException;
import java.util.List;


/**
 * @author vlegault
 * @since 15-03-17
 */
public class CameraPreview extends TextureView implements TextureView.SurfaceTextureListener {

    /**
     * Constants
     */
    private final String LOG_TAG = getClass().getSimpleName();
    private final LayoutMode DEFAULT_LAYOUT_MODE = LayoutMode.FIT_PARENT;
    private final CameraId DEFAULT_CAMERA_ID = CameraId.CAMERA_FACING_FRONT;
    private final int DEFAULT_MAXIMUM_VIDEO_DURATION_MS = 60000; // 60 sec
    private final int PREVIEW_ORIENTATION_THRESHOLD_DEG = 20;

    private static CameraPreview mCameraPreviewInstance;
    private Camera mCamera;

    private LayoutMode mLayoutMode;
    private FrameLayout mCameraPreviewContainer;

    private int mCameraId;
    private int mCameraBuiltInOrientationOffset = 0;
    private int mLastPreviewOrientation = 0;
    private int mOrientationWhenPictureTaken = 0;
    private float mAspectRatioWhenPictureTaken = 1.0f;

    private OrientationEventListener mOrientationListener;
    private SimpleCameraCallback mSimpleCameraCallback;

    private int mMaximumVideoDuration;
    private CamcorderProfile mCamcorderProfile;
    private MediaRecorder mMediaRecorder;

    public enum LayoutMode {
        FIT_PARENT,
        CENTER_CROP
    }

    public enum CameraId {
        CAMERA_FACING_FRONT,
        CAMERA_FACING_BACK
    }

    public interface SimpleCameraCallback {
        public void onCameraPreviewReady();

        public void onCameraPreviewFailed();

        public void onPictureReady(Bitmap image);
    }

    /**
     * Callback that plays a camera sound as near as possible to the moment when a photo is captured
     * from the sensor.
     */
    private final Camera.ShutterCallback mShutterCallback = new Camera.ShutterCallback() {
        public void onShutter() {
            mOrientationWhenPictureTaken = getOrientation();
            mAspectRatioWhenPictureTaken = getPreviewAspectRatio();
            AudioManager mgr = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
            mgr.playSoundEffect(AudioManager.FLAG_PLAY_SOUND);
        }
    };

    /**
     * Called when image data is available after a picture is taken. We transform the raw data to a
     * bitmap object then start the modification activity
     */
    private final Camera.PictureCallback mJpegCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] bytes, Camera camera) {
            Bitmap image = BitmapFactory.decodeByteArray(bytes, 0, bytes.length); // Get resulting image
            image = PhotoUtil.rotateBitmap(image, mOrientationWhenPictureTaken); // Add rotation correction to bitmap
            image = PhotoUtil.getCenterCropBitmapWithTargetAspectRatio(image, mAspectRatioWhenPictureTaken);

            if (null != mSimpleCameraCallback) {
                mSimpleCameraCallback.onPictureReady(image);
            }
        }
    };


    public static CameraPreview getNewInstance(Activity activity) {
        if (mCameraPreviewInstance != null) {
            mCameraPreviewInstance.release();
            mCameraPreviewInstance = null;
        }
        return mCameraPreviewInstance = new CameraPreview(activity);
    }

    /**
     * CameraPreview constructor
     *
     * @param activity
     */
    private CameraPreview(Activity activity) {
        super(activity);

        setLayoutMode(DEFAULT_LAYOUT_MODE);
        setCameraId(DEFAULT_CAMERA_ID);
        setMaximumVideoDuration_ms(DEFAULT_MAXIMUM_VIDEO_DURATION_MS);
    }


    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
        resizeToFitContainerView();
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
        release();
        return true;
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int width, int height) {
        resizeToFitContainerView();
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
    }

    /**
     * Get an instance of the Camera object.
     */
    public Camera getCameraInstance(int cameraId) {
        Camera camera = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD &&
                cameraId < Camera.getNumberOfCameras()) {
            try {
                camera = Camera.open(cameraId);
            } catch (Exception e) {
                Log.e(LOG_TAG, "Camera is not available (in use or does not exist): " + e.getMessage());
            }
        } else {
            try {
                camera = Camera.open();
            } catch (Exception e) {
                Log.e(LOG_TAG, "Camera is not available (in use or does not exist): " + e.getMessage());
            }
        }

        return camera;
    }

    /**
     * Resize PhotoCamera view to fit it's parent container
     */
    public void resizeToFitContainerView() {
        int previewContainerWidth = mCameraPreviewContainer.getWidth();
        int previewContainerHeight = mCameraPreviewContainer.getHeight();

        if (!adjustSurfaceLayoutSize(previewContainerWidth, previewContainerHeight)) {
            if (prepareCamera()) {
                if (null != mSimpleCameraCallback) {
                    mOrientationListener.enable();
                    mSimpleCameraCallback.onCameraPreviewReady();
                }
            } else {
                if (null != mSimpleCameraCallback) {
                    mSimpleCameraCallback.onCameraPreviewFailed();
                }
                Log.e(LOG_TAG, "Unable to prepare camera");
            }
        }
    }

    /**
     * Adjusts SurfaceView dimension to our layout available space.
     *
     * @param availableWidth  available width of the parent container
     * @param availableHeight available height of the parent container
     */
    private boolean adjustSurfaceLayoutSize(int availableWidth, int availableHeight) {
        float previewSizeWidth, previewSizeHeight;
        float heightScale, widthScale, previewSizeScale;

        if (CameraHelper.isPortrait(getContext())) {
            previewSizeWidth = mCamcorderProfile.videoFrameHeight;
            previewSizeHeight = mCamcorderProfile.videoFrameWidth;
        } else {
            previewSizeWidth = mCamcorderProfile.videoFrameWidth;
            previewSizeHeight = mCamcorderProfile.videoFrameHeight;
        }

        heightScale = availableHeight / previewSizeHeight;
        widthScale = availableWidth / previewSizeWidth;

        if (mLayoutMode == LayoutMode.FIT_PARENT) {
            // Select smaller factor, because the surface cannot be set to the size larger than display metrics.
            if (heightScale < widthScale) {
                previewSizeScale = heightScale;
            } else {
                previewSizeScale = widthScale;
            }
        } else {
            if (heightScale < widthScale) {
                previewSizeScale = widthScale;
            } else {
                previewSizeScale = heightScale;
            }
        }

        int layoutHeight = Math.round(previewSizeHeight * previewSizeScale);
        int layoutWidth = Math.round(previewSizeWidth * previewSizeScale);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.getLayoutParams();
        if ((layoutWidth != this.getWidth()) || (layoutHeight != this.getHeight())) {
            layoutParams.height = layoutHeight;
            layoutParams.width = layoutWidth;
            layoutParams.gravity = Gravity.CENTER;
            this.setLayoutParams(layoutParams);

            // A call to setLayoutParams will trigger another surfaceChanged invocation.
            // Set return value to true since the layout as been modified.
            return true;
        } else {
            // Set return value to false since no changes were made to the layout.
            return false;
        }
    }

    /**
     * Prepare camera to take a picture or a video.
     *
     * @return true on success
     */
    private boolean prepareCamera() {
        // Reset camera and media recorder instances
        releaseCamera();

        mCamera = getCameraInstance(mCameraId);
        Camera.Parameters cameraParams = mCamera.getParameters();

        // Setting up camera preview
        mLastPreviewOrientation = getPreviewOrientation();
        mCamera.setDisplayOrientation(mLastPreviewOrientation);
        cameraParams.setPreviewSize(mCamcorderProfile.videoFrameWidth, mCamcorderProfile.videoFrameHeight);

        // Setting up optimal picture size & resolution based on camera preview aspect ratio
        List<Size> supportedPreviewSizes = cameraParams.getSupportedPreviewSizes();
        List<Size> supportedPictureSizes = cameraParams.getSupportedPictureSizes();
        Size optimalPictureSize = CameraHelper.getOptimalPictureSize(supportedPreviewSizes, mCamcorderProfile.videoFrameWidth, mCamcorderProfile.videoFrameHeight);
        Size cameraPictureSize = CameraHelper.determinePictureSize(supportedPictureSizes, optimalPictureSize);
        cameraParams.setPictureSize(cameraPictureSize.width, cameraPictureSize.height);

        mCamera.setParameters(cameraParams);

        try {
            mCamera.setPreviewTexture(getSurfaceTexture());
        } catch (IOException e) {
            Log.e(LOG_TAG, "Surface texture for camera is unavailable or unsuitable" + e.getMessage());
            releaseCamera();
            return false;
        }

        try {
            mCamera.startPreview();
        } catch (Exception e) {
            Log.e(LOG_TAG, "Failed to start camera preview: " + e.getMessage());
            releaseCamera();
            return false;
        }

        return true;
    }

    public void takePicture() {
        mCamera.autoFocus(new Camera.AutoFocusCallback() {
            @Override
            public void onAutoFocus(boolean b, Camera camera) {
                camera.takePicture(mShutterCallback, null, mJpegCallback);
            }
        });
    }

    /**
     * Start video recording
     *
     * @return true on success
     */
    public boolean startRecording() {
        // Setting up video
        mMediaRecorder = new MediaRecorder();

        // Unlock and set camera to MediaRecorder
        mCamera.unlock();
        mMediaRecorder.setCamera(mCamera);

        // Set sources
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

        mMediaRecorder.setProfile(mCamcorderProfile);

        // Set output file
        mMediaRecorder.setOutputFile(CameraHelper.getDefaultVideoFilePath());

        // Set maximum duration
        mMediaRecorder.setMaxDuration(mMaximumVideoDuration);

        // Tags the video with deviceOrientationAngle in order to tell the phone how to display it
        mMediaRecorder.setOrientationHint(getOrientation());

        // Prepare configured MediaRecorder
        try {
            mMediaRecorder.prepare();
        } catch (IllegalStateException e) {
            Log.e(LOG_TAG, "IllegalStateException preparing MediaRecorder: " + e.getMessage());
            releaseMediaRecorder();
            return false;
        } catch (IOException e) {
            Log.e(LOG_TAG, "IOException preparing MediaRecorder: " + e.getMessage());
            releaseMediaRecorder();
            return false;
        }

        try {
            mMediaRecorder.start();
            return true;
        } catch (IllegalStateException exception) {
            Log.e(LOG_TAG, "start() cannot be called before prepare()");
            return false;
        }
    }

    /**
     * Stop video recording
     *
     * @return true on success
     */
    public boolean stopRecording() {
        boolean isVideoCaptureSuccessful = false;

        try {
            mMediaRecorder.stop();
            isVideoCaptureSuccessful = true;
        } catch (IllegalStateException exception) {
            Log.e(LOG_TAG, "stop() cannot be called before start()");
        } catch (RuntimeException stopException) {
            Log.e(LOG_TAG, "no valid audio/video data has been received when stop() is called.");
        }

        releaseMediaRecorder();
        return isVideoCaptureSuccessful;
    }

    /**
     * Release media recorder for other applications.
     */
    private void releaseMediaRecorder() {
        if (mMediaRecorder != null) {
            mMediaRecorder.reset();
            mMediaRecorder.release();
            mMediaRecorder = null;
        }

        if (mCamera != null) {
            mCamera.lock();
        }
    }

    /**
     * Release camera for other applications.
     */
    private void releaseCamera() {
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

    /**
     * Release the camera for other applications.
     */
    public void release() {
        releaseMediaRecorder();
        releaseCamera();
        setSurfaceTextureListener(null);

        if (mOrientationListener != null) {
            mOrientationListener.disable();
            mOrientationListener = null;
        }

        if (mCameraPreviewContainer != null) {
            mCameraPreviewContainer.removeAllViews();
        }
    }

    /**
     * Set camera preview layout mode.
     *
     * @param layoutMode : FIT_PARENT or CENTER_CROP
     */
    public CameraPreview setLayoutMode(LayoutMode layoutMode) {
        mLayoutMode = layoutMode;
        return this;
    }

    /**
     * Get which camera is currently used.
     */
    public CameraId getCameraId() {
        if (mCameraId == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            return CameraId.CAMERA_FACING_FRONT;
        } else {
            return CameraId.CAMERA_FACING_BACK;
        }
    }

    /**
     * Set which camera to use.
     *
     * @param cameraId : CAMERA_FACING_FRONT, CAMERA_FACING_BACK
     */
    public CameraPreview setCameraId(CameraId cameraId) {
        if (cameraId == CameraId.CAMERA_FACING_FRONT) {
            mCameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;
        } else {
            mCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
        }

        android.hardware.Camera.CameraInfo info =
                new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(mCameraId, info);
        mCameraBuiltInOrientationOffset = info.orientation;

        return this;
    }

    /**
     * Set maximum video capture duration.
     *
     * @param maximumVideoDuration video duration in milliseconds
     */
    public CameraPreview setMaximumVideoDuration_ms(int maximumVideoDuration) {
        mMaximumVideoDuration = maximumVideoDuration;
        return this;
    }

    /**
     * Set SimpleCameraCallback to know when the camera preview is ready or when a picture was taken.
     * Providing a SimpleCameraCallback is mandatory.
     *
     * @param simpleCameraCallback
     */
    public CameraPreview setOnCameraPreviewReady(SimpleCameraCallback simpleCameraCallback) {
        mSimpleCameraCallback = simpleCameraCallback;
        return this;
    }

    /**
     * Set in which container to display camera preview.
     *
     * @param cameraPreviewContainer
     */
    public CameraPreview into(FrameLayout cameraPreviewContainer) {

        if (mSimpleCameraCallback == null) {
            Log.e(LOG_TAG, "No SimpleCameraCallback callback was provided");
            return null;
        }

        if (cameraPreviewContainer != null) {
            mCameraPreviewContainer = cameraPreviewContainer;

            // Set a CamcorderProfile to 720p quality or lower of not available
            if (CamcorderProfile.hasProfile(CamcorderProfile.QUALITY_720P)) {
                mCamcorderProfile = CamcorderProfile.get(CamcorderProfile.QUALITY_720P);

            } else {
                mCamcorderProfile = CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH);
            }

            // Add view to container
            mCameraPreviewContainer.removeAllViews();
            setSurfaceTextureListener(this);
            mCameraPreviewContainer.addView(this);

            // Setup orientation listener to force 180 camera preview flipping (since view are not destroyed on 180 degrees layout flip)
            mOrientationListener = new OrientationEventListener(getContext(), SensorManager.SENSOR_DELAY_NORMAL) {
                public void onOrientationChanged(int orientation) {
                    if (orientation >= 0) { // if valid orientation
                        int newPreviewOrientation = (360 - mCameraBuiltInOrientationOffset + orientation) % 360;
                        int previewOrientationDelta = newPreviewOrientation - mLastPreviewOrientation;

                        if ((previewOrientationDelta > (180 - PREVIEW_ORIENTATION_THRESHOLD_DEG) &&
                                previewOrientationDelta < (180 + PREVIEW_ORIENTATION_THRESHOLD_DEG)) ||
                                (previewOrientationDelta < (-180 - PREVIEW_ORIENTATION_THRESHOLD_DEG) &&
                                        previewOrientationDelta > (-180 + PREVIEW_ORIENTATION_THRESHOLD_DEG))) {
                            mLastPreviewOrientation = getPreviewOrientation();
                            mCamera.setDisplayOrientation(mLastPreviewOrientation);
                        }
                    }
                }
            };

            return this;
        } else {
            Log.e(LOG_TAG, "Camera preview container is null");
            return null;
        }
    }

    /**
     * If more the one camera is available on the current device, this function switches the camera
     * source (FRONT, BACK).
     */
    public CameraId flip() {
        releaseCamera();

        if (mCameraId == Camera.CameraInfo.CAMERA_FACING_BACK && hasFrontCamera()) {
            mCameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;
        } else {
            mCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
        }

        resizeToFitContainerView();
        return getCameraId();
    }

    /**
     * Tells if the current device have a front camera.
     *
     * @return true if it has a front camera
     */
    public boolean hasFrontCamera() {
        PackageManager packageManager = getContext().getPackageManager();
        return packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT);
    }

    /**
     * Tells if the current camera have a flash functionality.
     *
     * @return true if it has a front camera
     */
    public boolean isFlashAvailable() {
        if (mCamera != null) {
            List<String> supportedFlashModes = mCamera.getParameters().getSupportedFlashModes();
            if (supportedFlashModes != null && supportedFlashModes.size() > 0) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * Set current camera's flash mode.
     *
     * @param flashMode : FLASH_MODE_AUTO, FLASH_MODE_OFF, FLASH_MODE_ON
     * @return true on success
     */
    public boolean setFlashMode(String flashMode) {
        boolean success = false;
        if (isFlashAvailable()) {
            List<String> supportedFlashModes = mCamera.getParameters().getSupportedFlashModes();
            if (supportedFlashModes != null && supportedFlashModes.contains(flashMode)) {
                Camera.Parameters cameraParams = mCamera.getParameters();
                cameraParams.setFlashMode(flashMode);
                mCamera.setParameters(cameraParams);
                success = true;
            } else {
                Log.e(LOG_TAG, "Flash mode \'" + flashMode + "\' not supported");
            }
        } else if (mCamera == null) {
            Log.e(LOG_TAG, "Camera is not initialized");
        } else {
            Log.e(LOG_TAG, "Flash is not available on the current camera");
        }

        return success;
    }

    /**
     * Get next available flash mode on the current camera. Supported mode : FLASH_MODE_AUTO, FLASH_MODE_OFF, FLASH_MODE_ON
     *
     * @return next available flash mode
     */
    public String getNextAvailableFlashMode() {
        String newFlashMode;
        if (isFlashAvailable() && mCamera != null) {
            List<String> supportedFlashModes = mCamera.getParameters().getSupportedFlashModes();
            if (supportedFlashModes != null) {
                String currentFlashMode = mCamera.getParameters().getFlashMode();
                switch (currentFlashMode) {
                    case Camera.Parameters.FLASH_MODE_AUTO:
                        if (supportedFlashModes.contains(Camera.Parameters.FLASH_MODE_OFF)) {
                            newFlashMode = Camera.Parameters.FLASH_MODE_OFF;
                        } else if (supportedFlashModes.contains(Camera.Parameters.FLASH_MODE_ON)) {
                            newFlashMode = Camera.Parameters.FLASH_MODE_ON;
                        } else {
                            newFlashMode = currentFlashMode;
                        }
                        break;
                    case Camera.Parameters.FLASH_MODE_OFF:
                        if (supportedFlashModes.contains(Camera.Parameters.FLASH_MODE_ON)) {
                            newFlashMode = Camera.Parameters.FLASH_MODE_ON;
                        } else if (supportedFlashModes.contains(Camera.Parameters.FLASH_MODE_AUTO)) {
                            newFlashMode = Camera.Parameters.FLASH_MODE_AUTO;
                        } else {
                            newFlashMode = currentFlashMode;
                        }
                        break;
                    case Camera.Parameters.FLASH_MODE_ON:
                        if (supportedFlashModes.contains(Camera.Parameters.FLASH_MODE_AUTO)) {
                            newFlashMode = Camera.Parameters.FLASH_MODE_AUTO;
                        } else if (supportedFlashModes.contains(Camera.Parameters.FLASH_MODE_OFF)) {
                            newFlashMode = Camera.Parameters.FLASH_MODE_OFF;
                        } else {
                            newFlashMode = currentFlashMode;
                        }
                        break;
                    default:
                        newFlashMode = currentFlashMode;
                        break;
                }
            } else {
                newFlashMode = null;
            }
        } else {
            newFlashMode = null;
        }
        return newFlashMode;
    }

    /**
     * Get camera preview display aspect ratio.
     *
     * @return aspect ratio
     */
    public float getPreviewAspectRatio() {
        float width, height;

        if (mLayoutMode == LayoutMode.CENTER_CROP) {
            width = (float) mCameraPreviewContainer.getWidth();
            height = (float) mCameraPreviewContainer.getHeight();
        } else if (mLayoutMode == LayoutMode.FIT_PARENT) {
            width = (float) getWidth();
            height = (float) getHeight();
        } else {
            width = height = 1.0f;
        }

        return width / height;
    }

    /**
     * Gets camera preview angle
     */
    private int getPreviewOrientation() {
        int angle = mCameraBuiltInOrientationOffset;
        WindowManager windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();

        switch (display.getRotation()) {
            case Surface.ROTATION_0:
                if (mCameraId == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                    angle += 180;
                }
                break;
            case Surface.ROTATION_90:
                if (mCameraId == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                    angle += 90;
                } else {
                    angle += 270;
                }
                break;
            case Surface.ROTATION_180:
                if (mCameraId != Camera.CameraInfo.CAMERA_FACING_FRONT) {
                    angle += 180;
                }
                break;
            case Surface.ROTATION_270:
                if (mCameraId == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                    angle += 270;
                } else {
                    angle += 90;
                }
                break;
            default:
                break;
        }

        return (angle % 360); // always return a value between 0 and 360 degrees
    }

    /**
     * Gets camera's current orientation angle
     */
    public int getOrientation() {
        int angle = mCameraBuiltInOrientationOffset;
        WindowManager windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();

        switch (display.getRotation()) {
            case Surface.ROTATION_90:
                if (mCameraId == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                    angle += 90;
                } else {
                    angle += 270;
                }
                break;
            case Surface.ROTATION_180:
                angle += 180;
                break;
            case Surface.ROTATION_270:
                if (mCameraId == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                    angle += 270;
                } else {
                    angle += 90;
                }
                break;
            default:
                break;
        }

        return (angle % 360); // always return a value between 0 and 360 degrees
    }
}
