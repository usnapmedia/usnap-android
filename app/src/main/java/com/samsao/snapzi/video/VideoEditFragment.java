package com.samsao.snapzi.video;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.samsao.snapzi.R;



public class VideoEditFragment extends Fragment {


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

        return view;
    }
}
