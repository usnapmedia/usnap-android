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
public class SelectMediaActivity extends ActionBarActivity implements CameraProvider {

    @Icicle
    public int mCameraId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mCameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;
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
    public int getCameraId() {
        return mCameraId;
    }

    @Override
    public void setCameraId(int cameraId) {
        mCameraId = cameraId;
    }
}