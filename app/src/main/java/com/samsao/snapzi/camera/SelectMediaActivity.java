package com.samsao.snapzi.camera;

import android.hardware.Camera;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import icepick.Icepick;
import icepick.Icicle;


/**
 * @author vlegault
 * @since 15-03-17
 */
public class SelectMediaActivity extends ActionBarActivity implements SelectMediaProvider {

    /**
     * Constants
     */
    private final int DEFAULT_CAMERA_ID = Camera.CameraInfo.CAMERA_FACING_FRONT;
    private final boolean DEFAULT_PHOTO_MODE_STATE = true;

    @Icicle
    public boolean mIsPhotoModeOn;

    @Icicle
    public int mCameraId;

    @Icicle
    public int mCameraLastOrientationAngleKnown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mIsPhotoModeOn = DEFAULT_PHOTO_MODE_STATE;
        mCameraId = DEFAULT_CAMERA_ID;
        mCameraLastOrientationAngleKnown = 0;
        if (savedInstanceState != null) {
            // restore saved state
            Icepick.restoreInstanceState(this, savedInstanceState);
        }

        SelectMediaFragment selectMediaFragment = new SelectMediaFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(android.R.id.content, selectMediaFragment).commit();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this, outState);
    }

    @Override
    public boolean isPhotoModeOn() {
        return mIsPhotoModeOn;
    }

    @Override
    public void setIsPhotoModeOn(boolean state) {
        mIsPhotoModeOn = state;
    }

    @Override
    public int getCameraId() {
        return mCameraId;
    }

    @Override
    public void setCameraId(int cameraId) {
        mCameraId = cameraId;
    }

    @Override
    public int getCameraLastOrientationAngleKnown() {
        return mCameraLastOrientationAngleKnown;
    }

    @Override
    public void setCameraLastOrientationAngleKnown(int angle) {
        mCameraLastOrientationAngleKnown = angle;
    }
}