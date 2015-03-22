package com.samsao.snapzi.photo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import icepick.Icepick;
import icepick.Icicle;

public class PhotoEditActivity extends ActionBarActivity implements PhotoEditFragment.Listener {
    public static final String EXTRA_IMAGE = "com.samsao.snapzi.photo.PhotoEditActivity.EXTRA_IMAGE";
    @Icicle
    public byte[] mImageBytes;

    private Bitmap mBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // TODO get the image URI instead
        Intent intent = getIntent();
        if (intent != null) {
            mImageBytes = intent.getByteArrayExtra(EXTRA_IMAGE);
        }

        // restore saved state
        Icepick.restoreInstanceState(this, savedInstanceState);

        // decompress bitmap
        mBitmap = BitmapFactory.decodeByteArray(mImageBytes, 0, mImageBytes.length);

        if (savedInstanceState == null) {
            PhotoEditFragment fragment = PhotoEditFragment.newInstance();
            getFragmentManager().beginTransaction().replace(android.R.id.content, fragment, "TAG").commit();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this, outState);
    }

    @Override
    public Bitmap getBitmap() {
        return mBitmap;
    }
}
