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
import com.samsao.snapzi.util.MediaUtil;
import com.samsao.snapzi.util.PhotoUtil;
import com.samsao.snapzi.util.SaveImageCallback;
import com.samsao.snapzi.util.VideoUtil;

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
    public final static int RESULT_MEDIA_LOADED_FROM_GALLERY = 8401;
    public final static int MAXIMUM_VIDEO_DURATION_MS = 30000; // 30 seconds
    public final static int COUNTDOWN_INTERVAL_MS = 500; // half a second
    public final static int MINIMUM_AVAILABLE_SPACE_IN_MEGABYTES_TO_CAPTURE_PHOTO = 20;
    public final static int MINIMUM_AVAILABLE_SPACE_IN_MEGABYTES_TO_CAPTURE_VIDEO = 120;
    private final int DEFAULT_CAMERA_ID = Camera.CameraInfo.CAMERA_FACING_FRONT;
    private final String DEFAULT_CAMERA_FLASH_MODE = Camera.Parameters.FLASH_MODE_OFF;

    SelectMediaFragment mSelectMediaFragment;

    @Icicle
    public int mCameraId;

    @Icicle
    public String mCameraFlashMode;

    @Icicle
    public int mCameraLastOrientationAngleKnown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mCameraId = DEFAULT_CAMERA_ID;
        mCameraFlashMode = DEFAULT_CAMERA_FLASH_MODE;
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
        if (requestCode == RESULT_MEDIA_LOADED_FROM_GALLERY
                && null != data
                && MediaUtil.getMediaTypeFromUri(this, data.getData()) == MediaUtil.MediaType.Image
                && resultCode == Activity.RESULT_OK) {
            if (CameraHelper.getAvailableDiskSpace(this) >= MINIMUM_AVAILABLE_SPACE_IN_MEGABYTES_TO_CAPTURE_PHOTO) {
                try {
                    // Get the image from data
                    String imagePath = CameraHelper.getRealPathFromURI(this, data.getData());
                    final Bitmap image = BitmapFactory.decodeStream(getContentResolver().openInputStream(data.getData()));

                    // Save image in the app sandbox
                    // FIXME: inform user that the picture is being saved in background
                    PhotoUtil.saveImage(PhotoUtil.applyBitmapOrientationCorrection(imagePath, image), new SaveImageCallback() {
                        @Override
                        public void onSuccess() {
                            if (mSelectMediaFragment != null) {
                                mSelectMediaFragment.startEditImageActivity();
                            }
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
        else if (requestCode == RESULT_MEDIA_LOADED_FROM_GALLERY
                && null != data
                && MediaUtil.getMediaTypeFromUri(this, data.getData()) == MediaUtil.MediaType.Video
                && resultCode == Activity.RESULT_OK) {
            // Get the video from data
            String sourceVideoPath = CameraHelper.getRealPathFromURI(this, data.getData());
            String destVideoPath = CameraHelper.getVideoMediaFilePath();

            // If non-local video select an other one
            if (sourceVideoPath.contains("https://")) {
                Toast.makeText(SelectMediaActivity.this,
                        getResources().getString(R.string.error_please_select_a_local_video),
                        Toast.LENGTH_LONG).show();

                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("video/*, image/*");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(intent, SelectMediaActivity.RESULT_MEDIA_LOADED_FROM_GALLERY);
            } else {
                if (VideoUtil.getSubVideo(sourceVideoPath, destVideoPath, 0.0, (double) MAXIMUM_VIDEO_DURATION_MS / 1000.0)) {
                    if (mSelectMediaFragment != null) {
                        mSelectMediaFragment.startEditVideoActivity(CameraHelper.getVideoMediaFilePath());
                    }
                } else {
                    Toast.makeText(SelectMediaActivity.this,
                            getResources().getString(R.string.error_unable_to_open_video),
                            Toast.LENGTH_LONG).show();
                }
            }
        }
        // When an unsupported media is picked
        else if (requestCode == RESULT_MEDIA_LOADED_FROM_GALLERY
                && null != data
                && MediaUtil.getMediaTypeFromUri(this, data.getData()) == MediaUtil.MediaType.Unsupported
                && resultCode == Activity.RESULT_OK) {
            Toast.makeText(SelectMediaActivity.this,
                    getResources().getString(R.string.error_unsupported_format),
                    Toast.LENGTH_LONG).show();
        }
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
    public String getCameraFlashMode() {
        return mCameraFlashMode;
    }

    @Override
    public void setCameraFlashMode(String cameraFlashMode) {
        mCameraFlashMode = cameraFlashMode;
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