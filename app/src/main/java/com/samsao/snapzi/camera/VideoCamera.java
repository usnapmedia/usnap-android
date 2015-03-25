package com.samsao.snapzi.camera;

import android.app.Activity;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
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


/**
 * @author vlegault
 * @since 15-03-20
 */
public class VideoCamera extends TextureView implements TextureView.SurfaceTextureListener {

    /**
     * Constants
     */
    private final String LOG_TAG = getClass().getSimpleName();

    private Camera mCamera;
    private int mCameraId;
    private int mMaximumVideoDuration;
    private CameraHelper.LayoutMode mLayoutMode;
    VideoCameraCallback mVideoCameraCallback = null;

    private MediaRecorder mMediaRecorder;
    private CamcorderProfile mCameraProfile;

    public interface VideoCameraCallback {
        public void onVideoCameraReady();

        public void onVideoCameraFailed();
    }


    /**
     * VideoCamera constructor
     *
     * @param activity
     * @param layoutMode           FitToCenter or CenterCrop
     * @param cameraId             Front-facing or back-facing camera
     * @param maximumVideoDuration Maximum length (in time, for video)
     */
    public VideoCamera(Activity activity, CameraHelper.LayoutMode layoutMode, int cameraId, int maximumVideoDuration) {
        super(activity);

        setSurfaceTextureListener(this);
        mCameraId = cameraId;
        mMaximumVideoDuration = maximumVideoDuration;
        mLayoutMode = layoutMode;

        // Set a CamcorderProfile to 720p quality or lower of not available
        if (CamcorderProfile.hasProfile(CamcorderProfile.QUALITY_720P)) {
            mCameraProfile = CamcorderProfile.get(CamcorderProfile.QUALITY_720P);

        } else {
            mCameraProfile = CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH);
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
     * Resize VideoCamera view to fit it's parent container
     */
    public void resizeToFitParentView() {
        int parentViewWidth = ((View) getParent()).getWidth();
        int parentViewHeight = ((View) getParent()).getHeight();

        if (!adjustSurfaceLayoutSize(parentViewWidth, parentViewHeight)) {
            if (prepareVideoCamera()) {
                if (null != mVideoCameraCallback) {
                    mVideoCameraCallback.onVideoCameraReady();
                }
            } else {
                if (null != mVideoCameraCallback) {
                    mVideoCameraCallback.onVideoCameraFailed();
                }
                Toast.makeText(getContext(),
                        getResources().getString(R.string.error_unable_to_start_video_camera),
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
            previewSizeWidth = mCameraProfile.videoFrameHeight;
            previewSizeHeight = mCameraProfile.videoFrameWidth;
        } else {
            previewSizeWidth = mCameraProfile.videoFrameWidth;
            previewSizeHeight = mCameraProfile.videoFrameHeight;
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
     * Prepare camera to record a video.
     *
     * @return true on success
     */
    private boolean prepareVideoCamera() {
        // Reset camera and media recorder instances
        release();

        mCamera = CameraHelper.getCameraInstance(mCameraId);
        Camera.Parameters cameraParams = mCamera.getParameters();

        int cameraCurrentOrientationAngle = CameraHelper.getCameraCurrentOrientationAngle(getContext());
        mCamera.setDisplayOrientation(cameraCurrentOrientationAngle);
        cameraParams.setPreviewSize(mCameraProfile.videoFrameWidth, mCameraProfile.videoFrameHeight);
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
        mMediaRecorder.setOutputFile(CameraHelper.getVideoMediaFilePath());

        // Set maximum duration
        mMediaRecorder.setMaxDuration(mMaximumVideoDuration);

        // Tags the video with deviceOrientationAngle in order to tell the phone how to display it
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
