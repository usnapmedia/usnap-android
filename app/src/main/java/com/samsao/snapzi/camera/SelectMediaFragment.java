package com.samsao.snapzi.camera;

import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.MediaStore;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.samsao.snapzi.R;
import com.samsao.snapzi.api.ApiService;
import com.samsao.snapzi.api.entity.FeedImageList;
import com.samsao.snapzi.edit.EditActivity;
import com.samsao.snapzi.live_feed.LiveFeedAdapter;
import com.samsao.snapzi.util.PhotoUtil;
import com.samsao.snapzi.util.WindowUtil;
import com.squareup.picasso.Picasso;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;


/**
 * @author vlegault
 * @since 15-03-17
 */
public class SelectMediaFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    /**
     * Constants
     */
    private final String LOG_TAG = getClass().getSimpleName();
    private static final int URI_LOADER = 0;

    private String mImageLocation = "";

    private SelectMediaProvider mSelectMediaProvider;
    private boolean mIsCapturingMedia, mIsCapturingVideo;
    private CountDownTimer mVideoCaptureCountdownTimer;
    private Dialog mPickMediaDialog;

    @InjectView(R.id.fragment_select_media_livefeed_recycler_view)
    public RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private LiveFeedAdapter mLiveFeedAdapter;
    private ApiService mApiService = new ApiService();

    @InjectView(R.id.fragment_select_media_latest_image)
    public ImageView mImageView;

    @InjectView(R.id.fragment_select_media_camera_preview_container)
    public FrameLayout mCameraPreviewContainer;
    private CameraPreview mCameraPreview;

    @InjectView(R.id.fragment_select_media_flash_setup_button)
    public Button mFlashSetupButton;

    @InjectView(R.id.fragment_select_media_flip_camera_button)
    public Button mFlipCameraButton;

    @InjectView(R.id.fragment_select_media_video_countdown)
    public TextView mVideoCountdown;

    @InjectView(R.id.fragment_select_media_capture_media_button)
    public ProgressButton mCaptureMediaButton;


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
        initLiveFeed();
        getLoaderManager().initLoader(URI_LOADER, null, this);
        return view;
    }

    public void initLiveFeed() {
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mLiveFeedAdapter = new LiveFeedAdapter();
        mRecyclerView.setAdapter(mLiveFeedAdapter);
        getFeedImage();
    }

    public void getFeedImage() {
        mApiService.getLiveFeed(new Callback<FeedImageList>() {
            @Override
            public void success(FeedImageList feedImageList, Response response) {
                mLiveFeedAdapter.setImageLiveFeed(feedImageList.getResponse());
            }

            @Override
            public void failure(RetrofitError error) {
                Timber.e("Error Fetching Images!");
            }
        });
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
            initializeCamera();
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
        mFlashSetupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setCameraFlashMode(mCameraPreview.getNextAvailableFlashMode());
            }
        });

        // Camera flip button
        // Activate camera flipping function only if more than one camera is available
        mFlipCameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSelectMediaProvider.setCameraId(mCameraPreview.flip());
            }
        });

        // Pick media from gallery
        mImageView.setOnClickListener(new View.OnClickListener() {
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
                        mCameraPreview.takePicture();
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
                    boolean isVideoCaptureSuccessful = mCameraPreview.stopRecording();
                    mVideoCaptureCountdownTimer.cancel();
                    hideAllSettingsButtons();

                    if (isVideoCaptureSuccessful) {
                        mSelectMediaProvider.startEditActivity(EditActivity.VIDEO_MODE, CameraHelper.getDefaultVideoFilePath());
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
                mCaptureMediaButton.setProgress(1.0f);
                mVideoCountdown.setText(String.valueOf(0));

                // stop recording and start video edit activity
                boolean isVideoCaptureSuccessful = mCameraPreview.stopRecording();
                if (isVideoCaptureSuccessful) {
                    mSelectMediaProvider.startEditActivity(EditActivity.VIDEO_MODE, CameraHelper.getDefaultVideoFilePath());
                } else {
                    // Video capture didn't work
                    Toast.makeText(getActivity(),
                            getResources().getString(R.string.error_video_capture_failed),
                            Toast.LENGTH_LONG).show();
                    triggerCapturingVideo(false);
                }
            }
        };
        mCaptureMediaButton.setVisibility(View.VISIBLE);
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
            mVideoCountdown.setVisibility(View.GONE);
            WindowUtil.unlockScreenOrientation(getActivity());
            mIsCapturingMedia = mIsCapturingVideo = false;
        }
        setFlashButtonVisibility(isCapturingMedia);
        setFlipButtonVisiblity(isCapturingMedia);
        mCaptureMediaButton.setVisibility(View.VISIBLE);
    }

    /**
     * Set flash button visibility
     *
     * @param isCapturingMedia
     */
    private void setFlashButtonVisibility(boolean isCapturingMedia) {
        if (!isCapturingMedia && mCameraPreview.isFlashAvailable()) {
            setCameraFlashMode(mSelectMediaProvider.getCameraFlashMode());
            mFlashSetupButton.setVisibility(View.VISIBLE);
        } else {
            mFlashSetupButton.setVisibility(View.GONE);
        }
    }

    /**
     * Set the flip button visibility
     *
     * @param isCapturingMedia
     */
    private void setFlipButtonVisiblity(boolean isCapturingMedia) {
        if (!isCapturingMedia && mCameraPreview.hasFrontCamera()) {
            mFlipCameraButton.setVisibility(View.VISIBLE);
        } else {
            mFlipCameraButton.setVisibility(View.GONE);
        }
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
     */
    public void initializeCamera() {
        if (mCameraPreview == null) {
            mCameraPreview = CameraPreview.getNewInstance(getActivity())
                    .setLayoutMode(CameraPreview.LayoutMode.CENTER_CROP)
                    .setCameraId(mSelectMediaProvider.getCameraId())
                    .setMaximumVideoDuration_ms(SelectMediaActivity.MAXIMUM_VIDEO_DURATION_MS)
                    .setOnCameraPreviewReady(new CameraPreview.SimpleCameraCallback() {
                        private final ViewTreeObserver.OnGlobalLayoutListener flashButtonGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
                            @Override
                            public void onGlobalLayout() {
                                setFlashButtonVisibility(false);
                                // Controls were initialize, stop listening for their creation
                                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                                    mFlashSetupButton.getViewTreeObserver().removeOnGlobalLayoutListener(flashButtonGlobalLayoutListener);
                                } else {
                                    //noinspection deprecation
                                    mFlashSetupButton.getViewTreeObserver().removeGlobalOnLayoutListener(flashButtonGlobalLayoutListener);
                                }
                            }
                        };

                        private final ViewTreeObserver.OnGlobalLayoutListener flipButtonGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
                            @Override
                            public void onGlobalLayout() {
                                setFlipButtonVisiblity(false);
                                // Controls were initialize, stop listening for their creation
                                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                                    mFlipCameraButton.getViewTreeObserver().removeOnGlobalLayoutListener(flipButtonGlobalLayoutListener);
                                } else {
                                    //noinspection deprecation
                                    mFlipCameraButton.getViewTreeObserver().removeGlobalOnLayoutListener(flipButtonGlobalLayoutListener);
                                }
                            }
                        };

                        @Override
                        public void onCameraPreviewReady() {
                            triggerCapturingMediaState(false);
                            setCameraFlashMode(mSelectMediaProvider.getCameraFlashMode());
                            mFlashSetupButton.getViewTreeObserver().addOnGlobalLayoutListener(flashButtonGlobalLayoutListener);
                            mFlipCameraButton.getViewTreeObserver().addOnGlobalLayoutListener(flipButtonGlobalLayoutListener);
                            mSelectMediaProvider.setCameraPreviewAspectRatio(mCameraPreview.getPreviewAspectRatio());
                        }

                        @Override
                        public void onCameraPreviewFailed() {
                            Log.e(LOG_TAG, "Camera init failed");
                            //FIXME
                        }

                        @Override
                        public void onPictureReady(Bitmap image) {
                            mSelectMediaProvider.saveImageAndStartEditActivity(image, CameraHelper.getDefaultImageFilePath());
                        }
                    })
                    .into(mCameraPreviewContainer);
        }
    }

    /**
     * Release camera
     */
    public void releaseCamera() {
        if (mCameraPreview != null) {
            mCameraPreview.release();
            mCameraPreview = null;
        }
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
     * Display the latest image from gallery.
     */
    public void initLatestImage(String imageLocation) {
        if (imageLocation != "") {
            Picasso.with(getActivity()).load(imageLocation).fit().centerCrop().into(mImageView);
        } else {
            Log.e(LOG_TAG, "Image not found");
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
     * Create a dialog that asks what kind of media to pick from gallery.
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

    @Override
    public Loader<Cursor> onCreateLoader(int loaderID, Bundle bundle) {
        String[] mProjection = new String[]{
                MediaStore.Images.ImageColumns._ID,
                MediaStore.Images.ImageColumns.DATA,
                MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME,
                MediaStore.Images.ImageColumns.DATE_TAKEN,
                MediaStore.Images.ImageColumns.MIME_TYPE
        };

        switch (loaderID) {
            case URI_LOADER:
                return new CursorLoader(getActivity(), MediaStore.Images.Media.EXTERNAL_CONTENT_URI, mProjection, null, null, MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC");
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        if (cursor.moveToFirst()) {
            mImageLocation = cursor.getString(1);
            mImageLocation = "file://" + mImageLocation;
            initLatestImage(mImageLocation);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
    }
}
