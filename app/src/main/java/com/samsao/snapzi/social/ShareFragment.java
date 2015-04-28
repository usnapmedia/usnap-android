package com.samsao.snapzi.social;


import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.samsao.snapzi.R;
import com.samsao.snapzi.api.ApiService;
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
import retrofit.client.Response;


public class ShareFragment extends SocialNetworkFragment {

    @InjectView(R.id.fragment_share_facebook)
    public Button mFacebookBtn;
    @InjectView(R.id.fragment_share_twitter)
    public Button mTwitterBtn;
    @InjectView(R.id.fragment_share_gplus)
    public Button mGooglePlusBtn;
    @InjectView(R.id.fragment_share_comment_editText)
    public EditText mCommentEditText;
    @InjectView(R.id.fragment_share_toolbar)
    public Toolbar mToolbar;
    @InjectView(R.id.fragment_share_video)
    public FrameLayout mVideoContainer;
    private VideoPreview mVideoPreview;
    @InjectView(R.id.fragment_share_image)
    public ImageView mImage;

    private Listener mListener;

    // TODO inject me
    private UserManager mUserManager = new UserManager(new PreferenceManager());
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
                .into(mImage);

        initializeSocialNetworks();
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
     * Initializes the social network buttons
     */
    protected void initializeSocialNetworks() {
        setFacebookBtn(isFacebookConnected());
        setTwitterBtn(isTwitterConnected());
        setGooglePlusBtn(isGooglePlusConnected());
    }

    /**
     * Set the Facebook Button
     */
    protected void setFacebookBtn(boolean connected) {
        if (connected) {
            enableSocialNetworkBtn(mFacebookBtn);
        } else {
            disableSocialNetworkBtn(mFacebookBtn);
        }
    }

    /**
     * Set the Twitter Button
     */
    protected void setTwitterBtn(boolean connected) {
        if (connected) {
            enableSocialNetworkBtn(mTwitterBtn);
        } else {
            disableSocialNetworkBtn(mTwitterBtn);
        }
    }

    /**
     * Set the Google+ Button
     */
    protected void setGooglePlusBtn(boolean connected) {
        if (connected) {
            enableSocialNetworkBtn(mGooglePlusBtn);
        } else {
            disableSocialNetworkBtn(mGooglePlusBtn);
        }
    }

    /**
     * Setup the toolbar
     */
    public void setupToolbar() {
        if (mToolbar != null) {
            ((ActionBarActivity) getActivity()).setSupportActionBar(mToolbar);
        }
        ((ActionBarActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((ActionBarActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(true);
    }


    /**
     * Toggle Facebook ON/OFF
     */
    @OnClick(R.id.fragment_share_facebook)
    public void toggleFacebook() {
        if (!isFacebookConnected()) {
            loginWithFacebook(new OnLoginListener() {
                @Override
                public void onLogin() {
                    setFacebookAccessToken();
                    setFacebookBtn(true);
                    // TODO translation
                    Toast.makeText(getActivity(), "Facebook login success", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onNotAcceptingPermissions(Permission.Type type) {
                    removeFacebookAccessToken();
                    setFacebookBtn(false);
                    // TODO translation
                    Toast.makeText(getActivity(), "Facebook login failed: user did not accept permissions", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onThinking() {

                }

                @Override
                public void onException(Throwable throwable) {
                    removeFacebookAccessToken();
                    setFacebookBtn(false);
                    // TODO translation
                    Toast.makeText(getActivity(), "Facebook login failed: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFail(String error) {
                    removeFacebookAccessToken();
                    setFacebookBtn(false);
                    // TODO translation
                    Toast.makeText(getActivity(), "Facebook login failed: " + error, Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            logoutFromFacebook(new OnLogoutListener() {
                @Override
                public void onLogout() {
                    setFacebookBtn(false);
                }

                @Override
                public void onThinking() {

                }

                @Override
                public void onException(Throwable throwable) {
                    setFacebookBtn(true);
                    // TODO translation
                    Toast.makeText(getActivity(), "Facebook logout failed: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFail(String s) {
                    setFacebookBtn(true);
                    // TODO translation
                    Toast.makeText(getActivity(), "Facebook logout failed: " + s, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    /**
     * Toggle Twitter ON/OFF
     */
    @OnClick(R.id.fragment_share_twitter)
    public void toggleTwitter() {
        if (!isTwitterConnected()) {
            loginWithTwitter(new Callback<TwitterSession>() {
                @Override
                public void success(Result<TwitterSession> twitterSessionResult) {
                    setTwitterAccessToken();
                    setTwitterBtn(true);
                    // TODO translation
                    Toast.makeText(getActivity(), "Twitter login success", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void failure(TwitterException e) {
                    removeTwitterAccessToken();
                    setTwitterBtn(false);
                    // TODO translation
                    Toast.makeText(getActivity(), "Twitter login failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            logoutFromTwitter();
            setTwitterBtn(false);
        }
    }

    /**
     * Toggle G+ ON/OFF
     */
    @OnClick(R.id.fragment_share_gplus)
    public void toggleGooglePlus() {
        if (!isGooglePlusConnected()) {
            loginWithGooglePlus(new OnGooglePlusLoginListener() {
                @Override
                public void onSuccess() {
                    setGooglePlusAccessToken();
                    setGooglePlusBtn(true);
                    // TODO translation
                    Toast.makeText(getActivity(), "Google+ login success", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFail() {
                    removeGooglePlusAccessToken();
                    setGooglePlusBtn(false);
                    // TODO translation
                    Toast.makeText(getActivity(), "Google+ login failed", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            disconnectFromGooglePlus();
            setGooglePlusBtn(false);
        }
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

    /**
     * Enables a social network button
     *
     * @param btn
     */
    public void enableSocialNetworkBtn(Button btn) {
        //noinspection deprecation
        btn.setBackgroundDrawable(getResources().getDrawable(R.drawable.sel_app_btn));
        btn.setTextColor(getResources().getColor(android.R.color.white));
    }

    /**
     * Disables a social network button
     * @param btn
     */
    public void disableSocialNetworkBtn(Button btn) {
        //noinspection deprecation
        btn.setBackgroundDrawable(getResources().getDrawable(R.drawable.sel_app_btn));
        btn.setTextColor(getResources().getColor(R.color.medium_gray));
    }


    @OnClick(R.id.fragment_share_share_btn)
    public void share() {
        // TODO show loading dialog
        mApiService.sharePicture(mListener.getImagePath(), mCommentEditText.getText().toString(), new retrofit.Callback<com.samsao.snapzi.api.entity.Response>() {
            @Override
            public void success(com.samsao.snapzi.api.entity.Response response, Response response2) {
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
