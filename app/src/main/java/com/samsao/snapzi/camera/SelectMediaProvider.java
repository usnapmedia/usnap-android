package com.samsao.snapzi.camera;


/**
 * Created by vlegault on 15-03-18.
 */
public interface SelectMediaProvider {

    public int getCameraId();

    public void setCameraId(int cameraId);

    public String getCameraFlashMode();

    public void setCameraFlashMode(String cameraFlashMode);

    public int getCameraLastOrientationAngleKnown();

    public void setCameraLastOrientationAngleKnown(int angle);
}
