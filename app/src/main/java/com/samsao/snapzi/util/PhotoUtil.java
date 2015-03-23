package com.samsao.snapzi.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;

import com.samsao.snapzi.SnapziApplication;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author jfcartier
 * @since 15-03-23
 */
public class PhotoUtil {

    private final static String LOG_TAG = PhotoUtil.class.getSimpleName();

    /**
     * Save a bitmap to disk
     * @param bitmap
     * @return
     */
    public static Uri saveBitmap(Bitmap bitmap) {
        try {
            FileOutputStream fOutputStream = SnapziApplication.getContext().openFileOutput("image.png", Context.MODE_PRIVATE);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOutputStream);
            fOutputStream.flush();
            fOutputStream.close();
            return Uri.fromFile(SnapziApplication.getContext().getFileStreamPath("image.png"));
        } catch (FileNotFoundException e) {
            Log.e(LOG_TAG, "Save bitmap failed:" + e.getMessage());
            return null;
        } catch (IOException e) {
            Log.e(LOG_TAG, "Save bitmap failed:" + e.getMessage());
            return null;
        }
    }
}
