package com.samsao.snapzi.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

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

    /**
     * Constants
     */
    private final static String LOG_TAG = PhotoUtil.class.getSimpleName();
    public static final int MAXIMUM_IMAGE_SIDE_SIZE = 1280; // 720p standard: 1280x720

    private static SaveBitmapTask mSaveBitmapTask;


    /**
     * Save an image to disk
     *
     * @param bitmap
     * @param callback
     */
    public static void saveImage(Bitmap bitmap, SaveImageCallback callback) {
        mSaveBitmapTask = new SaveBitmapTask(bitmap, callback);
        mSaveBitmapTask.execute();
    }

    public static boolean isSaveImageInProgress() {
        if (mSaveBitmapTask != null && mSaveBitmapTask.getStatus() == AsyncTask.Status.RUNNING) {
            return true;
        } else {
            return false;
        }
    }

    public static void cancelSaveImage() {
        if (mSaveBitmapTask != null) {
            mSaveBitmapTask.cancel(true);
            mSaveBitmapTask = null;
        }
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
                resizeImage(mBitmap, PhotoUtil.MAXIMUM_IMAGE_SIDE_SIZE).compress(Bitmap.CompressFormat.PNG, 100, fOutputStream);
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

            mSaveBitmapTask = null;
        }

        /**
         * @param sourceBitmap
         * @param maximumSideSize in pixels
         * @return
         */
        private Bitmap resizeImage(Bitmap sourceBitmap, int maximumSideSize) {
            Bitmap destBitmap;
            int width = sourceBitmap.getWidth();
            int height = sourceBitmap.getHeight();

            if (width <= maximumSideSize && height <= maximumSideSize) {
                destBitmap = sourceBitmap;
            } else {
                int destWidth, destHeight;

                if (width < height) {
                    destWidth = (int) ((float) width * (float) maximumSideSize / (float) height);
                    destHeight = maximumSideSize;
                } else {
                    destHeight = (int) ((float) height * (float) maximumSideSize / (float) width);
                    destWidth = maximumSideSize;
                }
                destBitmap = Bitmap.createScaledBitmap(sourceBitmap, destWidth, destHeight, true);
            }

            return destBitmap;
        }
    }

    /**
     * Correct bitmap orientation on some devices (i.e. Samsung)
     *
     * @param context
     * @param sourceUri
     * @return corrected bitmap
     */
    public static Bitmap applyBitmapOrientationCorrection(Context context, Uri sourceUri) {
        String sourcePath = CameraHelper.getRealPathFromURI(context, sourceUri);
        final Bitmap sourceBitmap;
        try {
            sourceBitmap = BitmapFactory.decodeStream(context.getContentResolver().openInputStream(sourceUri));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }

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
     * Get a square center cropped bitmap from
     *
     * @param sourceBitmap original bitmap
     * @return rotated bitmap
     */
    public static Bitmap getSquareCenterCropBitmapFrom(Bitmap sourceBitmap) {
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

    /**
     * Get center cropped a bitmap with a new aspect ratio.
     *
     * @param sourceBitmap      original bitmap
     * @param targetAspectRatio aspect ratio of the new image
     * @return rotated bitmap
     */
    public static Bitmap getCenterCropBitmapWithTargetAspectRatio(Bitmap sourceBitmap, float targetAspectRatio) {
        float originalAspectRatio = (float) sourceBitmap.getWidth() / (float) sourceBitmap.getHeight();
        Bitmap outputBitmap;

        if (originalAspectRatio < targetAspectRatio) {
            outputBitmap = Bitmap.createBitmap(
                    sourceBitmap,
                    0,
                    (int) (((float) sourceBitmap.getHeight() - (float) sourceBitmap.getWidth() / targetAspectRatio) / 2.0f),
                    sourceBitmap.getWidth(),
                    sourceBitmap.getHeight() - (int) ((float) sourceBitmap.getHeight() - (float) sourceBitmap.getWidth() / targetAspectRatio)
            );
        } else {
            outputBitmap = Bitmap.createBitmap(
                    sourceBitmap,
                    (int) (((float) sourceBitmap.getWidth() - (float) sourceBitmap.getHeight() * targetAspectRatio) / 2.0f),
                    0,
                    sourceBitmap.getWidth() - (int) ((float) sourceBitmap.getWidth() - (float) sourceBitmap.getHeight() * targetAspectRatio),
                    sourceBitmap.getHeight()
            );
        }

        return outputBitmap;
    }

    public static ImageSize getImageSizeFromImageView(ImageView imageView) {
        if (imageView != null) {
            final int imageViewWidth = imageView.getMeasuredWidth();
            final int imageViewHeight = imageView.getMeasuredHeight();
            final int originalImageWidth = imageView.getDrawable().getIntrinsicWidth();
            final int originalImageHeight = imageView.getDrawable().getIntrinsicHeight();
            final float widthScaleFactor = (float) imageViewWidth / (float) originalImageWidth;
            final float heightScaleFactor = (float) imageViewHeight / (float) originalImageHeight;
            float imageScaleFactor;

            // Find image scale factor
            if (widthScaleFactor < heightScaleFactor) {
                imageScaleFactor = widthScaleFactor;
            } else {
                imageScaleFactor = heightScaleFactor;
            }

            return new ImageSize((int) (originalImageWidth * imageScaleFactor),
                    (int) (originalImageHeight * imageScaleFactor));
        } else {
            return new ImageSize(0, 0);
        }
    }

    public static class ImageSize {
        private int mWidth;
        private int mHeight;

        public ImageSize(int width, int height) {
            mWidth = width;
            mHeight = height;
        }

        public int getWidth() {
            return mWidth;
        }

        public void setWidth(int width) {
            mWidth = width;
        }

        public int getHeight() {
            return mHeight;
        }

        public void setHeight(int height) {
            mHeight = height;
        }
    }
}
