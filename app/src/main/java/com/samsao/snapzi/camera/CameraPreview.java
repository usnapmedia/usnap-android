package com.samsao.snapzi.camera;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.FrameLayout;

import java.io.IOException;
import java.util.List;


/**
 * @author vlegault
 * @since 15-03-17
 */
public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {

    /**
     * Constants
     */
    private final String LOG_TAG = getClass().getSimpleName();

    private SurfaceHolder mHolder;
    private Camera mCamera;
    private List<Size> mSupportedPreviewSizes;
    private List<Size> mSupportedPictureSizes;
    private CameraHelper.LayoutMode mLayoutMode;
    CameraPreviewCallback mCameraPreviewCallback = null;

    public interface CameraPreviewCallback {
        public void onCameraPreviewReady();

        public void onCameraPreviewFailed();
    }

    public CameraPreview(Activity activity, int cameraId, CameraHelper.LayoutMode layoutMode) {
        super(activity);

        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        mHolder = getHolder();
        mHolder.addCallback(this);

        // deprecated setting, but required on Android versions prior to 3.0
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        mCamera = CameraHelper.getCameraInstance(cameraId);
        mLayoutMode = layoutMode;

        Camera.Parameters cameraParams = mCamera.getParameters();
        mSupportedPreviewSizes = cameraParams.getSupportedPreviewSizes();
        mSupportedPictureSizes = cameraParams.getSupportedPictureSizes();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            mCamera.setPreviewDisplay(mHolder);
        } catch (IOException e) {
            releaseCamera();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        releaseCamera();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        mCamera.stopPreview();

        Size previewSize = CameraHelper.getOptimalPreviewSize(mSupportedPreviewSizes, width, height);
        if (!adjustSurfaceLayoutSize(previewSize, width, height)) {
            configureCamera(previewSize);

            try {
                mCamera.startPreview();
                if (null != mCameraPreviewCallback) {
                    mCameraPreviewCallback.onCameraPreviewReady();
                }
            } catch (Exception e) {
                // Remove failed size and retry starting preview without
                mSupportedPreviewSizes.remove(previewSize);
                if (mSupportedPreviewSizes.size() > 0) { // prevent infinite loop
                    surfaceChanged(holder, format, width, height);
                } else {
                    Log.e(LOG_TAG, "Failed to start camera preview: " + e.getMessage());
                    if (null != mCameraPreviewCallback) {
                        mCameraPreviewCallback.onCameraPreviewFailed();
                    }
                }
            }
        }
    }

    /**
     * Adjusts SurfaceView dimension to our layout available space.
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
     * Sets Camera orientation, preview size and picture size
     */
    private void configureCamera(Size cameraPreviewSize) {
        int deviceOrientationAngle = CameraHelper.getCurrentOrientationAngle(getContext());
        Camera.Parameters cameraParams = mCamera.getParameters();

        Log.v(LOG_TAG, "Camera Orientation Angle: " + deviceOrientationAngle);
        mCamera.setDisplayOrientation(deviceOrientationAngle);

        cameraParams.setPreviewSize(cameraPreviewSize.width, cameraPreviewSize.height);

        Size cameraPictureSize = determinePictureSize(cameraPreviewSize);
        cameraParams.setPictureSize(cameraPictureSize.width, cameraPictureSize.height);
        Log.v(LOG_TAG, "Camera Preview Size - w: " + cameraPreviewSize.width + ", h: " + cameraPreviewSize.height);
        Log.v(LOG_TAG, "Camera Picture Size - w: " + cameraPictureSize.width + ", h: " + cameraPictureSize.height);

        mCamera.setParameters(cameraParams);
    }

    /**
     * Release the camera for other applications.
     */
    public void releaseCamera() {
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
