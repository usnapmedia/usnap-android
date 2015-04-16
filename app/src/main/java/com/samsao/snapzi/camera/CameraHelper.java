package com.samsao.snapzi.camera;

import android.content.Context;
import android.content.res.Configuration;
import android.database.Cursor;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.WindowManager;

import com.samsao.snapzi.SnapziApplication;

import java.util.List;


/**
 * Created by vlegault on 15-03-19.
 */
public class CameraHelper {

    /**
     * Constants
     */
    private final static String LOG_TAG = CameraHelper.class.getSimpleName();
    public final static String IMAGE_FILENAME = "image.png";
    public final static String VIDEO_FILENAME = "video.mp4";


    /**
     * Get an instance of the Camera object.
     */
    public static Camera getCameraInstance(int cameraId) {
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
     * Iterate over supported camera preview sizes to see which one best fits the
     * dimensions of the given view while maintaining the aspect ratio. If none can,
     * be lenient with the aspect ratio.
     *
     * @param sizes        Supported camera preview sizes.
     * @param targetWidth
     * @param targetHeight
     * @return Best match camera preview size to fit in the view.
     */
    public static Camera.Size getOptimalPictureSize(List<Camera.Size> sizes, int targetWidth, int targetHeight) {
        // Use a very small tolerance because we want an exact match.
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) targetWidth / (double) targetHeight;
        if (sizes == null)
            return null;

        Camera.Size optimalSize = null;

        // Start with max value and refine as we iterate over available preview sizes. This is the
        // minimum difference between view and camera height.
        double minDiff = Double.MAX_VALUE;

        // Try to find a preview size that matches aspect ratio and the target view size.
        // Iterate over all available sizes and pick the largest size that can fit in the view and
        // still maintain the aspect ratio.
        for (Camera.Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE)
                continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        // Cannot find preview size that matches the aspect ratio, ignore the requirement
        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }

    /**
     * Gets the optimal device specific camera preview size
     *
     * @param previewSize requested camera preview size
     * @return Camera.Size object that is an element of the list returned from Camera.Parameters.getSupportedPictureSizes.
     */
    public static Camera.Size determinePictureSize(List<Camera.Size> supportedPictureSizes, Camera.Size previewSize) {
        Camera.Size retSize = null;

        if (supportedPictureSizes.contains(previewSize)) {
            retSize = previewSize;
        } else {
            Log.v(LOG_TAG, "Same picture size not found.");

            float reqRatio = (float) previewSize.width / (float) previewSize.height;
            float curRatio, deltaRatio;
            float deltaRatioMin = Float.MAX_VALUE;
            for (Camera.Size size : supportedPictureSizes) {
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
     * Get image media path.
     *
     * @return path to image
     */
    public static String getDefaultImageFilePath() {
        return (SnapziApplication.getContext().getFilesDir().getPath() + "/" + IMAGE_FILENAME);
    }

    /**
     * Get video media path.
     *
     * @return path to video
     */
    public static String getDefaultVideoFilePath() {
        return (SnapziApplication.getContext().getFilesDir().getPath() + "/" + VIDEO_FILENAME);
    }

    /**
     * Returns true if device orientation is in portrait mode
     */
    public static boolean isPortrait(Context context) {
        return (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT);
    }

    /**
     * Gets camera's current orientation angle
     */
    public static int getCameraCurrentOrientationAngle(Context context) {
        int angle;
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();

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

        return angle;
    }

    /**
     * Returns file path from URI.
     */
    public static String getRealPathFromURI(Context context, Uri uri) {
        String result;
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
        if (cursor == null) {
            result = uri.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            if (idx == -1) {
                result = uri.getPath();
            } else {
                result = cursor.getString(idx);
                cursor.close();
            }
        }
        return result;
    }

    /**
     * Returns the number of free megabytes on the partition containing this path.
     */
    public static long getAvailableDiskSpace(Context context) {
        long bytesAvailable = context.getFilesDir().getFreeSpace();
        long megabytesAvailable = bytesAvailable / 1000000;
        return megabytesAvailable;
    }
}
