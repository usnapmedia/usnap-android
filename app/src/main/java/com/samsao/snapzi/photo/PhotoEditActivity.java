package com.samsao.snapzi.photo;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import icepick.Icepick;
import icepick.Icicle;

public class PhotoEditActivity extends ActionBarActivity implements PhotoEditFragment.Listener {
    public static final String EXTRA_IMAGE = "com.samsao.snapzi.photo.PhotoEditActivity.EXTRA_IMAGE";
    // brightness varies from -1.0 to 1.0, but progress bar from 0 to MAX -> initial brightness is 10 and max is 20
    private final int INITIAL_BRIGHTNESS = 10;

    @Icicle
    public byte[] mImageBytes;
    @Icicle
    public int mBrightness;

    private Bitmap mBitmap;
    @Icicle
    public Uri mImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // TODO get the image URI instead
        Intent intent = getIntent();
        if (intent != null) {
            mImageBytes = intent.getByteArrayExtra(EXTRA_IMAGE);
        }
        mBrightness = INITIAL_BRIGHTNESS;
        // restore saved state
        Icepick.restoreInstanceState(this, savedInstanceState);

        // decompress bitmap
        mImageUri = saveBitmap(BitmapFactory.decodeByteArray(mImageBytes, 0, mImageBytes.length));

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

    @Override
    public int getBrightness() {
        return mBrightness;
    }

    @Override
    public void setBrightness(int brightness) {
        mBrightness = brightness;
    }

    @Override
    public Uri getImageUri() {
        return mImageUri;
    }

    public Uri saveBitmap(Bitmap bitmap) {
        mBitmap = bitmap;
        try {
            FileOutputStream fOutputStream = openFileOutput("image.jpg", Context.MODE_PRIVATE);
            mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOutputStream);
            fOutputStream.flush();
            fOutputStream.close();
            return Uri.fromFile(getFileStreamPath("image.jpg"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(this, "Save Failed", Toast.LENGTH_SHORT).show();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Save Failed", Toast.LENGTH_SHORT).show();
            return null;
        }
    }
}
