package com.samsao.snapzi.camera;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

import com.samsao.snapzi.R;
import com.samsao.snapzi.preferences.PreferencesActivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

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

    private CameraProvider mCameraProvider;
    private CameraPreview mCameraPreview;

    @InjectView(R.id.fragment_select_media_camera_preview_container)
    public FrameLayout mCameraPreviewContainer;

    @InjectView(R.id.fragment_select_media_flip_camera_button)
    public Button mFlipCameraButton;

    @InjectView(R.id.fragment_select_media_take_picture_button)
    public Button mTakePictureButton;

    @InjectView(R.id.fragment_select_media_pref_button)
    public Button mPreferenceButton;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_select_media, container, false);
        ButterKnife.inject(this, view);

        setFlipCameraButton();
        setTakePictureButton();
        setPreferenceButton();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        createCameraPreviewSurfaceView(mCameraProvider.getCameraId());
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
            mCameraProvider = (CameraProvider) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement CameraProvider");
        }
    }

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

    private void setTakePictureButton() {
        mTakePictureButton.setOnClickListener(new View.OnClickListener() {
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

    private void setPreferenceButton() {
        mPreferenceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), PreferencesActivity.class));
            }
        });
    }

    public void flipCamera() {
        releaseCameraPreviewSurfaceView();

        if (mCameraProvider.getCameraId() == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            mCameraProvider.setCameraId(Camera.CameraInfo.CAMERA_FACING_BACK);
        } else {
            mCameraProvider.setCameraId(Camera.CameraInfo.CAMERA_FACING_FRONT);
        }

        createCameraPreviewSurfaceView(mCameraProvider.getCameraId());
    }

    private void createCameraPreviewSurfaceView(int cameraId) {
        mCameraPreview = new CameraPreview(getActivity(), cameraId);
        mCameraPreviewContainer.addView(mCameraPreview);
    }

    private void releaseCameraPreviewSurfaceView() {
        mCameraPreview.releaseCamera();
        mCameraPreviewContainer.removeView(mCameraPreview); // This is necessary.
        mCameraPreview = null;
    }

    private final Camera.ShutterCallback mShutterCallback = new Camera.ShutterCallback() {
        public void onShutter() {
            AudioManager mgr = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
            mgr.playSoundEffect(AudioManager.FLAG_PLAY_SOUND);
        }
    };

    private Camera.PictureCallback mJpegCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {

            File pictureFile = CameraUtils.getOutputMediaFile(CameraUtils.MEDIA_TYPE_IMAGE);
            if (pictureFile == null) {
                Log.d(LOG_TAG, "Error creating media file, check storage permissions");
                return;
            }

            try {
                FileOutputStream fos = new FileOutputStream(pictureFile);
                fos.write(data);
                fos.close();
            } catch (FileNotFoundException e) {
                Log.d(LOG_TAG, "File not found: " + e.getMessage());
            } catch (IOException e) {
                Log.d(LOG_TAG, "Error accessing file: " + e.getMessage());
            }

            mCameraPreview.getCamera().startPreview();
        }
    };
}
