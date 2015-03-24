package com.samsao.snapzi.video;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.samsao.snapzi.R;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class VideoEditFragment extends Fragment {

    /**
     * Constants
     */
    private final VideoPreview.LayoutMode DEFAULT_VIDEO_PREVIEW_LAYOUT = VideoPreview.LayoutMode.CenterCrop;

    private VideoPreview mVideoPreview;

    @InjectView(R.id.fragment_video_edit_video_preview_container)
    public FrameLayout mVideoPreviewContainer;

    private Listener mListener;


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
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mListener = (Listener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement VideoEditFragment.Listener");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mVideoPreview = new VideoPreview(getActivity(), DEFAULT_VIDEO_PREVIEW_LAYOUT, mListener.getVideoPath());
        mVideoPreviewContainer.addView(mVideoPreview);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mVideoPreview != null) {
            mVideoPreviewContainer.removeView(mVideoPreview);
            mVideoPreview = null;
        }
    }

    public interface Listener {
        public String getVideoPath();
    }
}
