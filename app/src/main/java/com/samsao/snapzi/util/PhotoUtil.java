package com.samsao.snapzi.util;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
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
     *
     * @param bitmap
     * @param callback
     */
    public static void saveImage(Bitmap bitmap, SaveImageCallback callback) {
        new SaveBitmapTask(bitmap, callback).execute();
    }

    /**
     * Returns the image URI
     *
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

    /**
     * Correct bitmap orientation on some devices (i.e. Samsung)
     *
     * @param sourcePath
     * @param sourceBitmap
     * @return corrected bitmap
     */
    public static Bitmap ApplyBitmapOrientationCorrection(String sourcePath, Bitmap sourceBitmap) {
        if (sourcePath == null || sourceBitmap == null) {
            return null;
        }

        try {
            ExifInterface ei;
            ei = new ExifInterface(sourcePath);
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    return RotateBitmap(sourceBitmap, 90);
                case ExifInterface.ORIENTATION_ROTATE_180:
                    return RotateBitmap(sourceBitmap, 180);
                case ExifInterface.ORIENTATION_ROTATE_270:
                    return RotateBitmap(sourceBitmap, 270);
                default:
                    return RotateBitmap(sourceBitmap, 0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Rotate bitmap
     *
     * @param bitmap original bitmap
     * @param angle  angle of rotation
     * @return rotated bitmap
     */
    public static Bitmap RotateBitmap(Bitmap bitmap, float angle) {
        if (angle != 0) {
            Matrix matrix = new Matrix();
            matrix.postRotate(angle);
            return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        } else {
            return bitmap;
        }
    }

    public static String getRealPathFromURI(Context context, Uri uri) {
        String result;
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
        if (cursor == null) {
            result = uri.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            if (idx == -1) {
                result = uri.getPath();
            } else {
                result = cursor.getString(idx);
                cursor.close();
            }
        }
        return result;
    }
}
