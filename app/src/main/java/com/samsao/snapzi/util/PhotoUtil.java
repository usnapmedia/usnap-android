package com.samsao.snapzi.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.AsyncTask;
import android.util.Log;

import com.samsao.snapzi.SnapziApplication;
import com.samsao.snapzi.camera.CameraHelper;

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
     * Save an image to disk
     *
     * @param bitmap
     * @param callback
     */
    public static void saveImage(Bitmap bitmap, SaveImageCallback callback) {
        new SaveBitmapTask(bitmap, callback).execute();
    }

    /**
     * Background task to save an image
     */
    private static class SaveBitmapTask extends AsyncTask<Void, Void, Boolean> {
        private Bitmap mBitmap;
        private SaveImageCallback mCallback;

        private SaveBitmapTask(Bitmap bitmap, SaveImageCallback callback) {
            mBitmap = bitmap;
            mCallback = callback;
        }

        protected Boolean doInBackground(Void... nothing) {
            try {
                FileOutputStream fOutputStream = SnapziApplication.getContext().openFileOutput(CameraHelper.IMAGE_FILENAME, Context.MODE_PRIVATE);
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
    public static Bitmap applyBitmapOrientationCorrection(String sourcePath, Bitmap sourceBitmap) {
        if (sourcePath == null || sourceBitmap == null) {
            return null;
        }

        try {
            ExifInterface ei;
            ei = new ExifInterface(sourcePath);
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    return rotateBitmap(sourceBitmap, 90);
                case ExifInterface.ORIENTATION_ROTATE_180:
                    return rotateBitmap(sourceBitmap, 180);
                case ExifInterface.ORIENTATION_ROTATE_270:
                    return rotateBitmap(sourceBitmap, 270);
                default:
                    return rotateBitmap(sourceBitmap, 0);
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
    public static Bitmap rotateBitmap(Bitmap bitmap, float angle) {
        if (angle != 0) {
            Matrix matrix = new Matrix();
            matrix.postRotate(angle);
            return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        } else {
            return bitmap;
        }
    }

    /**
     * Scale bitmap
     *
     * @param bitmap original bitmap
     * @param scaleX
     * @param scaleY
     * @return rotated bitmap
     */
    public static Bitmap scaleBitmap(Bitmap bitmap, float scaleX, float scaleY) {
        if (scaleX != 1 || scaleY != 1) {
            Matrix matrix = new Matrix();
            matrix.postScale(scaleX, scaleY);
            return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        } else {
            return bitmap;
        }
    }

    /**
     * Get center cropped bitmap from
     *
     * @param sourceBitmap original bitmap
     * @return rotated bitmap
     */
    public static Bitmap getCenterCropBitmapFrom(Bitmap sourceBitmap) {
        Bitmap outputBitmap;

        if (sourceBitmap.getWidth() >= sourceBitmap.getHeight()) {
            outputBitmap = Bitmap.createBitmap(
                    sourceBitmap,
                    sourceBitmap.getWidth() / 2 - sourceBitmap.getHeight() / 2,
                    0,
                    sourceBitmap.getHeight(),
                    sourceBitmap.getHeight()
            );

        } else {
            outputBitmap = Bitmap.createBitmap(
                    sourceBitmap,
                    0,
                    sourceBitmap.getHeight() / 2 - sourceBitmap.getWidth() / 2,
                    sourceBitmap.getWidth(),
                    sourceBitmap.getWidth()
            );
        }

        return outputBitmap;
    }
}
