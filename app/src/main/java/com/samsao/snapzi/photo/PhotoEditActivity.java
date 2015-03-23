package com.samsao.snapzi.photo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.samsao.snapzi.util.PhotoUtil;
import com.samsao.snapzi.util.SaveImageCallback;

import icepick.Icepick;
import icepick.Icicle;

public class PhotoEditActivity extends ActionBarActivity implements PhotoEditFragment.Listener {
    public static final String EXTRA_URI = "com.samsao.snapzi.photo.PhotoEditActivity.EXTRA_URI";
    // brightness varies from -1.0 to 1.0, but progress bar from 0 to MAX -> initial brightness is 10 (0.0) and max is 20
    private final int INITIAL_BRIGHTNESS = 10;
    // contrast varies from 0 to 4.0, but progress bar from 0 to MAX -> initial contrast is 10 (1.0) and max is 40
    private final int INITIAL_CONTRAST = 10;

    @Icicle
    public int mBrightness;
    @Icicle
    public int mContrast;
    @Icicle
    public Uri mImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        if (intent != null) {
            mImageUri = intent.getParcelableExtra(EXTRA_URI);
        }
        mBrightness = INITIAL_BRIGHTNESS;
        mContrast = INITIAL_CONTRAST;
        // restore saved state
        Icepick.restoreInstanceState(this, savedInstanceState);

        if (savedInstanceState == null) {
            PhotoEditFragment fragment = PhotoEditFragment.newInstance();
            getFragmentManager().beginTransaction().replace(android.R.id.content, fragment).commit();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this, outState);
    }

    @Override
    public int getBrightness() {
        return mBrightness;
    }

    @Override
    public void setBrightness(int brightness) {
        mBrightness = brightness;
    }

    @Override
    public int getContrast() {
        return mContrast;
    }

    @Override
    public void setContrast(int contrast) {
        mContrast = contrast;
    }

    @Override
    public Uri getImageUri() {
        return mImageUri;
    }

    /**
     * Save the image to disk
     * @param bitmap
     */
    public void saveBitmap(Bitmap bitmap) {
        PhotoUtil.saveImage(bitmap, new SaveImageCallback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onFailure() {

            }
        });
    }
}
