package com.samsao.snapzi.fan_page;


import android.app.Activity;
import android.app.Fragment;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.samsao.snapzi.R;
import com.samsao.snapzi.SnapziApplication;
import com.squareup.picasso.Picasso;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class PhotoDetailsFragment extends Fragment {
    public final static String PHOTO_DETAILS_FRAGMENT_TAG = "com.samsao.snapzi.fan_page.PhotoDetailsFragment.PHOTO_DETAILS_FRAGMENT_TAG";

    @InjectView(R.id.activity_photo_detail_first_letter_id)
    public TextView mFirstLetterTextView;
    @InjectView(R.id.activity_photo_detail_user_name_id)
    public TextView mUserNameTextView;
    @InjectView(R.id.activity_photo_detail_time_id)
    public TextView mTimeTextView;
    @InjectView(R.id.activity_photo_detail_image_view_id)
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

    private Listener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment LoginFragment.
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

        Picasso.with(getActivity()).load(mListener.getPhotoPath()).into(mImageView);
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
        mListener.getSupportActionBar().setDisplayShowTitleEnabled(true);
    }

    public interface Listener {
        String getPhotoPath();
        String getText();
        String getUsername();
        void setSupportActionBar(Toolbar toolbar);
        ActionBar getSupportActionBar();
    }
}
