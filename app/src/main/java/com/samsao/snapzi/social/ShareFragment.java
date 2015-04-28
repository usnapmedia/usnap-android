package com.samsao.snapzi.social;


import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import com.samsao.snapzi.R;
import com.samsao.snapzi.api.ApiService;
import com.samsao.snapzi.api.entity.Response;
import com.samsao.snapzi.camera.SelectMediaActivity;
import com.samsao.snapzi.edit.VideoPreview;
import com.samsao.snapzi.util.PreferenceManager;
import com.samsao.snapzi.util.UserManager;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;
import com.sromku.simple.fb.Permission;
import com.sromku.simple.fb.listeners.OnLoginListener;
import com.sromku.simple.fb.listeners.OnLogoutListener;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;

import java.io.File;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import retrofit.RetrofitError;


public class ShareFragment extends SocialNetworkFragment {

    @InjectView(R.id.fragment_share_facebook)
    public Switch mFacebookSwitch;
    @InjectView(R.id.fragment_share_twitter)
    public Switch mTwitterSwitch;
    @InjectView(R.id.fragment_share_gplus)
    public Switch mGooglePlusSwitch;
    @InjectView(R.id.fragment_share_comment_editText)
    public EditText mCommentEditText;
    @InjectView(R.id.fragment_share_toolbar)
    public Toolbar mToolbar;
    @InjectView(R.id.fragment_share_video)
    public FrameLayout mVideoContainer;
    private VideoPreview mVideoPreview;
    @InjectView(R.id.fragment_share_image)
    public ImageView mImageContainer;

    private Listener mListener;

    /**
     * Switches onChange listeners
     */
    private CompoundButton.OnCheckedChangeListener mFacebookSwitchOnCheckedChangeListener;
    private CompoundButton.OnCheckedChangeListener mTwitterSwitchOnCheckedChangeListener;
    private CompoundButton.OnCheckedChangeListener mGooglePlusSwitchOnCheckedChangeListener;

    // TODO inject me
    private UserManager mUserManager = new UserManager(new PreferenceManager());

    /**
     * TODO inject me
     */
    private ApiService mApiService = new ApiService();

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment LoginFragment.
     */
    public static ShareFragment newInstance() {
        ShareFragment fragment = new ShareFragment();
        return fragment;
    }

