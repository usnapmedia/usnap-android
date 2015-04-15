package com.samsao.snapzi.camera;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.media.AudioManager;
import android.os.Bundle;
import android.app.Fragment;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.samsao.snapzi.R;
import com.samsao.snapzi.util.PhotoUtil;
import com.samsao.snapzi.util.WindowUtil;

import butterknife.ButterKnife;
import butterknife.InjectView;


/**
 * @author vlegault
 * @since 15-03-17
 */
public class SelectMediaFragment extends Fragment {

    /**
     * Constants
     */
    private final String LOG_TAG = getClass().getSimpleName();

    private SelectMediaProvider mSelectMediaProvider;
    private CameraPreview mCameraPreview;
    private boolean mIsCapturingMedia, mIsCapturingVideo;
    private CountDownTimer mVideoCaptureCountdownTimer;
    private Dialog mPickMediaDialog;

    @InjectView(R.id.fragment_select_media_camera_preview_container)
    public FrameLayout mCameraPreviewContainer;

    @InjectView(R.id.fragment_select_media_flash_setup_button)
    public Button mFlashSetupButton;

    @InjectView(R.id.fragment_select_media_flip_camera_button)
    public Button mFlipCameraButton;

    @InjectView(R.id.fragment_select_media_pick_from_gallery_button)
    public Button mPickFromGalleryButton;

    @InjectView(R.id.fragment_select_media_video_countdown)
    public TextView mVideoCountdown;

    @InjectView(R.id.fragment_select_media_capture_media_button)
    public ProgressButton mCaptureMediaButton;

    /**
     * Callback that plays a camera sound as near as possible to the moment when a photo is captured
     * from the sensor.
     */
    private final Camera.ShutterCallback mShutterCallback = new Camera.ShutterCallback() {
        public void onShutter() {
            mSelectMediaProvider.setCameraLastOrientationAngleKnown(CameraHelper.getCameraCurrentOrientationAngle(getActivity()));
            AudioManager mgr = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
            mgr.playSoundEffect(AudioManager.FLAG_PLAY_SOUND);
        }
    };

