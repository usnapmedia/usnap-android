package com.samsao.snapzi.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
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

    private final static String FILENAME = "image.png";
    private final static String LOG_TAG = PhotoUtil.class.getSimpleName();

    /**
     * Save an image to disk
     * @param bitmap
     * @param callback
     *
     */
    public static void saveImage(Bitmap bitmap, SaveImageCallback callback) {
        new SaveBitmapTask(bitmap, callback).execute();
    }

    /**
     * Returns the image URI
     * @return
     */
    public static Uri getImageUri() {
        return Uri.fromFile(SnapziApplication.getContext().getFileStreamPath(FILENAME));
    }

    private static class SaveBitmapTask extends AsyncTask<Void, Void, Boolean> {
        private Bitmap mBitmap;
        private SaveImageCallback mCallback;

        private SaveBitmapTask(Bitmap bitmap, SaveImageCallback callback) {
            mBitmap = bitmap;
            mCallback = callback;
        }

        protected Boolean doInBackground(Void... nothing) {
            try {
                FileOutputStream fOutputStream = SnapziApplication.getContext().openFileOutput(FILENAME, Context.MODE_PRIVATE);
                mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOutputStream);
                fOutputStream.flush();
                fOutputStream.close();
                return true;
            } catch (FileNotFoundException e) {
                Log.e(LOG_TAG, "Save bitmap failed:" + e.getMessage());
                return false;
            } catch (IOException e) {
                Log.e(LOG_TAG, "Save bitmap failed:" + e.getMessage());
                return false;
            }
        }

        protected void onPostExecute(Boolean success) {
            if (success) {
                mCallback.onSuccess();
            } else {
                mCallback.onFailure();
            }
        }
    }
}
