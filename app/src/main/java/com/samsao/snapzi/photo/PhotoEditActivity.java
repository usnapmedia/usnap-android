package com.samsao.snapzi.photo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;

import com.aviary.android.feather.sdk.AviaryIntent;
import com.aviary.android.feather.sdk.internal.filters.ToolLoaderFactory.Tools;
import com.aviary.android.feather.sdk.internal.headless.utils.MegaPixels;

import java.io.ByteArrayOutputStream;

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

        Intent serviceIntent = AviaryIntent.createCdsInitIntent(getBaseContext());
        startService(serviceIntent);

        Intent aviaryIntent = new AviaryIntent.Builder(this)
                .setData(getImageUri()) // input image src
                .withOutput(Uri.parse("file://" + "wathever.jpg")) // output file
                .withOutputFormat(Bitmap.CompressFormat.JPEG) // output format
                .withOutputSize(MegaPixels.Mp10) // output size
                .withOutputQuality(90) // output quality
                .withVibrationEnabled(false)
                .withToolList(new Tools[]{Tools.LIGHTING, Tools.DRAW, Tools.TEXT, Tools.CROP, Tools.ORIENTATION})
                .build();

        // start the activity
        startActivityForResult(aviaryIntent, 1);
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

    public Uri getImageUri() {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(getContentResolver(), mBitmap, "Title", null);
        return Uri.parse(path);
    }
}
