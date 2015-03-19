package com.samsao.snapzi.camera;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.samsao.snapzi.R;
import com.samsao.snapzi.preferences.PreferencesActivity;

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
    private final static int RESULT_LOAD_IMG = 8401;

    private SelectMediaProvider mSelectMediaProvider;
    private CameraPreview mCameraPreview;

    @InjectView(R.id.fragment_select_media_current_mode)
    public TextView mCurrentModeTextView;

    @InjectView(R.id.fragment_select_media_camera_preview_container)
    public FrameLayout mCameraPreviewContainer;

    @InjectView(R.id.fragment_select_media_flip_camera_button)
    public Button mFlipCameraButton;

    @InjectView(R.id.fragment_select_media_pick_button)
    public Button mPickButton;

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
            Bitmap image = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            // TODO: fix bitmap rotation: http://stackoverflow.com/questions/11674816/android-image-orientation-issue-with-custom-camera-activity
            startEditImageActivity(image);

            mCameraPreview.getCamera().startPreview(); // TODO: line to be deleted
        }
    };


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_select_media, container, false);
        ButterKnife.inject(this, view);

        setBaseButtons();
        if (mSelectMediaProvider.isPhotoModeOn()) {
            setPhotoMode();
        } else {
            setVideoMode();
        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        createCameraPreviewSurfaceView(mSelectMediaProvider.getCameraId());
    }

    @Override
    public void onPause() {
        super.onPause();
        releaseCameraPreviewSurfaceView();
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

        try {
            // When an Image is picked
            if (requestCode == RESULT_LOAD_IMG
                    && resultCode == Activity.RESULT_OK
                    && null != data) {

                // Get the Image from data
                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};

                Cursor cursor = getActivity().getContentResolver().query(
                        selectedImage, filePathColumn, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String filePath = cursor.getString(columnIndex);
                cursor.close();

                Bitmap image = BitmapFactory.decodeFile(filePath);
                startEditImageActivity(image);
            }
        } catch (Exception e) {
            Toast.makeText(getActivity(),
                    getResources().getString(R.string.error_unable_to_open_image),
                    Toast.LENGTH_LONG).show();
            Log.e(LOG_TAG, "An error happened while trying to open an image: " + e.getMessage());
        }
    }

    private void setPhotoMode() {
        setFlipCameraButton();
        setPickPictureButton();
        setTakePictureButton();
        mCurrentModeTextView.setText("PHOTO MODE");
        mSelectMediaProvider.setIsPhotoModeOn(true);
    }

    private void setVideoMode() {
        setFlipCameraButton();
        setPickVideoButton();
        setTakeVideoButton();
        mCurrentModeTextView.setText("VIDEO MODE");
        mSelectMediaProvider.setIsPhotoModeOn(false);
    }

    /**
     * Sets the flip camera button behaviour.
     * Depending on how many camera are available on the current device, this function sets the flip
     * camera button visibility and the callback to allow camera flipping (FRONT, BACK).
     */
    private void setFlipCameraButton() {
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
    }

    /**
     * Sets the pick picture button behaviour.
     * Sets a callback function to open the user's image gallery.
     */
    private void setPickPictureButton() {
        mPickButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                releaseCameraPreviewSurfaceView();

                Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
            }
        });
    }

    private void setPickVideoButton() {
        mPickButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO
            }
        });
    }

    /**
     * Sets the take picture button behaviour.
     * Sets a callback function to autofocus the camera and then take a picture right afterwards.
     */
    private void setTakePictureButton() {
        mTakeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCameraPreview.getCamera().autoFocus(new Camera.AutoFocusCallback() {
                    @Override
                    public void onAutoFocus(boolean b, Camera camera) {
                        mCameraPreview.getCamera().takePicture(mShutterCallback, null, mJpegCallback);
                    }
                });
            }
        });
    }

    private void setTakeVideoButton() {
        mTakeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO
            }
        });
    }

    private void setBaseButtons() {
        mTriggerPhotoModeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setPhotoMode();
            }
        });

        mTriggerVideoModeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setVideoMode();
            }
        });

        mPreferenceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                releaseCameraPreviewSurfaceView();

                startActivity(new Intent(getActivity(), PreferencesActivity.class));
            }
        });
    }

    /**
     * If more the one camera is available on the current device, this function switches the camera
     * source (FRONT, BACK).
     */
    public void flipCamera() {
        releaseCameraPreviewSurfaceView();

        if (mSelectMediaProvider.getCameraId() == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            mSelectMediaProvider.setCameraId(Camera.CameraInfo.CAMERA_FACING_BACK);
        } else {
            mSelectMediaProvider.setCameraId(Camera.CameraInfo.CAMERA_FACING_FRONT);
        }

        createCameraPreviewSurfaceView(mSelectMediaProvider.getCameraId());
    }

    /**
     * Initialise camera
     *
     * @param cameraId source camera: FRONT or BACK
     */
    private void createCameraPreviewSurfaceView(int cameraId) {
        mCameraPreview = new CameraPreview(getActivity(), cameraId);
        mCameraPreviewContainer.addView(mCameraPreview);
    }

    /**
     * Release camera
     */
    private void releaseCameraPreviewSurfaceView() {
        if (mCameraPreview != null) {
            mCameraPreview.releaseCamera();
            mCameraPreviewContainer.removeView(mCameraPreview); // This is necessary.
            mCameraPreview = null;
        }
    }

    /**
     * Starts edit image activity.
     *
     * @param image bitmap image to edit
     */
    private void startEditImageActivity(Bitmap image) {

        //Intent editImageIntent = new Intent();
        //editImageIntent.putExtra("data", image);
        //releaseCameraPreviewSurfaceView();

        // TODO: part moi ca el gros
        Toast.makeText(getActivity(),
                "edit image : pars moi ca el gros",
                Toast.LENGTH_LONG).show();
        //startActivity(editImageIntent, EditImageActivity.class));
    }
}
