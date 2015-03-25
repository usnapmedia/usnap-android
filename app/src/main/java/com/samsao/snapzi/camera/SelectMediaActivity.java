package com.samsao.snapzi.camera;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.Toast;

import com.samsao.snapzi.R;
import com.samsao.snapzi.util.PhotoUtil;
import com.samsao.snapzi.util.SaveImageCallback;
import com.soundcloud.android.crop.Crop;

import icepick.Icepick;
import icepick.Icicle;


/**
 * @author vlegault
 * @since 15-03-17
 */
public class SelectMediaActivity extends ActionBarActivity implements SelectMediaProvider {

    /**
     * Constants
     */
    private final String LOG_TAG = getClass().getSimpleName();
    public final static int RESULT_LOAD_IMG = 8401;
    public final static int RESULT_LOAD_VID = 8402;
    public final static int MINIMUM_AVAILABLE_SPACE_IN_MEGABYTES_TO_CAPTURE_PHOTO = 20;
    public final static int MINIMUM_AVAILABLE_SPACE_IN_MEGABYTES_TO_CAPTURE_VIDEO = 120;
    private final int DEFAULT_CAMERA_ID = Camera.CameraInfo.CAMERA_FACING_FRONT;
    private final boolean DEFAULT_PHOTO_MODE_STATE = true;

    SelectMediaFragment mSelectMediaFragment;

    @Icicle
    public boolean mIsPhotoModeOn;

    @Icicle
    public int mCameraId;

    @Icicle
    public int mCameraLastOrientationAngleKnown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mIsPhotoModeOn = DEFAULT_PHOTO_MODE_STATE;
        mCameraId = DEFAULT_CAMERA_ID;
        mCameraLastOrientationAngleKnown = 0;

        if (savedInstanceState != null) {
            // restore saved state
            Icepick.restoreInstanceState(this, savedInstanceState);
        } else {
            mSelectMediaFragment = SelectMediaFragment.newInstance();
            getFragmentManager().beginTransaction().replace(android.R.id.content, mSelectMediaFragment).commit();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this, outState);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // When an image is picked
        if (requestCode == RESULT_LOAD_IMG
                && resultCode == Activity.RESULT_OK
                && null != data) {
            if (CameraHelper.getAvailableDiskSpace(this) >= MINIMUM_AVAILABLE_SPACE_IN_MEGABYTES_TO_CAPTURE_PHOTO) {
                try {
                    // Get the video from data
                    String imagePath = CameraHelper.getRealPathFromURI(this, data.getData());
                    final Bitmap image = BitmapFactory.decodeStream(getContentResolver().openInputStream(data.getData()));

                    // Save image in the app sandbox
                    // FIXME: inform user that the picture is being saved in background
                    PhotoUtil.saveImage(PhotoUtil.applyBitmapOrientationCorrection(imagePath, image), new SaveImageCallback() {
                        @Override
                        public void onSuccess() {
                            // Start cropping activity
                            new Crop(CameraHelper.getImageUri())
                                    .output(CameraHelper.getImageUri())
                                    .asSquare()
                                    .start(SelectMediaActivity.this);
                        }

                        @Override
                        public void onFailure() {
                            Toast.makeText(SelectMediaActivity.this,
                                    getResources().getString(R.string.error_unable_to_open_image),
                                    Toast.LENGTH_LONG).show();
                            Log.e(LOG_TAG, "An error happened while saving image for crop activity");
                        }
                    });
                } catch (Exception e) {
                    Toast.makeText(this,
                            getResources().getString(R.string.error_unable_to_open_image),
                            Toast.LENGTH_LONG).show();
                    Log.e(LOG_TAG, "An error happened while preparing image for crop activity: " + e.getMessage());
                }
            } else {
                Toast.makeText(this,
                        getResources().getString(R.string.error_not_enough_available_space),
                        Toast.LENGTH_LONG).show();
            }
        }
        // When a video is picked
        else if (requestCode == RESULT_LOAD_VID
                && resultCode == Activity.RESULT_OK
                && null != data) {

            // Get the video from data
            String videoPath = CameraHelper.getRealPathFromURI(this, data.getData());
            if (mSelectMediaFragment != null) {
                mSelectMediaFragment.startEditVideoActivity(videoPath);
            }
        }
        // When an image as been cropped
        else if (requestCode == Crop.REQUEST_CROP
                && resultCode == Activity.RESULT_OK
                && null != data) {
            if (mSelectMediaFragment != null) {
                mSelectMediaFragment.startEditImageActivity();
            }
        }
        // On an image cropped error
        else if (requestCode == Crop.REQUEST_CROP) {
            Toast.makeText(this,
                    getResources().getString(R.string.error_unable_to_open_image),
                    Toast.LENGTH_LONG).show();
            if (Crop.getError(data) != null) {
                Log.e(LOG_TAG, "An error happened while returning from crop activity: " + Crop.getError(data).getMessage());
            } else {
                Log.e(LOG_TAG, "An error happened while returning from crop activity.");
            }
        }
    }

    @Override
    public boolean isPhotoModeOn() {
        return mIsPhotoModeOn;
    }

    @Override
    public void setIsPhotoModeOn(boolean state) {
        mIsPhotoModeOn = state;
    }

    @Override
    public int getCameraId() {
        return mCameraId;
    }

    @Override
    public void setCameraId(int cameraId) {
        mCameraId = cameraId;
    }

    @Override
    public int getCameraLastOrientationAngleKnown() {
        return mCameraLastOrientationAngleKnown;
    }

    @Override
    public void setCameraLastOrientationAngleKnown(int angle) {
        mCameraLastOrientationAngleKnown = angle;
    }
}