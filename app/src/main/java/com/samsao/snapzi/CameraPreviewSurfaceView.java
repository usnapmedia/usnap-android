package com.samsao.snapzi;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.os.Build;
import android.util.Log;
import android.view.Display;
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
public class CameraPreviewSurfaceView extends SurfaceView implements SurfaceHolder.Callback {
    /**
     * Constants
     */
    private final String LOG_TAG = getClass().getSimpleName();

    private SurfaceHolder mHolder;
    private Camera mCamera;
    private List<Size> mSupportedPreviewSizes;
    private List<Size> mSupportedPictureSizes;
    private LayoutMode mLayoutMode;
    CameraPreviewCallback mCameraPreviewCallback = null;

    public static enum LayoutMode {
        FitParent,
        CenterCrop
    }

    public interface CameraPreviewCallback {
        public void onCameraPreviewReady();

        public void onCameraPreviewFailed();
    }

    public CameraPreviewSurfaceView(Activity activity) {
        super(activity);

        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        mHolder = getHolder();
        mHolder.addCallback(this);

        // deprecated setting, but required on Android versions prior to 3.0
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        mCamera = getCameraInstance(Camera.CameraInfo.CAMERA_FACING_FRONT);
        mLayoutMode = LayoutMode.CenterCrop;

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

        Size previewSize = getOptimalPreviewSize(width, height);
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

        if (isPortrait()) {
            previewSizeWidth = previewSize.height;
            previewSizeHeight = previewSize.width;
        } else {
            previewSizeWidth = previewSize.width;
            previewSizeHeight = previewSize.height;
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
        Log.v(LOG_TAG, "Camera Preview Layout Scale Factor: " + previewSizeScale);

        int layoutHeight = (int) (previewSizeHeight * previewSizeScale);
        int layoutWidth = (int) (previewSizeWidth * previewSizeScale);
        Log.v(LOG_TAG, "Camera Preview Layout Size - w: " + layoutWidth + ", h: " + layoutHeight);

        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.getLayoutParams();
        if ((layoutWidth != this.getWidth()) || (layoutHeight != this.getHeight())) {
            layoutParams.height = layoutHeight;
            layoutParams.width = layoutWidth;
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
     * @param reqWidth  requested width
     * @param reqHeight requested height
     * @return Camera.Size object that is an element of the list returned from Camera.Parameters.getSupportedPreviewSizes.
     */
    private Size getOptimalPreviewSize(int reqWidth, int reqHeight) {
        float reqRatio, curRatio, deltaRatio;
        float deltaRatioMin = Float.MAX_VALUE;
        Size retSize = null;

        if (isPortrait()) {
            reqRatio = (float) reqHeight / (float) reqWidth;
        } else {
            reqRatio = (float) reqWidth / (float) reqHeight;
        }

        // Adjust surface size with the closest aspect-ratio
        for (Size size : mSupportedPreviewSizes) {
            curRatio = (float) size.width / (float) size.height;
            deltaRatio = Math.abs(reqRatio - curRatio);
            if (deltaRatio < deltaRatioMin) {
                deltaRatioMin = deltaRatio;
                retSize = size;
            }
        }

        return retSize;
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
        Camera.Parameters cameraParams = mCamera.getParameters();
        WindowManager windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        int angle;

        switch (display.getRotation()) {
            case Surface.ROTATION_0: // This is display orientation
                angle = 90; // This is camera orientation
                break;
            case Surface.ROTATION_90:
                angle = 0;
                break;
            case Surface.ROTATION_180:
                angle = 270;
                break;
            case Surface.ROTATION_270:
                angle = 180;
                break;
            default:
                angle = 90;
                break;
        }
        Log.v(LOG_TAG, "Camera Orientation Angle: " + angle);
        mCamera.setDisplayOrientation(angle);

        cameraParams.setPreviewSize(cameraPreviewSize.width, cameraPreviewSize.height);

        Size cameraPictureSize = determinePictureSize(cameraPreviewSize);
        cameraParams.setPictureSize(cameraPictureSize.width, cameraPictureSize.height);
        if (true) {
            Log.v(LOG_TAG, "Camera Preview Size - w: " + cameraPreviewSize.width + ", h: " + cameraPreviewSize.height);
            Log.v(LOG_TAG, "Camera Picture Size - w: " + cameraPictureSize.width + ", h: " + cameraPictureSize.height);
        }

        mCamera.setParameters(cameraParams);
    }

    /**
     * Get an instance of the Camera object.
     */
    private Camera getCameraInstance(int cameraId) {
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
     * Release the camera for other applications.
     */
    public void releaseCamera() {
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

    /**
     * Returns true if device orientation is in portrait mode
     */
    private boolean isPortrait() {
        return (getContext().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT);
    }

    public Camera getCamera() {
        return mCamera;
    }

    public void setOnPreviewReady(CameraPreviewCallback cameraPreviewCallback) {
        mCameraPreviewCallback = cameraPreviewCallback;
    }
}
