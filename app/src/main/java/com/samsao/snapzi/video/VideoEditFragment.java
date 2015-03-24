package com.samsao.snapzi.video;


import android.app.Fragment;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.VideoView;

import com.samsao.snapzi.R;
import com.samsao.snapzi.camera.CameraHelper;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class VideoEditFragment extends Fragment {

    @InjectView(R.id.fragment_video_edit_video_preview_container)
    public VideoView mVideoPreviewContainer;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment VideoEditFragment.
     */
    public static VideoEditFragment newInstance() {
        VideoEditFragment fragment = new VideoEditFragment();
        return fragment;
    }

    public VideoEditFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_video_edit, container, false);
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
        mVideoPreviewContainer.setVideoPath(CameraHelper.getVideoMediaFilePath());
        mVideoPreviewContainer.setOnPreparedListener (new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
            }
        });
        mVideoPreviewContainer.start();
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}
