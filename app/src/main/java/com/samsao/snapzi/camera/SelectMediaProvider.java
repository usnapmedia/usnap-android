package com.samsao.snapzi.camera;


/**
 * Created by vlegault on 15-03-18.
 */
public interface SelectMediaProvider {

    public boolean isPhotoModeOn();

    public void setIsPhotoModeOn(boolean state);

    public int getCameraId();

    public void setCameraId(int cameraId);

    public int getCameraLastOrientationAngleKnown();

    public void setCameraLastOrientationAngleKnown(int angle);
}
