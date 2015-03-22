package com.samsao.snapzi.camera;

import android.app.Activity;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.util.Log;
import android.view.Gravity;
import android.view.TextureView;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.samsao.snapzi.R;

import java.io.IOException;


/**
 * @author vlegault
 * @since 15-03-20
 */
public class VideoCamera extends TextureView implements TextureView.SurfaceTextureListener {

    /**
     * Constants
     */
    private final String LOG_TAG = getClass().getSimpleName();
    private final CameraHelper.LayoutMode DEFAULT_VIDEO_CAMERA_LAYOUT = CameraHelper.LayoutMode.CenterCrop;

    private Camera mCamera;
    private int mCameraId;
    private CameraHelper.LayoutMode mLayoutMode;
    VideoCameraCallback mVideoCameraCallback = null;

    private MediaRecorder mMediaRecorder;
    private CamcorderProfile mCameraProfile;

    public interface VideoCameraCallback {
        public void onVideoCameraReady();

        public void onVideoCameraFailed();
    }

    public VideoCamera(Activity activity, int cameraId) {
        super(activity);

        setSurfaceTextureListener(this);
        mCameraId = cameraId;
        mLayoutMode = DEFAULT_VIDEO_CAMERA_LAYOUT;

        // Set a CamcorderProfile to 720p quality or lower of not available
        if (CamcorderProfile.hasProfile(CamcorderProfile.QUALITY_720P)) {
            mCameraProfile = CamcorderProfile.get(CamcorderProfile.QUALITY_720P);

        } else {
            mCameraProfile = CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH);
        }
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
        if (!adjustSurfaceLayoutSize(mCameraProfile.videoFrameWidth, mCameraProfile.videoFrameHeight, width, height)) {
            if (!prepareVideoCamera(mCameraProfile.videoFrameWidth, mCameraProfile.videoFrameHeight)) {
                Toast.makeText(getContext(),
                        getResources().getString(R.string.error_unable_to_start_video_camera),
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
        release();
        return true;
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int width, int height) {
        if (!prepareVideoCamera(mCameraProfile.videoFrameWidth, mCameraProfile.videoFrameHeight)) {
            Toast.makeText(getContext(),
                    getResources().getString(R.string.error_unable_to_start_video_camera),
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
    }

    /**
     * Adjusts SurfaceView dimension to our layout available space.
     */
    private boolean adjustSurfaceLayoutSize(int targetedWidth, int targetedHeight,
                                            int availableWidth, int availableHeight) {
        float previewSizeWidth, previewSizeHeight;
        float heightScale, widthScale, previewSizeScale;

        if (CameraHelper.isPortrait(getContext())) {
            previewSizeWidth = targetedHeight;
            previewSizeHeight = targetedWidth;
        } else {
            previewSizeWidth = targetedWidth;
            previewSizeHeight = targetedHeight;
        }

        heightScale = availableHeight / previewSizeHeight;
        widthScale = availableWidth / previewSizeWidth;

        if (mLayoutMode == CameraHelper.LayoutMode.FitParent) {
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
        Log.v(LOG_TAG, "Camera Preview Layout Scale Factor: " + previewSizeScale);

        int layoutHeight = Math.round(previewSizeHeight * previewSizeScale);
        int layoutWidth = Math.round(previewSizeWidth * previewSizeScale);
        Log.v(LOG_TAG, "Camera Preview Layout Size - w: " + layoutWidth + ", h: " + layoutHeight);

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
     * Prepare camera to record a video.
     *
     * @param width  width of the rendering surface
     * @param height height of the rendering surface
     * @return true on success
     */
    private boolean prepareVideoCamera(int width, int height) {
        // Reset camera and media recorder instances
        release();

        mCamera = CameraHelper.getCameraInstance(mCameraId);
        Camera.Parameters cameraParams = mCamera.getParameters();

        int deviceOrientationAngle = CameraHelper.getCurrentOrientationAngle(getContext());
        Log.v(LOG_TAG, "Camera Orientation Angle: " + deviceOrientationAngle);
        mCamera.setDisplayOrientation(deviceOrientationAngle);

        Log.v(LOG_TAG, "Camera Preview Size - w: " + width + ", h: " + height);
        cameraParams.setPreviewSize(width, height);

        mCamera.setParameters(cameraParams);
        try {
            mCamera.setPreviewTexture(getSurfaceTexture());
        } catch (IOException e) {
            Log.e(LOG_TAG, "Surface texture is unavailable or unsuitable" + e.getMessage());
            release();
            return false;
        }

        mCamera.startPreview();

        mMediaRecorder = new MediaRecorder();

        // Unlock and set camera to MediaRecorder
        mCamera.unlock();
        mMediaRecorder.setCamera(mCamera);

        // Set sources
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

        mMediaRecorder.setProfile(mCameraProfile);

        // Set output file
        mMediaRecorder.setOutputFile(CameraHelper.getOutputMediaFile(CameraHelper.MEDIA_TYPE_VIDEO).toString());

        // Prepare configured MediaRecorder
        try {
            mMediaRecorder.prepare();
        } catch (IllegalStateException e) {
            Log.d(LOG_TAG, "IllegalStateException preparing MediaRecorder: " + e.getMessage());
            releaseMediaRecorder();
            return false;
        } catch (IOException e) {
            Log.d(LOG_TAG, "IOException preparing MediaRecorder: " + e.getMessage());
            releaseMediaRecorder();
            return false;
        }

        return true;
    }

    /**
     * Start recording
     *
     * @return true on success
     */
    public boolean startRecording() {
        if (mMediaRecorder != null) {
            try {
                mMediaRecorder.start();
                return true;
            } catch (IllegalStateException exception) {
                Log.e(LOG_TAG, "start() cannot be called before prepare()");
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * Stop recording
     */
    public void stopRecording() {
        try {
            mMediaRecorder.stop();
        } catch (IllegalStateException exception) {
            Log.v(LOG_TAG, "stop() cannot be called before start()");
        }

        releaseMediaRecorder();
    }

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
        releaseMediaRecorder();

        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

    public void setOnPreviewReady(VideoCameraCallback videoCameraCallback) {
        mVideoCameraCallback = videoCameraCallback;
    }
}
