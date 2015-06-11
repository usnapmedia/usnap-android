package com.samsao.snapzi.fan_page;


import android.app.Activity;
import android.app.Fragment;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.samsao.snapzi.R;
import com.samsao.snapzi.SnapziApplication;
import com.samsao.snapzi.api.ApiService;
import com.samsao.snapzi.api.entity.Response;
import com.samsao.snapzi.edit.util.ProgressDialogFragment;
import com.squareup.picasso.Picasso;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RetrofitError;


public class PhotoDetailsFragment extends Fragment implements ReportImageDialogFragment.Listener,
        ProgressDialogFragment.Listener {
    public final static String PHOTO_DETAILS_FRAGMENT_TAG = "com.samsao.snapzi.fan_page.PhotoDetailsFragment.PHOTO_DETAILS_FRAGMENT_TAG";
    private final String PROGRESS_DIALOG_FRAGMENT_TAG = "com.samsao.snapzi.fan_page.PhotoDetailsFragment.PROGRESS_DIALOG_FRAGMENT_TAG";

    @InjectView(R.id.activity_photo_detail_first_letter_id)
    public TextView mFirstLetterTextView;
    @InjectView(R.id.activity_photo_detail_user_name_id)
    public TextView mUserNameTextView;
    @InjectView(R.id.activity_photo_detail_time_id)
    public TextView mTimeTextView;
    @InjectView(R.id.activity_photo_detail_image)
    public ImageView mImageView;
    @InjectView(R.id.activity_photo_detail_description_id)
    public TextView mDescriptionTextView;
    @InjectView(R.id.activity_photo_detail_toolbar)
    public Toolbar mToolbar;
    @InjectView(R.id.activity_photo_detail_social_network_facebook)
    public TextView mFacebookTextView;
    @InjectView(R.id.activity_photo_detail_social_network_twitter)
    public TextView mTwitterTextView;
    @InjectView(R.id.activity_photo_detail_social_network_google_plus)
    public TextView mGooglePlusTextView;
    @InjectView(R.id.activity_photo_detail_image_video_layout)
    public FrameLayout mImageVideoLayout;
    @InjectView(R.id.activity_photo_detail_report)
    public TextView mReportTextView;
    @InjectView(R.id.activity_photo_detail_video)
    public VideoView mVideoView;

    private Listener mListener;
    // TODO inject me
    private ApiService mApiService = new ApiService();

    private ProgressDialogFragment mProgressDialog;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment PhotoDetailsFragment.
     */
    public static PhotoDetailsFragment newInstance() {
        PhotoDetailsFragment fragment = new PhotoDetailsFragment();
        return fragment;
    }

    public PhotoDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_photo_detail, container, false);
        ButterKnife.inject(this, view);
        setupToolbar();

        showProgressDialog();
        if (hasVideo()) {
            // set the right report button label
            mReportTextView.setText(getString(R.string.report_video));
        } else {
            Picasso.with(getActivity()).load(mListener.getPhotoPath()).into(mImageView, new com.squareup.picasso.Callback() {
                @Override
                public void onSuccess() {
                    dismissProgressDialog();
                }

                @Override
                public void onError() {
                    dismissProgressDialog();
                    mImageVideoLayout.setVisibility(View.INVISIBLE);
                    Toast.makeText(getActivity(), getString(R.string.error_loading_image), Toast.LENGTH_SHORT).show();
                }
            });
        }
        // FIXME
        if (!TextUtils.isEmpty(mListener.getUsername())) {
            mUserNameTextView.setText(mListener.getUsername());
            mFirstLetterTextView.setText(mListener.getUsername().substring(0, 1));
        }
        // FIXME
        if (!TextUtils.isEmpty(mListener.getText())) {
            mDescriptionTextView.setText(mListener.getText());
        }
        // TODO set time textview
        // TODO hide social networks
        Typeface fontawesome = Typeface.createFromAsset(SnapziApplication.getContext().getAssets(), "fonts/fontawesome.ttf");
        mFacebookTextView.setTypeface(fontawesome);
        mTwitterTextView.setTypeface(fontawesome);
        mGooglePlusTextView.setTypeface(fontawesome);

        Typeface fontText = Typeface.createFromAsset(SnapziApplication.getContext().getAssets(), "fonts/GothamHTF-Book.ttf");
        mUserNameTextView.setTypeface(fontText);
        mFirstLetterTextView.setTypeface(fontText);
        mDescriptionTextView.setTypeface(fontText);
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
                    + " must implement PhotoDetailsFragment.Listener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (hasVideo()) {
            // load the video
            mImageView.setVisibility(View.GONE);
            mVideoView.setVisibility(View.VISIBLE);
            playVideo();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (hasVideo()) {
            mVideoView.stopPlayback();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setupToolbar() {
        if (mToolbar != null) {
            mListener.setSupportActionBar(mToolbar);
        }
        mListener.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mListener.getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    @OnClick(R.id.activity_photo_detail_report)
    public void reportImage() {
        ReportImageDialogFragment.newInstance(this, hasVideo() ? getString(R.string.confirm_report_video) : getString(R.string.confirm_report_image)).show(getFragmentManager(), "REPORT");
    }

    @Override
    public void onReportImageConfirmation() {
        mApiService.reportImage(mListener.getId(), new Callback<Response>() {
            @Override
            public void success(com.samsao.snapzi.api.entity.Response response, retrofit.client.Response response2) {
                Toast.makeText(getActivity(), hasVideo() ? getString(R.string.success_report_video) : getString(R.string.success_report_image), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Show ProgressDialog
     */
    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = ProgressDialogFragment.newInstance(this);
            mProgressDialog.setCancelable(false);
        }

        if (getFragmentManager().findFragmentByTag(PROGRESS_DIALOG_FRAGMENT_TAG) == null) {
            mProgressDialog.show(getFragmentManager(), PROGRESS_DIALOG_FRAGMENT_TAG);
        }
    }

    /**
     * Hide ProgressDialog
     */
    public void dismissProgressDialog() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }

    /**
     * Returns true if the fragment is showing details for a video
     *
     * @return
     */
    public boolean hasVideo() {
        return !TextUtils.isEmpty(mListener.getVideoPath());
    }

    /**
     * Plays the video
     */
    private void playVideo() {
        try {
            getActivity().getWindow().setFormat(PixelFormat.TRANSLUCENT);
            MediaController mediaController = new MediaController(getActivity());
            mediaController.setAnchorView(mVideoView);

            Uri video = Uri.parse(mListener.getVideoPath());
            mVideoView.setMediaController(mediaController);
            mVideoView.setVideoURI(video);
            mVideoView.requestFocus();
            mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                public void onPrepared(MediaPlayer mp) {
                    mp.setLooping(true);
                    dismissProgressDialog();
                    mVideoView.start();
                }
            });
            mVideoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    dismissProgressDialog();
                    mImageVideoLayout.setVisibility(View.INVISIBLE);
                    Toast.makeText(getActivity(), getString(R.string.error_playing_video), Toast.LENGTH_SHORT).show();
                    return true;
                }
            });
        }
        catch(Exception e) {
            dismissProgressDialog();
            mImageVideoLayout.setVisibility(View.INVISIBLE);
            Toast.makeText(getActivity(), getString(R.string.error_playing_video), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onProgressDialogCancel() {

    }

    public interface Listener {
        Integer getId();

        String getPhotoPath();

        String getVideoPath();

        String getText();

        String getUsername();

        void setSupportActionBar(Toolbar toolbar);

        ActionBar getSupportActionBar();
    }
}
