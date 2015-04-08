package com.samsao.snapzi.camera;

import android.app.Activity;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.util.Log;
import android.view.Gravity;
import android.view.TextureView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.samsao.snapzi.R;

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

    private LayoutMode mLayoutMode;
    private int mCameraId;
    private Camera mCamera;
    private boolean mIsFlashAvailable;
    CameraPreviewCallback mCameraPreviewCallback = null;

    private int mMaximumVideoDuration;
    private CamcorderProfile mCamcorderProfile;
    private MediaRecorder mMediaRecorder;

    public enum LayoutMode {
        FitParent,
        CenterCrop
    }

    public interface CameraPreviewCallback {
        public void onCameraPreviewReady();

        public void onCameraPreviewFailed();
    }


    /**
     * PhotoCamera constructor
     *
     * @param activity
     * @param layoutMode FitToCenter or CenterCrop
     * @param cameraId   Front-facing or back-facing camera
     */
    public CameraPreview(Activity activity, LayoutMode layoutMode, int cameraId, int maximumVideoDuration) {
        super(activity);

        setSurfaceTextureListener(this);
        mLayoutMode = layoutMode;
        mCameraId = cameraId;

        Camera camera = CameraHelper.getCameraInstance(mCameraId);
        List<String> supportedFlashModes = camera.getParameters().getSupportedFlashModes();
        camera.release();
        if (supportedFlashModes != null && supportedFlashModes.size() > 0) {
            mIsFlashAvailable = true;
        } else {
            mIsFlashAvailable = false;
        }

        mMaximumVideoDuration = maximumVideoDuration;
        // Set a CamcorderProfile to 720p quality or lower of not available
        if (CamcorderProfile.hasProfile(CamcorderProfile.QUALITY_720P)) {
            mCamcorderProfile = CamcorderProfile.get(CamcorderProfile.QUALITY_720P);

        } else {
            mCamcorderProfile = CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH);
        }
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
        resizeToFitParentView();
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
        release();
        return true;
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int width, int height) {
        resizeToFitParentView();
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
    }

    /**
     * Resize PhotoCamera view to fit it's parent container
     */
    public void resizeToFitParentView() {
        int parentViewWidth = ((View) getParent()).getWidth();
        int parentViewHeight = ((View) getParent()).getHeight();

        if (!adjustSurfaceLayoutSize(parentViewWidth, parentViewHeight)) {
            if (prepareCamera()) {
                if (null != mCameraPreviewCallback) {
                    mCameraPreviewCallback.onCameraPreviewReady();
                }
            } else {
                if (null != mCameraPreviewCallback) {
                    mCameraPreviewCallback.onCameraPreviewFailed();
                }
                Toast.makeText(getContext(),
                        getResources().getString(R.string.error_unable_to_launch_camera),
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * Adjusts SurfaceView dimension to our layout available space.
     *
     * @param availableWidth  available width of the parent container
     * @param availableHeight available heigth of the parent container
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

        if (mLayoutMode == LayoutMode.FitParent) {
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
        release();

        mCamera = CameraHelper.getCameraInstance(mCameraId);
        Camera.Parameters cameraParams = mCamera.getParameters();

        // Setting up camera preview
        int cameraCurrentOrientationAngle = CameraHelper.getCameraCurrentOrientationAngle(getContext());
        mCamera.setDisplayOrientation(cameraCurrentOrientationAngle);
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
            release();
            return false;
        }

        try {
            mCamera.startPreview();
        } catch (Exception e) {
            Log.e(LOG_TAG, "Failed to start camera preview: " + e.getMessage());
            release();
            return false;
        }

        return true;
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
        mMediaRecorder.setOutputFile(CameraHelper.getVideoMediaFilePath());

        // Set maximum duration
        mMediaRecorder.setMaxDuration(mMaximumVideoDuration);

        // Tags the video with deviceOrientationAngle in order to tell the phone how to display it
        int cameraCurrentOrientationAngle = CameraHelper.getCameraCurrentOrientationAngle(getContext());
        if (mCameraId == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            // Dirty fix: setOrientationHint doesn't support negative values
            if (-cameraCurrentOrientationAngle < 0) {
                mMediaRecorder.setOrientationHint(-cameraCurrentOrientationAngle + 360);
            } else {
                mMediaRecorder.setOrientationHint(-cameraCurrentOrientationAngle);
            }
        } else {
            mMediaRecorder.setOrientationHint(cameraCurrentOrientationAngle);
        }

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
     * Release the camera for other applications.
     */
    public void release() {
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

    public Camera getCamera() {
        return mCamera;
    }

    public boolean isFlashAvailable() {
        return mIsFlashAvailable;
    }

    public boolean setFlashMode(String flashMode){
        boolean success = false;
        if (mIsFlashAvailable && mCamera != null) {
            List<String> supportedFlashModes = mCamera.getParameters().getSupportedFlashModes();
            if (supportedFlashModes != null) {
                Camera.Parameters cameraParams = mCamera.getParameters();
                cameraParams.setFlashMode(flashMode);
                mCamera.setParameters(cameraParams);
                success = true;
            }
        }

        return success;
    }

    public String triggerNextFlashMode() {
        String newFlashMode;
        if (mIsFlashAvailable && mCamera != null) {
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

                Camera.Parameters cameraParams = mCamera.getParameters();
                cameraParams.setFlashMode(newFlashMode);
                mCamera.setParameters(cameraParams);
            } else {
                newFlashMode = null;
            }
        } else {
            newFlashMode = null;
        }

        return newFlashMode;
    }

    public void setOnCameraPreviewReady(CameraPreviewCallback cameraPreviewCallback) {
        mCameraPreviewCallback = cameraPreviewCallback;
    }
}
