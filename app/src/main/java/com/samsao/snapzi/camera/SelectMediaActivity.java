package com.samsao.snapzi.camera;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.Toast;

import com.samsao.snapzi.R;
import com.samsao.snapzi.photo.EditActivity;
import com.samsao.snapzi.util.MediaUtil;
import com.samsao.snapzi.util.PhotoUtil;
import com.samsao.snapzi.util.SaveImageCallback;
import com.samsao.snapzi.util.VideoUtil;
import com.samsao.snapzi.video.VideoEditActivity;

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
    public final static int RESULT_IMAGE_LOADED_FROM_GALLERY = 8401;
    public final static int RESULT_VIDEO_LOADED_FROM_GALLERY = 8402;
    public final static int MAXIMUM_VIDEO_DURATION_MS = 30000; // 30 seconds
    public final static int COUNTDOWN_INTERVAL_MS = 84;
    public final static int MINIMUM_AVAILABLE_SPACE_IN_MEGABYTES_TO_CAPTURE_PHOTO = 20;
    public final static int MINIMUM_AVAILABLE_SPACE_IN_MEGABYTES_TO_CAPTURE_VIDEO = 120;
    private final int DEFAULT_CAMERA_ID = Camera.CameraInfo.CAMERA_FACING_FRONT;
    private final String DEFAULT_CAMERA_FLASH_MODE = Camera.Parameters.FLASH_MODE_OFF;

    SelectMediaFragment mSelectMediaFragment;
    Dialog mSavingImageProgressDialog;

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
        }

        if (mSelectMediaFragment == null) {
            mSelectMediaFragment = SelectMediaFragment.newInstance();
            getFragmentManager().beginTransaction().replace(android.R.id.content, mSelectMediaFragment).commit();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (PhotoUtil.isSaveImageInProgress()) {
            showSavingImageProgressDialog();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        dismissSavingImageProgressDialog();
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
        if (requestCode == RESULT_IMAGE_LOADED_FROM_GALLERY
                && null != data
                && MediaUtil.getMediaTypeFromUri(this, data.getData()) == MediaUtil.MediaType.Image
                && resultCode == Activity.RESULT_OK) {
            if (CameraHelper.getAvailableDiskSpace(this) >= MINIMUM_AVAILABLE_SPACE_IN_MEGABYTES_TO_CAPTURE_PHOTO) {
                Bitmap correctedImageBitmap = PhotoUtil.applyBitmapOrientationCorrection(this, data.getData());
                saveImageAndStartEditActivity(correctedImageBitmap);
            } else {
                Toast.makeText(this,
                        getResources().getString(R.string.error_not_enough_available_space),
                        Toast.LENGTH_LONG).show();
            }
        }
        // When a video is picked
        else if (requestCode == RESULT_VIDEO_LOADED_FROM_GALLERY
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
                intent.setType("video/*");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(intent, SelectMediaActivity.RESULT_VIDEO_LOADED_FROM_GALLERY);
            } else {
                if (VideoUtil.getSubVideo(sourceVideoPath, destVideoPath, 0.0, (double) MAXIMUM_VIDEO_DURATION_MS / 1000.0)) {
                    startEditVideoActivity(CameraHelper.getVideoMediaFilePath());
                } else {
                    Toast.makeText(SelectMediaActivity.this,
                            getResources().getString(R.string.error_unable_to_open_video),
                            Toast.LENGTH_LONG).show();
                }
            }
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

    @Override
    public void saveImageAndStartEditActivity(Bitmap bitmap) {
        if (bitmap != null) {
            showSavingImageProgressDialog();
            PhotoUtil.saveImage(bitmap, new SaveImageCallback() {
                @Override
                public void onSuccess() {
                    startEditImageActivity();
                    dismissSavingImageProgressDialog();
                }

                @Override
                public void onFailure() {
                    Log.e(LOG_TAG, "An error happened while saving image");
                    dismissSavingImageProgressDialog();

                    Toast.makeText(SelectMediaActivity.this,
                            getResources().getString(R.string.error_unable_to_open_image),
                            Toast.LENGTH_LONG).show();

                    // Restart camera preview
                    if (mSelectMediaFragment != null) {
                        mSelectMediaFragment.initializeCamera(mCameraId);
                    }
                }
            });
        } else {
            Log.e(LOG_TAG, "bitmap is null");
            Toast.makeText(SelectMediaActivity.this,
                    getResources().getString(R.string.error_unable_to_open_image),
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void startEditImageActivity() {
        Intent editImageIntent = new Intent(this, EditActivity.class);
        editImageIntent.putExtra(EditActivity.EXTRA_URI, CameraHelper.getImageUri());
        if (mSelectMediaFragment != null) {
            mSelectMediaFragment.releaseCamera();
        }

        startActivity(editImageIntent);
    }

    @Override
    public void startEditVideoActivity(String videoPath) {
        Intent editVideoIntent = new Intent(this, VideoEditActivity.class);
        editVideoIntent.putExtra(VideoEditActivity.EXTRA_VIDEO_PATH, videoPath);
        if (mSelectMediaFragment != null) {
            mSelectMediaFragment.releaseCamera();
        }

        startActivity(editVideoIntent);
    }

    /**
     * Show SavingImageProgressDialog
     */
    public void showSavingImageProgressDialog() {
        if (mSavingImageProgressDialog == null) {
            mSavingImageProgressDialog = createSavingImageProgressDialog();
        }
        mSavingImageProgressDialog.show();

        // Stop camera preview
        if (mSelectMediaFragment != null) {
            mSelectMediaFragment.dismissPickMediaDialog();
            mSelectMediaFragment.releaseCamera();
            mSelectMediaFragment.hideAllButtons();
        }
    }

    /**
     * Hide SavingImageProgressDialog
     */
    public void dismissSavingImageProgressDialog() {
        if (mSavingImageProgressDialog != null) {
            mSavingImageProgressDialog.dismiss();
            mSavingImageProgressDialog = null;
        }
    }

    /**
     * Create a SavingImageProgressDialog.
     *
     * @return saving image progress dialog
     */
    private Dialog createSavingImageProgressDialog() {
        final Dialog dialog = new Dialog(this, android.R.style.Theme_Holo_Light_Dialog);
        dialog.setTitle(R.string.action_processing_image_title);
        dialog.setContentView(R.layout.dialog_processing_image);
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                PhotoUtil.cancelSaveImage();

                // Restart camera preview
                if (mSelectMediaFragment != null) {
                    mSelectMediaFragment.initializeCamera(mCameraId);
                }
            }
        });

        return dialog;
    }
}
