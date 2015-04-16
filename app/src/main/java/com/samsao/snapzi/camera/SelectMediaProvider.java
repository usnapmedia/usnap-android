package com.samsao.snapzi.camera;

import android.graphics.Bitmap;


/**
 * Created by vlegault on 15-03-18.
 */
public interface SelectMediaProvider {

    public int getCameraId();

    public void setCameraId(int cameraId);

    public String getCameraFlashMode();

    public void setCameraFlashMode(String cameraFlashMode);

    public float getCameraPreviewAspectRatio();

    public void setCameraPreviewAspectRatio(float cameraPreviewAspectRatio);

    public int getCameraLastOrientationAngleKnown();

    public void setCameraLastOrientationAngleKnown(int angle);

    public void saveImageAndStartEditActivity(Bitmap bitmap, String destFilePath);

    public void startEditActivity(String isEditPictureMode, String mediaPath);
}