    /**
     * Called when image data is available after a picture is taken. We transform the raw data to a
     * bitmap object then start the modification activity
     */
    private Camera.PictureCallback mJpegCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] bytes, Camera camera) {
            int cameraLastOrientationAngleKnown = mSelectMediaProvider.getCameraLastOrientationAngleKnown();
            Bitmap image = BitmapFactory.decodeByteArray(bytes, 0, bytes.length); // Get resulting image
            image = PhotoUtil.rotateBitmap(image, cameraLastOrientationAngleKnown); // Add rotation correction to bitmap
            image = PhotoUtil.getCenterCropBitmapWithTargetAspectRatio(image, mCameraPreview.getPreviewAspectRatio());

            mSelectMediaProvider.saveImageAndStartEditActivity(image);
        }
    };


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment VideoEditFragment.
     */
    public static SelectMediaFragment newInstance() {
        SelectMediaFragment fragment = new SelectMediaFragment();
        return fragment;
    }

    public SelectMediaFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_select_media, container, false);
        ButterKnife.inject(this, view);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    @Override
    public void onResume() {
        super.onResume();

        // Don't start camera preview if an image is being saved
        if (!PhotoUtil.isSaveImageInProgress()) {
            initializeCamera(mSelectMediaProvider.getCameraId());
        } else {
            hideAllButtons();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        releaseCamera();
    }

    @Override
    public void onStop() {
        super.onStop();
        dismissPickMediaDialog();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mSelectMediaProvider = (SelectMediaProvider) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement CameraProvider");
        }
    }

    /**
     * Setup view's buttons listener to their corresponding behavior.
     */
    private void setupButtons() {
        // Camera flash setup button
        if (mCameraPreview.isFlashAvailable()) {
            mFlashSetupButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setNextAvailableCameraFlashMode();
                }
            });
            setCameraFlashMode(mSelectMediaProvider.getCameraFlashMode());
            mFlashSetupButton.setVisibility(View.VISIBLE);
        } else {
            mFlashSetupButton.setVisibility(View.GONE);
        }

        // Camera flip button
        // Activate camera flipping function only if more than one camera is available
        if (Camera.getNumberOfCameras() > 1) {
            mFlipCameraButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    flipCamera();
                }
            });
        }

        // Pick media from gallery button
        mPickFromGalleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPickMediaDialog();
            }
        });

        // Capture media button
        // Setup click event to take a picture
        mCaptureMediaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WindowUtil.lockScreenOrientation(getActivity());
                if (!mIsCapturingMedia) {
                    if (CameraHelper.getAvailableDiskSpace(getActivity()) >= SelectMediaActivity.MINIMUM_AVAILABLE_SPACE_IN_MEGABYTES_TO_CAPTURE_PHOTO) {
                        triggerCapturingMediaState(true);
                        mCameraPreview.getCamera().autoFocus(new Camera.AutoFocusCallback() {
                            @Override
                            public void onAutoFocus(boolean b, Camera camera) {
                                mCameraPreview.getCamera().takePicture(mShutterCallback, null, mJpegCallback);
                            }
                        });
                    } else {
                        WindowUtil.unlockScreenOrientation(getActivity());
                        Toast.makeText(getActivity(),
                                getResources().getString(R.string.error_not_enough_available_space),
                                Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
        // Setup long click event to capture a video
        mCaptureMediaButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                WindowUtil.lockScreenOrientation(getActivity());
                if (!mIsCapturingMedia) {
                    // Verifying if there's enough space to store the new video
                    if (CameraHelper.getAvailableDiskSpace(getActivity()) >= SelectMediaActivity.MINIMUM_AVAILABLE_SPACE_IN_MEGABYTES_TO_CAPTURE_VIDEO) {
                        if (mCameraPreview.startRecording()) {
                            triggerCapturingVideo(true);
                        } else {
                            // Start recording didn't work, release the camera
                            mCameraPreview.stopRecording();
                            WindowUtil.unlockScreenOrientation(getActivity());
                            Toast.makeText(getActivity(),
                                    getResources().getString(R.string.error_unable_to_start_video_recording),
                                    Toast.LENGTH_LONG).show();
                        }
                    } else {
                        WindowUtil.unlockScreenOrientation(getActivity());
                        Toast.makeText(getActivity(),
                                getResources().getString(R.string.error_not_enough_available_space),
                                Toast.LENGTH_LONG).show();
                    }
                    return true;
                } else {
                    return false;
                }
            }
        });
        // Setup on release button event
        mCaptureMediaButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // If releasing capture media button while capturing video, then stop recording video
                if (mIsCapturingVideo && event.getAction() == MotionEvent.ACTION_UP) {
                    boolean isVideoCaptureSuccessful = false;

                    // stop recording and start video edit activity
                    if (mCameraPreview != null) {
                        isVideoCaptureSuccessful = mCameraPreview.stopRecording();
                    }
                    triggerCapturingVideo(false);
                    hideAllSettingsButtons();

                    if (isVideoCaptureSuccessful) {
                        mSelectMediaProvider.startEditVideoActivity(CameraHelper.getVideoMediaFilePath());
                    } else {
                        // Video capture didn't work
                        Toast.makeText(getActivity(),
                                getResources().getString(R.string.error_video_capture_failed),
                                Toast.LENGTH_LONG).show();
                        triggerCapturingVideo(false);
                    }
                    return true;
                } else {
                    return false;
                }
            }
        });

        // Setup video capture countdown
        mVideoCaptureCountdownTimer = new CountDownTimer(SelectMediaActivity.MAXIMUM_VIDEO_DURATION_MS, SelectMediaActivity.COUNTDOWN_INTERVAL_MS) {
            public void onTick(long millisUntilFinished) {
                mCaptureMediaButton.setProgress(1.0f - ((float) millisUntilFinished / SelectMediaActivity.MAXIMUM_VIDEO_DURATION_MS));
                mVideoCountdown.setText(String.valueOf((int) Math.ceil((double) millisUntilFinished / 1000.0))); // show elapsed time in seconds
            }

            public void onFinish() {
                boolean isVideoCaptureSuccessful = false;
                mCaptureMediaButton.setProgress(1.0f);
                mVideoCountdown.setText(String.valueOf(0));

                // stop recording and start video edit activity
                if (mCameraPreview != null) {
                    isVideoCaptureSuccessful = mCameraPreview.stopRecording();
                }

                if (isVideoCaptureSuccessful) {
                    mSelectMediaProvider.startEditVideoActivity(CameraHelper.getVideoMediaFilePath());
                } else {
                    // Video capture didn't work
                    Toast.makeText(getActivity(),
                            getResources().getString(R.string.error_video_capture_failed),
                            Toast.LENGTH_LONG).show();
                    triggerCapturingVideo(false);
                }
            }
        };
    }

    /**
     * Set Camera flash mode (if mode available on current device).
     *
     * @param flashMode
     */
    private void setCameraFlashMode(String flashMode) {
        if (mCameraPreview.setFlashMode(flashMode)) {
            mSelectMediaProvider.setCameraFlashMode(flashMode);
            updateFlashButton(mSelectMediaProvider.getCameraFlashMode());
        }
    }

    /**
     * Sets next available Camera flash mode.
     */
    private void setNextAvailableCameraFlashMode() {
        if (mCameraPreview.isFlashAvailable()) {
            mSelectMediaProvider.setCameraFlashMode(mCameraPreview.triggerNextFlashMode());
            updateFlashButton(mSelectMediaProvider.getCameraFlashMode());
        }
    }

    /**
     * Update flash button design accordingly to the passed flash mode.
     *
     * @param flashMode
     */
    private void updateFlashButton(String flashMode) {
        switch (flashMode) {
            case Camera.Parameters.FLASH_MODE_AUTO:
                mFlashSetupButton.setText("AUTO");
                break;
            case Camera.Parameters.FLASH_MODE_OFF:
                mFlashSetupButton.setText("OFF");
                break;
            case Camera.Parameters.FLASH_MODE_ON:
                mFlashSetupButton.setText("ON");
                break;
            default:
                mFlashSetupButton.setText("FLASH");
                break;
        }
    }

    /**
     * If more the one camera is available on the current device, this function switches the camera
     * source (FRONT, BACK).
     */
    public void flipCamera() {
        if (mSelectMediaProvider.getCameraId() == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            mSelectMediaProvider.setCameraId(Camera.CameraInfo.CAMERA_FACING_BACK);
        } else {
            mSelectMediaProvider.setCameraId(Camera.CameraInfo.CAMERA_FACING_FRONT);
        }

        releaseCamera();
        initializeCamera(mSelectMediaProvider.getCameraId());
    }

    /**
     * Updates camera controls accordingly to camera capturing state.
     *
     * @param isCapturingMedia
     */
    private void triggerCapturingMediaState(boolean isCapturingMedia) {
        if (isCapturingMedia) {
            mIsCapturingMedia = true;
            WindowUtil.lockScreenOrientation(getActivity());

            // inform the user that recording has started
            hideAllSettingsButtons();
        } else {
            // inform the user that recording has stopped
            if (mCameraPreview.isFlashAvailable()) {
                mFlashSetupButton.setVisibility(View.VISIBLE);
            }
            // Activate camera flipping function only if more than one camera is available
            if (Camera.getNumberOfCameras() > 1) {
                mFlipCameraButton.setVisibility(View.VISIBLE);
            }
            mFlipCameraButton.setVisibility(View.VISIBLE);
            mPickFromGalleryButton.setVisibility(View.VISIBLE);
            mVideoCountdown.setVisibility(View.GONE);

            WindowUtil.unlockScreenOrientation(getActivity());
            mIsCapturingMedia = mIsCapturingVideo = false;
        }
        mCaptureMediaButton.setVisibility(View.VISIBLE);
    }

    /**
     * Updates camera controls accordingly to camera video capturing state.
     *
     * @param isCapturingVideo
     */
    private void triggerCapturingVideo(boolean isCapturingVideo) {
        mIsCapturingVideo = isCapturingVideo;

        if (isCapturingVideo) {
            mVideoCaptureCountdownTimer.start();
            triggerCapturingMediaState(isCapturingVideo);
            mVideoCountdown.setVisibility(View.VISIBLE);
        } else {
            mVideoCaptureCountdownTimer.cancel();
            triggerCapturingMediaState(isCapturingVideo);
        }
    }

    /**
     * Hide all setting buttons.
     */
    public void hideAllSettingsButtons() {
        if (mFlashSetupButton != null) {
            mFlashSetupButton.setVisibility(View.GONE);
        }

        if (mFlipCameraButton != null) {
            mFlipCameraButton.setVisibility(View.GONE);
        }

        if (mPickFromGalleryButton != null) {
            mPickFromGalleryButton.setVisibility(View.GONE);
        }
    }

    /**
     * Hide all buttons.
     */
    public void hideAllButtons() {
        hideAllSettingsButtons();

        if (mVideoCountdown != null) {
            mVideoCountdown.setVisibility(View.GONE);
        }

        if (mCaptureMediaButton != null) {
            mCaptureMediaButton.setVisibility(View.GONE);
        }
    }

    /**
     * Initialize camera
     *
     * @param cameraId source camera: FRONT or BACK
     */
    public void initializeCamera(int cameraId) {
        if (mCameraPreview == null) {
            mCameraPreview = new CameraPreview(getActivity(), CameraPreview.LayoutMode.CenterCrop, cameraId, SelectMediaActivity.MAXIMUM_VIDEO_DURATION_MS);
            mCameraPreview.setOnCameraPreviewReady(new CameraPreview.CameraPreviewCallback() {
                @Override
                public void onCameraPreviewReady() {
                    mSelectMediaProvider.setCameraPreviewAspectRatio(mCameraPreview.getPreviewAspectRatio());
                    setupButtons();
                }

                @Override
                public void onCameraPreviewFailed() {
                    //FIXME
                }
            });
            mCameraPreviewContainer.addView(mCameraPreview);
        }

        triggerCapturingMediaState(false);
        mCaptureMediaButton.setProgress(0.0f);
    }

    /**
     * Release camera
     */
    public void releaseCamera() {
        if (mFlashSetupButton != null) {
            mFlashSetupButton.setOnClickListener(null);
        }

        if (mFlipCameraButton != null) {
            mFlipCameraButton.setOnClickListener(null);
        }

        if (mPickFromGalleryButton != null) {
            mPickFromGalleryButton.setOnClickListener(null);
        }

        if (mCaptureMediaButton != null) {
            mCaptureMediaButton.setOnClickListener(null);
            mCaptureMediaButton.setOnLongClickListener(null);
            mCaptureMediaButton.setOnTouchListener(null);
        }

        if (mCameraPreview != null) {
            mCameraPreview.release();
            if (mCameraPreviewContainer != null) {
                mCameraPreviewContainer.removeView(mCameraPreview);
            }
            mCameraPreview = null;
        }
    }

    /**
     * Show pick media dialog
     */
    public void showPickMediaDialog() {
        if (mPickMediaDialog == null) {
            mPickMediaDialog = createPickMediaDialog();
        }
        mPickMediaDialog.show();
    }

    /**
     * Hide pick media dialog
     */
    public void dismissPickMediaDialog() {
        if (mPickMediaDialog != null) {
            mPickMediaDialog.dismiss();
            mPickMediaDialog = null;
        }
    }

    /**
     * Create a dialog that asks what kind kind of media to pick from gallery.
     *
     * @return pick media dialog
     */
    private Dialog createPickMediaDialog() {
        final Dialog dialog = new Dialog(getActivity(), android.R.style.Theme_Holo_Light_Dialog);
        dialog.setTitle(R.string.action_select_media_type_title);
        dialog.setContentView(R.layout.dialog_select_media_type);

        Button pickImageButton = (Button) dialog.findViewById(R.id.dialog_select_media_type_pick_image);
        pickImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                releaseCamera();
                dismissPickMediaDialog();

                Intent intent = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                getActivity().startActivityForResult(intent, SelectMediaActivity.RESULT_IMAGE_LOADED_FROM_GALLERY);
            }
        });

        Button pickVideoButton = (Button) dialog.findViewById(R.id.dialog_select_media_type_pick_video);
        pickVideoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                releaseCamera();
                dismissPickMediaDialog();

                Intent intent = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                getActivity().startActivityForResult(intent, SelectMediaActivity.RESULT_VIDEO_LOADED_FROM_GALLERY);
            }
        });

        return dialog;
    }
}
