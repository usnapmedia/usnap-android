package com.samsao.snapzi.camera;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.media.AudioManager;
import android.os.Bundle;
import android.app.Fragment;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.samsao.snapzi.R;
import com.samsao.snapzi.photo.PhotoEditActivity;
import com.samsao.snapzi.preferences.PreferencesActivity;
import com.samsao.snapzi.util.PhotoUtil;
import com.samsao.snapzi.util.SaveImageCallback;
import com.samsao.snapzi.video.VideoEditActivity;

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
    public Button mCaptureMediaButton;

    @InjectView(R.id.fragment_select_media_pref_button)
    public Button mPreferenceButton;

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
            Bitmap image = BitmapFactory.decodeByteArray(bytes, 0, bytes.length); // Get resulting center cropped photo

            // Adjust bitmap depending on camera ID and orientation
            if (mSelectMediaProvider.getCameraId() == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                image = PhotoUtil.scaleBitmap(image, -1, 1); // Compensate mirror effect
            }
            image = PhotoUtil.rotateBitmap(image, cameraLastOrientationAngleKnown);

            // FIXME: inform user of picture saving in background
            PhotoUtil.saveImage(image, new SaveImageCallback() {
                @Override
                public void onSuccess() {
                    startEditImageActivity();
                }

                @Override
                public void onFailure() {
                    Toast.makeText(getActivity(),
                            getResources().getString(R.string.error_unable_to_take_picture),
                            Toast.LENGTH_LONG).show();
                    Log.e(LOG_TAG, "An error happened while taking a picture");
                }
            });
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
        setupButtons();

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
        initializeCamera(mSelectMediaProvider.getCameraId());
    }

    @Override
    public void onPause() {
        super.onPause();
        releaseCamera();
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
        // Camera flip button
        // Activate camera flipping function only if more than one camera is available
        if (Camera.getNumberOfCameras() > 1) {
            mFlipCameraButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    flipCamera();
                }
            });
            mFlipCameraButton.setVisibility(View.VISIBLE);
        } else {
            mFlipCameraButton.setVisibility(View.GONE);
        }

        // Preferences button
        mPreferenceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                releaseCamera();
                startActivity(new Intent(getActivity(), PreferencesActivity.class));
            }
        });

        // Pick media from gallery button
        mPickFromGalleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                releaseCamera();

                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("video/*, image/*");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                getActivity().startActivityForResult(intent, SelectMediaActivity.RESULT_MEDIA_LOADED_FROM_GALLERY);
            }
        });

        // Capture media button
        // Setup click event to take a picture
        mCaptureMediaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                if (!mIsCapturingMedia) {
                    // Verifying if there's enough space to store the new video
                    if (CameraHelper.getAvailableDiskSpace(getActivity()) >= SelectMediaActivity.MINIMUM_AVAILABLE_SPACE_IN_MEGABYTES_TO_CAPTURE_VIDEO) {
                        if (mCameraPreview.startRecording()) {
                            triggerCapturingVideo(true);
                        } else {
                            // prepare didn't work, release the camera
                            mCameraPreview.stopRecording();
                            Toast.makeText(getActivity(),
                                    getResources().getString(R.string.error_unable_to_start_video_recording),
                                    Toast.LENGTH_LONG).show();
                        }
                    } else {
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
                    // stop recording and start video edit activity
                    mVideoCaptureCountdownTimer.cancel();
                    if (mCameraPreview != null) {
                        mCameraPreview.stopRecording();
                    }
                    getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
                    mIsCapturingMedia = mIsCapturingVideo = false;

                    startEditVideoActivity(CameraHelper.getVideoMediaFilePath());
                    return true;
                } else {
                    return false;
                }
            }
        });

        // Setup video capture countdown
        mVideoCaptureCountdownTimer = new CountDownTimer(SelectMediaActivity.MAXIMUM_VIDEO_DURATION_MS, SelectMediaActivity.COUNTDOWN_INTERVAL_MS) {
            public void onTick(long millisUntilFinished) {
                mVideoCountdown.setText(String.valueOf((int) Math.ceil((double) millisUntilFinished / 1000.0))); // show elapsed time in seconds
            }

            public void onFinish() {
                mVideoCountdown.setText(String.valueOf(0));

                // stop recording and start video edit activity
                if (mCameraPreview != null) {
                    mCameraPreview.stopRecording();
                }
                getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
                mIsCapturingMedia = mIsCapturingVideo = false;

                startEditVideoActivity(CameraHelper.getVideoMediaFilePath());
            }
        };
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

    private void triggerCapturingMediaState(boolean isCapturingMedia) {
        if (isCapturingMedia) {
            mIsCapturingMedia = true;
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);

            // inform the user that recording has started
            mFlashSetupButton.setVisibility(View.GONE);
            mFlipCameraButton.setVisibility(View.GONE);
            mPickFromGalleryButton.setVisibility(View.GONE);
            mPreferenceButton.setVisibility(View.GONE);
            mVideoCountdown.setVisibility(View.VISIBLE);

        } else {
            // inform the user that recording has stopped
            mFlashSetupButton.setVisibility(View.VISIBLE);
            mFlipCameraButton.setVisibility(View.VISIBLE);
            mPickFromGalleryButton.setVisibility(View.VISIBLE);
            mPreferenceButton.setVisibility(View.VISIBLE);
            mVideoCountdown.setVisibility(View.GONE);

            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
            mIsCapturingMedia = mIsCapturingVideo = false;
        }
    }

    private void triggerCapturingVideo(boolean isCapturingVideo) {
        mIsCapturingVideo = isCapturingVideo;

        if (isCapturingVideo) {
            triggerCapturingMediaState(isCapturingVideo);
            mVideoCaptureCountdownTimer.start();
        } else {
            mVideoCaptureCountdownTimer.cancel();
            triggerCapturingMediaState(isCapturingVideo);
        }
    }

    /**
     * Initialize camera
     *
     * @param cameraId source camera: FRONT or BACK
     */
    private void initializeCamera(int cameraId) {
        mCameraPreview = new CameraPreview(getActivity(), CameraPreview.LayoutMode.FitParent, cameraId, SelectMediaActivity.MAXIMUM_VIDEO_DURATION_MS);
        mCameraPreviewContainer.addView(mCameraPreview);
        triggerCapturingMediaState(false);
    }

    /**
     * Release camera
     */
    private void releaseCamera() {
        if (mCameraPreview != null) {
            mCameraPreview.release();
            mCameraPreviewContainer.removeView(mCameraPreview);
            mCameraPreview = null;
        }
    }

    /**
     * Starts edit image activity.
     */
    public void startEditImageActivity() {
        Intent editImageIntent = new Intent(getActivity(), PhotoEditActivity.class);
        editImageIntent.putExtra(PhotoEditActivity.EXTRA_URI, CameraHelper.getImageUri());
        releaseCamera();
        startActivity(editImageIntent);
    }

    /**
     * Starts edit video activity.
     */
    public void startEditVideoActivity(String videoPath) {
        Intent editVideoIntent = new Intent(getActivity(), VideoEditActivity.class);
        editVideoIntent.putExtra(VideoEditActivity.EXTRA_VIDEO_PATH, videoPath);
        releaseCamera();
        startActivity(editVideoIntent);
    }
}
