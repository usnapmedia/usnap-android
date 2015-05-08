package com.samsao.snapzi.camera;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.samsao.snapzi.R;
import com.samsao.snapzi.edit.EditActivity;
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
public class SelectMediaActivity extends AppCompatActivity implements SelectMediaProvider, SelectMediaFragment.Listener {

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
    private final CameraPreview.CameraId DEFAULT_CAMERA_ID = CameraPreview.CameraId.CAMERA_FACING_FRONT;
    private final String DEFAULT_CAMERA_FLASH_MODE = Camera.Parameters.FLASH_MODE_OFF;
    private final static String EXTRA_CAMPAIGN_ID = "com.samsao.snapzi.camera.SelectMediaActivity.EXTRA_CAMPAIGN_ID";

    private SelectMediaFragment mSelectMediaFragment;

    @Icicle
    public CameraPreview.CameraId mCameraId;

    @Icicle
    public String mCameraFlashMode;

    @Icicle
    public float mCameraPreviewAspectRatio;

    @Icicle
    public int mCampaignId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mCameraId = DEFAULT_CAMERA_ID;
        mCameraFlashMode = DEFAULT_CAMERA_FLASH_MODE;
        mCameraPreviewAspectRatio = 0.0f;

        // restore saved state
        if (savedInstanceState != null) {
            Icepick.restoreInstanceState(this, savedInstanceState);
        }

        if (mSelectMediaFragment == null) {
            mSelectMediaFragment = SelectMediaFragment.newInstance();
            getFragmentManager().beginTransaction().replace(android.R.id.content, mSelectMediaFragment).commit();
        }

        Intent intent = getIntent();
        mCampaignId = intent.getIntExtra(EXTRA_CAMPAIGN_ID, 0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (PhotoUtil.isSaveImageInProgress() && mSelectMediaFragment != null) {
            mSelectMediaFragment.showSavingImageProgressDialog();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mSelectMediaFragment != null) {
            mSelectMediaFragment.dismissSavingImageProgressDialog();
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
        if (requestCode == RESULT_IMAGE_LOADED_FROM_GALLERY
                && null != data
                && MediaUtil.getMediaTypeFromUri(this, data.getData()) == MediaUtil.MediaType.Image
                && resultCode == Activity.RESULT_OK) {
            if (CameraHelper.getAvailableDiskSpace(this) >= MINIMUM_AVAILABLE_SPACE_IN_MEGABYTES_TO_CAPTURE_PHOTO) {
                Bitmap bitmap = PhotoUtil.applyBitmapOrientationCorrection(this, data.getData());
                bitmap = PhotoUtil.getCenterCropBitmapWithTargetAspectRatio(bitmap, getCameraPreviewAspectRatio());
                saveImageAndStartEditActivity(bitmap, CameraHelper.getDefaultImageFilePath(), mCampaignId);
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
            String destVideoPath = CameraHelper.getDefaultVideoFilePath();

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
                    startEditActivity(EditActivity.VIDEO_MODE, CameraHelper.getDefaultVideoFilePath(), mCampaignId);
                } else {
                    Toast.makeText(SelectMediaActivity.this,
                            getResources().getString(R.string.error_unable_to_open_video),
                            Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    @Override
    public CameraPreview.CameraId getCameraId() {
        return mCameraId;
    }

    @Override
    public void setCameraId(CameraPreview.CameraId cameraId) {
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
    public float getCameraPreviewAspectRatio() {
        return mCameraPreviewAspectRatio;
    }

    @Override
    public void setCameraPreviewAspectRatio(float cameraPreviewAspectRatio) {
        mCameraPreviewAspectRatio = cameraPreviewAspectRatio;
    }

    @Override
    public void saveImageAndStartEditActivity(Bitmap bitmap, String destFilePath, final Integer campaignId) {
        if (bitmap != null) {
            if (mSelectMediaFragment != null) {
                mSelectMediaFragment.showSavingImageProgressDialog();
            }
            PhotoUtil.saveImage(bitmap, destFilePath, new SaveImageCallback() {
                @Override
                public void onSuccess(String destFilePath) {
                    startEditActivity(EditActivity.IMAGE_MODE, destFilePath, campaignId);
                    if (mSelectMediaFragment != null) {
                        mSelectMediaFragment.dismissSavingImageProgressDialog();
                    }
                }

                @Override
                public void onFailure() {
                    Log.e(LOG_TAG, "An error happened while saving image");
                    if (mSelectMediaFragment != null) {
                        mSelectMediaFragment.dismissSavingImageProgressDialog();
                        mSelectMediaFragment.initializeCamera();
                    }

                    Toast.makeText(SelectMediaActivity.this,
                            getResources().getString(R.string.error_unable_to_open_image),
                            Toast.LENGTH_LONG).show();

                }
            });
        } else {
            Log.e(LOG_TAG, "bitmap is null");
            Toast.makeText(SelectMediaActivity.this,
                    getResources().getString(R.string.error_unable_to_open_image),
                    Toast.LENGTH_LONG).show();

            // Restart camera preview
            if (mSelectMediaFragment != null) {
                mSelectMediaFragment.initializeCamera();
            }
        }
    }

    @Override
    public void startEditActivity(String editMode, String mediaPath, Integer campaignId) {
        Intent editIntent = new Intent(this, EditActivity.class);
        editIntent.putExtra(EditActivity.EXTRA_EDIT_MODE, editMode);
        editIntent.putExtra(EditActivity.EXTRA_MEDIA_PATH, mediaPath);
        if (campaignId != null) {
            editIntent.putExtra(EditActivity.EXTRA_CAMPAIGN_ID, (int) campaignId);
        }
        if (mSelectMediaFragment != null) {
            mSelectMediaFragment.hideAllButtons();
            mSelectMediaFragment.releaseCamera();
        }
        startActivity(editIntent);
    }

    /**
     * Helper method to start this activity
     *
     * @param context
     * @param campaignId
     */
    public static void start(Context context, Integer campaignId) {
        Intent intent = new Intent(context, SelectMediaActivity.class);
        if (campaignId != null) {
            intent.putExtra(EXTRA_CAMPAIGN_ID, (int) campaignId);
        }
        context.startActivity(intent);
    }

    @Override
    public int getCampaignId() {
        return mCampaignId;
    }
}
