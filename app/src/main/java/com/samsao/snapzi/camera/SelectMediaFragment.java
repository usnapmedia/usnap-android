package com.samsao.snapzi.camera;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.media.AudioManager;
import android.os.Bundle;
import android.app.Fragment;
import android.os.CountDownTimer;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
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
public class SelectMediaFragment extends Fragment implements SaveImageCallback {

    /**
     * Constants
     */
    private final String LOG_TAG = getClass().getSimpleName();
    private final static int RESULT_LOAD_IMG = 8401;
    private final static int RESULT_LOAD_VID = 8402;
    private final int MAXIMUM_VIDEO_DURATION = 30000; // 30 seconds
    private final int COUNTDOWN_INTERVAL = 500; // half a second
    private final int MINIMUM_AVAILABLE_SPACE_IN_MEGABYTES_TO_CAPTURE_PHOTO = 20;
    private final int MINIMUM_AVAILABLE_SPACE_IN_MEGABYTES_TO_CAPTURE_VIDEO = 120;

    private SelectMediaProvider mSelectMediaProvider;
    private PhotoCamera mPhotoCamera;
    private VideoCamera mVideoCamera;
    private boolean mIsRecording;
    private CountDownTimer mVideoCaptureCountdownTimer;

    @InjectView(R.id.fragment_select_media_current_mode)
    public TextView mCurrentModeTextView;

    @InjectView(R.id.fragment_select_media_camera_preview_container)
    public FrameLayout mCameraPreviewContainer;

    @InjectView(R.id.fragment_select_media_flip_camera_button)
    public Button mFlipCameraButton;

    @InjectView(R.id.fragment_select_media_pick_button)
    public Button mPickButton;

    @InjectView(R.id.fragment_select_media_video_countdown)
    public TextView mVideoCountdown;

    @InjectView(R.id.fragment_select_media_take_button)
    public Button mTakeButton;

    @InjectView(R.id.fragment_select_media_trigger_photo_mode_button)
    public Button mTriggerPhotoModeButton;

    @InjectView(R.id.fragment_select_media_trigger_video_mode_button)
    public Button mTriggerVideoModeButton;

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
            Bitmap image = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

            // Adjust bitmap depending on camera ID and orientation
            if (mSelectMediaProvider.getCameraId() == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                image = PhotoUtil.ScaleBitmap(image, -1, 1); // Compensate mirror effect
            }
            image = PhotoUtil.RotateBitmap(image, cameraLastOrientationAngleKnown);

            // FIXME: inform user of picture saving in background
            PhotoUtil.saveImage(image, SelectMediaFragment.this);
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

