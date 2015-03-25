package com.samsao.snapzi.camera;

import android.app.Activity;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.Camera.Size;
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
public class PhotoCamera extends TextureView implements TextureView.SurfaceTextureListener {

    /**
     * Constants
     */
    private final String LOG_TAG = getClass().getSimpleName();

    private Camera mCamera;
    private List<Size> mSupportedPreviewSizes;
    private List<Size> mSupportedPictureSizes;
    private CameraHelper.LayoutMode mLayoutMode;
    CameraPreviewCallback mCameraPreviewCallback = null;

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
    public PhotoCamera(Activity activity, CameraHelper.LayoutMode layoutMode, int cameraId) {
        super(activity);

        setSurfaceTextureListener(this);
        mCamera = CameraHelper.getCameraInstance(cameraId);
        mLayoutMode = layoutMode;

        Camera.Parameters cameraParams = mCamera.getParameters();
        mSupportedPreviewSizes = cameraParams.getSupportedPreviewSizes();
        mSupportedPictureSizes = cameraParams.getSupportedPictureSizes();
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

        Size previewSize = CameraHelper.getOptimalPreviewSize(mSupportedPreviewSizes, parentViewWidth, parentViewHeight);
        if (!adjustSurfaceLayoutSize(previewSize, parentViewWidth, parentViewHeight)) {
            if (prepareVideoCamera(previewSize)) {
                if (null != mCameraPreviewCallback) {
                    mCameraPreviewCallback.onCameraPreviewReady();
                }
            } else {
                if (null != mCameraPreviewCallback) {
                    mCameraPreviewCallback.onCameraPreviewFailed();
                }
                Toast.makeText(getContext(),
                        getResources().getString(R.string.error_unable_to_start_video_camera),
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * Prepare camera to record a video.
     *
     * @param cameraPreviewSize Size of the preview camera
     * @return true on success
     */
    private boolean prepareVideoCamera(Size cameraPreviewSize) {
        int cameraCurrentOrientationAngle = CameraHelper.getCameraCurrentOrientationAngle(getContext());
        Camera.Parameters cameraParams = mCamera.getParameters();

        mCamera.stopPreview();

        mCamera.setDisplayOrientation(cameraCurrentOrientationAngle);
        cameraParams.setPreviewSize(cameraPreviewSize.width, cameraPreviewSize.height);

        Size cameraPictureSize = determinePictureSize(cameraPreviewSize);
        cameraParams.setPictureSize(cameraPictureSize.width, cameraPictureSize.height);
        mCamera.setParameters(cameraParams);

        try {
            mCamera.setPreviewTexture(getSurfaceTexture());
        } catch (IOException e) {
            Log.e(LOG_TAG, "Surface texture is unavailable or unsuitable" + e.getMessage());
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
     * Adjusts PhotoCamera TextureView dimension to our layout available space.
     *
     * @param previewSize     requested camera preview size
     * @param availableWidth  available width of the parent container
     * @param availableHeight available heigth of the parent container
     */
    private boolean adjustSurfaceLayoutSize(Size previewSize,
                                            int availableWidth, int availableHeight) {
        float previewSizeWidth, previewSizeHeight;
        float heightScale, widthScale, previewSizeScale;

        if (CameraHelper.isPortrait(getContext())) {
            previewSizeWidth = previewSize.height;
            previewSizeHeight = previewSize.width;
        } else {
            previewSizeWidth = previewSize.width;
            previewSizeHeight = previewSize.height;
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

        int layoutHeight = (int) (previewSizeHeight * previewSizeScale);
        int layoutWidth = (int) (previewSizeWidth * previewSizeScale);
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
     * Gets the optimal device specific camera preview size
     *
     * @param previewSize requested camera preview size
     * @return Camera.Size object that is an element of the list returned from Camera.Parameters.getSupportedPictureSizes.
     */
    private Size determinePictureSize(Size previewSize) {
        Size retSize = null;

        if (mSupportedPictureSizes.contains(previewSize)) {
            retSize = previewSize;
        } else {
            Log.v(LOG_TAG, "Same picture size not found.");

            float reqRatio = (float) previewSize.width / (float) previewSize.height;
            float curRatio, deltaRatio;
            float deltaRatioMin = Float.MAX_VALUE;
            for (Size size : mSupportedPictureSizes) {
                curRatio = (float) size.width / (float) size.height;
                deltaRatio = Math.abs(reqRatio - curRatio);
                if (deltaRatio < deltaRatioMin) {
                    deltaRatioMin = deltaRatio;
                    retSize = size;
                }
            }
        }

        return retSize;
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

    public void setOnPreviewReady(CameraPreviewCallback cameraPreviewCallback) {
        mCameraPreviewCallback = cameraPreviewCallback;
    }
}
