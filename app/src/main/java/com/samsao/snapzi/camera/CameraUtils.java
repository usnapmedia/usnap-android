package com.samsao.snapzi.camera;


import android.hardware.Camera;
import android.os.Build;
import android.util.Log;

/**
 * Created by vlegault on 15-03-19.
 */
public class CameraUtils {

    /**
     * Constants
     */
    private static final String LOG_TAG = "CameraUtils";

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
}
