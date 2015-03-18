package com.samsao.snapzi.camera;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

import com.samsao.snapzi.R;
import com.samsao.snapzi.preferences.PreferencesActivity;

import butterknife.ButterKnife;
import butterknife.InjectView;


/**
 * @author vlegault
 * @since 15-03-17
 */
public class SelectMediaFragment extends Fragment {

    private CameraProvider mCameraProvider;
    private CameraPreview mCameraPreview;

    @InjectView(R.id.fragment_select_media_camera_preview_container)
    public FrameLayout mCameraPreviewContainer;

    @InjectView(R.id.fragment_select_media_flip_camera_button)
    public Button mFlipCameraButton;

    @InjectView(R.id.fragment_select_media_pref_button)
    public Button mPreferenceButton;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_select_media, container, false);
        ButterKnife.inject(this, view);

        setFlipCameraButton();
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
}
