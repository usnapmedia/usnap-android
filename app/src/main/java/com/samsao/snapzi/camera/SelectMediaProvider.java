package com.samsao.snapzi.camera;

import android.graphics.Bitmap;


/**
 * Created by vlegault on 15-03-18.
 */
public interface SelectMediaProvider {

    public CameraPreview.CameraId getCameraId();

    public void setCameraId(CameraPreview.CameraId cameraId);

    public String getCameraFlashMode();

    public void setCameraFlashMode(String cameraFlashMode);

    public float getCameraPreviewAspectRatio();

    public void setCameraPreviewAspectRatio(float cameraPreviewAspectRatio);

    public void saveImageAndStartEditActivity(Bitmap bitmap, String destFilePath, final Integer campaignId);

    public void startEditActivity(String isEditPictureMode, String mediaPath, final Integer campaignId);
}
