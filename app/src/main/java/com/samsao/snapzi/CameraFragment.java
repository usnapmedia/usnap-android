package com.samsao.snapzi;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;


/**
 * @author vlegault
 * @since 15-03-17
 */
public class CameraFragment extends Fragment {
    /**
     * Constants
     */
    private final String LOG_TAG = getClass().getSimpleName();

    private CameraPreviewSurfaceView mCameraPreviewSurfaceView;
    private FrameLayout mCameraPreviewLayout;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mCameraPreviewLayout = (FrameLayout) inflater.inflate(R.layout.fragment_camera, container, false);

        return mCameraPreviewLayout;
    }

    @Override
    public void onResume() {
        super.onResume();
        mCameraPreviewSurfaceView = new CameraPreviewSurfaceView(getActivity());
        mCameraPreviewLayout.addView(mCameraPreviewSurfaceView);
    }

    @Override
    public void onPause() {
        super.onPause();
        mCameraPreviewSurfaceView.releaseCamera();
        mCameraPreviewLayout.removeView(mCameraPreviewSurfaceView); // This is necessary.
        mCameraPreviewSurfaceView = null;
    }
}