    public ShareFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_share, container, false);
        ButterKnife.inject(this, view);
        setupToolbar();

        // load the image
        Uri imageUri = Uri.fromFile(new File(mListener.getImagePath()));
        Picasso.with(getActivity()).load(imageUri)
                .noPlaceholder()
                .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                .into(mImageContainer);

        mFacebookSwitchOnCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    loginWithFacebook(new OnLoginListener() {
                        @Override
                        public void onLogin() {
                            setFacebookAccessToken();
                            mFacebookSwitch.setChecked(true);
                            // TODO translation
                            Toast.makeText(getActivity(), "Facebook login success", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onNotAcceptingPermissions(Permission.Type type) {
                            removeFacebookAccessToken();
                            mFacebookSwitch.setOnCheckedChangeListener(null);
                            mFacebookSwitch.setChecked(false);
                            mFacebookSwitch.setOnCheckedChangeListener(mFacebookSwitchOnCheckedChangeListener);
                            // TODO translation
                            Toast.makeText(getActivity(), "Facebook login failed: user did not accept permissions", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onThinking() {

                        }

                        @Override
                        public void onException(Throwable throwable) {
                            removeFacebookAccessToken();
                            mFacebookSwitch.setOnCheckedChangeListener(null);
                            mFacebookSwitch.setChecked(false);
                            mFacebookSwitch.setOnCheckedChangeListener(mFacebookSwitchOnCheckedChangeListener);
                            // TODO translation
                            Toast.makeText(getActivity(), "Facebook login failed: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFail(String error) {
                            removeFacebookAccessToken();
                            mFacebookSwitch.setOnCheckedChangeListener(null);
                            mFacebookSwitch.setChecked(false);
                            mFacebookSwitch.setOnCheckedChangeListener(mFacebookSwitchOnCheckedChangeListener);
                            // TODO translation
                            Toast.makeText(getActivity(), "Facebook login failed: " + error, Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    logoutFromFacebook(new OnLogoutListener() {
                        @Override
                        public void onLogout() {

                        }

                        @Override
                        public void onThinking() {

                        }

                        @Override
                        public void onException(Throwable throwable) {

                        }

                        @Override
                        public void onFail(String s) {

                        }
                    });
                }
            }
        };

        mTwitterSwitchOnCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    loginWithTwitter(new Callback<TwitterSession>() {
                        @Override
                        public void success(Result<TwitterSession> twitterSessionResult) {
                            setTwitterAccessToken();
                            // TODO translation
                            Toast.makeText(getActivity(), "Twitter login success", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void failure(TwitterException e) {
                            removeTwitterAccessToken();
                            mTwitterSwitch.setOnCheckedChangeListener(null);
                            mTwitterSwitch.setChecked(false);
                            mTwitterSwitch.setOnCheckedChangeListener(mTwitterSwitchOnCheckedChangeListener);
                            // TODO translation
                            Toast.makeText(getActivity(), "Twitter login failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    logoutFromTwitter();
                }
            }
        };

        mGooglePlusSwitchOnCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    loginWithGooglePlus(new OnGooglePlusLoginListener() {
                        @Override
                        public void onSuccess() {
                            setGooglePlusAccessToken();
                            // TODO translation
                            Toast.makeText(getActivity(), "Google+ login success", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFail() {
                            removeGooglePlusAccessToken();
                            mGooglePlusSwitch.setOnCheckedChangeListener(null);
                            mGooglePlusSwitch.setChecked(false);
                            mGooglePlusSwitch.setOnCheckedChangeListener(mGooglePlusSwitchOnCheckedChangeListener);
                            // TODO translation
                            Toast.makeText(getActivity(), "Google+ login failed", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    disconnectFromGooglePlus();
                }
            }
        };

        initializeSwitches();
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

        if (mListener.getMediaType().equals(ShareActivity.TYPE_VIDEO)) {
            // load the video
            if (mVideoPreview == null) {
                mVideoPreview = new VideoPreview(getActivity(), mListener.getVideoPath());
            }
            mVideoContainer.addView(mVideoPreview);
            mVideoContainer.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mListener.getMediaType().equals(ShareActivity.TYPE_VIDEO)) {
            mVideoContainer.removeView(mVideoPreview);
            mVideoPreview = null;
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mListener = (Listener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement ShareFragment.Listener");
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

    /**
     * Initializes the switches
     */
    protected void initializeSwitches() {
        initializeFacebookSwitch();
        initializeTwitterSwitch();
        initializeGooglePlusSwitch();
    }

    /**
     * Initializes the Facebook switch
     */
    protected void initializeFacebookSwitch() {
        mFacebookSwitch.setChecked(isFacebookConnected());
        mFacebookSwitch.setOnCheckedChangeListener(mFacebookSwitchOnCheckedChangeListener);
    }

    /**
     * Initializes the Twitter switch
     */
    protected void initializeTwitterSwitch() {
        mTwitterSwitch.setChecked(isTwitterConnected());
        mTwitterSwitch.setOnCheckedChangeListener(mTwitterSwitchOnCheckedChangeListener);
    }

    /**
     * Initializes the Google+ switch
     */
    protected void initializeGooglePlusSwitch() {
        mGooglePlusSwitch.setChecked(!TextUtils.isEmpty(mUserManager.getGooglePlusAccessToken()));
        mGooglePlusSwitch.setOnCheckedChangeListener(mGooglePlusSwitchOnCheckedChangeListener);
    }

    /**
     * Setup the toolbar
     */
    public void setupToolbar() {
        if (mToolbar != null) {
            ((ActionBarActivity) getActivity()).setSupportActionBar(mToolbar);
        }
        ((ActionBarActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((ActionBarActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    @OnClick(R.id.fragment_share_share_btn)
    public void share() {
        // TODO show loading dialog
        mApiService.sharePicture(mListener.getImagePath(), mCommentEditText.getText().toString(), new retrofit.Callback<Response>() {
            @Override
            public void success(Response response, retrofit.client.Response response2) {
                // TODO translation
                Toast.makeText(getActivity(), "Share picture success!", Toast.LENGTH_SHORT).show();
                SelectMediaActivity.start(getActivity());
                getActivity().finish();
            }

            @Override
            public void failure(RetrofitError error) {
                // TODO translation
                Toast.makeText(getActivity(), "Failure sharing picture: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public interface Listener {
        String getMediaType();

        String getImagePath();

        String getVideoPath();
    }
}