        if (mSelectMediaProvider.isPhotoModeOn()) {
            setPhotoFeatures();
        } else {
            setVideoFeatures();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        releasePhotoCamera();
        releaseVideoCamera();
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // When an image is picked
        if (requestCode == RESULT_LOAD_IMG
                && resultCode == Activity.RESULT_OK
                && null != data) {

            if (CameraHelper.getAvailableDiskSpace(getActivity()) >= MINIMUM_AVAILABLE_SPACE_IN_MEGABYTES_TO_CAPTURE_PHOTO) {
                try {
                    // Get the video from data
                    String imagePath = CameraHelper.getRealPathFromURI(getActivity(), data.getData());
                    final Bitmap image = BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(data.getData()));

                    // Save image in the app sandbox
                    // FIXME: inform user of picture saving in background
                    PhotoUtil.saveImage(PhotoUtil.ApplyBitmapOrientationCorrection(imagePath, image), this);
                } catch (Exception e) {
                    Toast.makeText(getActivity(),
                            getResources().getString(R.string.error_unable_to_open_image),
                            Toast.LENGTH_LONG).show();
                    Log.e(LOG_TAG, "An error happened while trying to open an image: " + e.getMessage());
                }
            } else {
                Toast.makeText(getActivity(),
                        getResources().getString(R.string.error_not_enough_available_space),
                        Toast.LENGTH_LONG).show();
            }
        }
        // When a video is picked
        else if (requestCode == RESULT_LOAD_VID
                && resultCode == Activity.RESULT_OK
                && null != data) {

            // Get the video from data
            String videoPath = CameraHelper.getRealPathFromURI(getActivity(), data.getData());
            startEditVideoActivity(videoPath);
        }
    }

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

    /**
     * Sets common features for PHOTO and VIDEO modes.
     */
    private void setCommonFeatures() {
        // Reset camera
        mIsRecording = false;
        releasePhotoCamera();
        releaseVideoCamera();

        // Sets flip camera button behavior
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

        // Sets trigger photo mode button behavior
        mTriggerPhotoModeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mSelectMediaProvider.isPhotoModeOn()) {
                    setPhotoFeatures();
                }
            }
        });

        // Sets trigger video mode button behavior
        mTriggerVideoModeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mSelectMediaProvider.isPhotoModeOn()) {
                    setVideoFeatures();
                }
            }
        });

        // Sets edit preferences button behavior
        mPreferenceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mSelectMediaProvider.isPhotoModeOn()) {
                    releasePhotoCamera();
                } else {
                    releaseVideoCamera();
                }

                startActivity(new Intent(getActivity(), PreferencesActivity.class));
            }
        });
    }

    /**
     * Sets PHOTO mode features.
     */
    private void setPhotoFeatures() {
        setCommonFeatures();
        createPhotoCamera(mSelectMediaProvider.getCameraId());

        // Sets the pick picture button behaviour
        mPickButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                releasePhotoCamera();

                Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
            }
        });

        // Sets the take picture button behaviour
        mTakeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (CameraHelper.getAvailableDiskSpace(getActivity()) >= MINIMUM_AVAILABLE_SPACE_IN_MEGABYTES_TO_CAPTURE_PHOTO) {
                    mPhotoCamera.getCamera().autoFocus(new Camera.AutoFocusCallback() {
                        @Override
                        public void onAutoFocus(boolean b, Camera camera) {
                            mPhotoCamera.getCamera().takePicture(mShutterCallback, null, mJpegCallback);
                        }
                    });
                } else {
                    Toast.makeText(getActivity(),
                            getResources().getString(R.string.error_not_enough_available_space),
                            Toast.LENGTH_LONG).show();
                }
            }
        });
        mTakeButton.setText("TAKE");
        mTakeButton.setTextColor(getResources().getColor(android.R.color.black));

        // Update UI
        mCurrentModeTextView.setText("PHOTO MODE");
        mVideoCountdown.setVisibility(View.GONE);
        mSelectMediaProvider.setIsPhotoModeOn(true);
    }

    /**
     * Sets VIDEO mode features.
     */
    private void setVideoFeatures() {
        setCommonFeatures();
        createVideoCamera(mSelectMediaProvider.getCameraId());

        // Setup video capture countdown
        mVideoCaptureCountdownTimer = new CountDownTimer(MAXIMUM_VIDEO_DURATION, COUNTDOWN_INTERVAL) {
            public void onTick(long millisUntilFinished) {
                mVideoCountdown.setText(String.valueOf((int) Math.ceil((double) millisUntilFinished / 1000.0))); // show elapsed time in seconds
            }

            public void onFinish() {
                mVideoCountdown.setText(String.valueOf(0));

                // stop recording and start video edit activity
                mVideoCamera.stopRecording();
                startEditVideoActivity(CameraHelper.getVideoMediaFilePath());
            }
        };

        // Sets the pick video button behaviour
        mPickButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                        MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, RESULT_LOAD_VID);
            }
        });

        // Sets the capture video button behaviour
        mTakeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mIsRecording) {
                    mVideoCaptureCountdownTimer.cancel();

                    // stop recording and start video edit activity
                    mVideoCamera.stopRecording();
                    startEditVideoActivity(CameraHelper.getVideoMediaFilePath());
                } else {
                    // Verifying if there's enough space to store the new video
                    if (CameraHelper.getAvailableDiskSpace(getActivity()) >= MINIMUM_AVAILABLE_SPACE_IN_MEGABYTES_TO_CAPTURE_VIDEO) {
                        if (mVideoCamera.startRecording()) {
                            // inform the user that recording has started
                            mVideoCaptureCountdownTimer.start();
                            mFlipCameraButton.setVisibility(View.GONE);
                            mTakeButton.setText("STOP");
                            mTakeButton.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                            mIsRecording = true;
                        } else {
                            // prepare didn't work, release the camera
                            mVideoCamera.stopRecording();
                            Toast.makeText(getActivity(),
                                    getResources().getString(R.string.error_unable_to_start_video_camera),
                                    Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(getActivity(),
                                getResources().getString(R.string.error_not_enough_available_space),
                                Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
        mTakeButton.setText("CAPTURE");
        mTakeButton.setTextColor(getResources().getColor(android.R.color.holo_green_dark));

        // Update UI
        mCurrentModeTextView.setText("VIDEO MODE");
        mVideoCountdown.setText(Integer.toString(MAXIMUM_VIDEO_DURATION / 1000));
        mVideoCountdown.setVisibility(View.VISIBLE);
        mSelectMediaProvider.setIsPhotoModeOn(false);
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

        if (mSelectMediaProvider.isPhotoModeOn()) {
            releasePhotoCamera();
            createPhotoCamera(mSelectMediaProvider.getCameraId());
        } else {
            releaseVideoCamera();
            createVideoCamera(mSelectMediaProvider.getCameraId());
        }
    }

    /**
     * Initialize PHOTO camera
     *
     * @param cameraId source camera: FRONT or BACK
     */
    private void createPhotoCamera(int cameraId) {
        mPhotoCamera = new PhotoCamera(getActivity(), cameraId);
        mCameraPreviewContainer.addView(mPhotoCamera);
    }

    /**
     * Release PHOTO camera
     */
    private void releasePhotoCamera() {
        if (mPhotoCamera != null) {
            mPhotoCamera.release();
            mCameraPreviewContainer.removeView(mPhotoCamera);
            mPhotoCamera = null;
        }
    }

    /**
     * Initialize VIDEO camera
     *
     * @param cameraId source camera: FRONT or BACK
     */
    private void createVideoCamera(int cameraId) {
        mVideoCamera = new VideoCamera(getActivity(), cameraId, MAXIMUM_VIDEO_DURATION);
        mCameraPreviewContainer.addView(mVideoCamera);
    }

    /**
     * Release VIDEO camera
     */
    private void releaseVideoCamera() {
        if (mVideoCamera != null) {
            mVideoCamera.release();
            mCameraPreviewContainer.removeView(mVideoCamera);
            mVideoCamera = null;
        }
    }

    /**
     * Starts edit image activity.
     */
    private void startEditImageActivity() {
        Intent editImageIntent = new Intent(getActivity(), PhotoEditActivity.class);
        editImageIntent.putExtra(PhotoEditActivity.EXTRA_URI, PhotoUtil.getImageUri());
        releasePhotoCamera();
        startActivity(editImageIntent);
    }

    /**
     * Starts edit video activity.
     */
    private void startEditVideoActivity(String videoPath) {
        Intent editVideoIntent = new Intent(getActivity(), VideoEditActivity.class);
        editVideoIntent.putExtra(VideoEditActivity.EXTRA_VIDEO_PATH, videoPath);
        releaseVideoCamera();
        startActivity(editVideoIntent);
    }
}
