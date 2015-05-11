package com.samsao.snapzi.social;


import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.samsao.snapzi.R;
import com.samsao.snapzi.api.ApiService;
import com.samsao.snapzi.api.entity.CampaignList;
import com.samsao.snapzi.authentication.AuthenticationActivity;
import com.samsao.snapzi.camera.SelectMediaActivity;
import com.samsao.snapzi.edit.VideoPreview;
import com.samsao.snapzi.edit.util.ProgressDialogFragment;
import com.samsao.snapzi.fan_page.FanPageActivity;
import com.samsao.snapzi.util.KeyboardUtil;
import com.samsao.snapzi.util.PreferenceManager;
import com.samsao.snapzi.util.StringUtil;
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


public class ShareFragment extends SocialNetworkFragment implements ProgressDialogFragment.Listener, ShareLoginDialogFragment.ShareDialogListener {
    public final static String SHARE_FRAGMENT_TAG = "com.samsao.snapzi.ShareFragment.PHOTO_DETAILS_FRAGMENT_TAG";
    private final int SHARE_FRAGMENT_REQUEST_CODE = 0;


    @InjectView(R.id.fragment_share_facebook)
    public LinearLayout mFacebookBtn;
    @InjectView(R.id.fragment_share_twitter)
    public LinearLayout mTwitterBtn;
    @InjectView(R.id.fragment_share_gplus)
    public LinearLayout mGooglePlusBtn;
    @InjectView(R.id.fragment_share_comment_editText)
    public EditText mCommentEditText;
    @InjectView(R.id.fragment_share_toolbar)
    public Toolbar mToolbar;
    @InjectView(R.id.fragment_share_video)
    public FrameLayout mVideoContainer;
    private VideoPreview mVideoPreview;
    @InjectView(R.id.fragment_share_image)
    public ImageView mImage;
    @InjectView(R.id.fragment_share_comment_characters_textView)
    public TextView mCommentCharactersCountTextView;

    private Listener mListener;
    private ProgressDialogFragment mProgressDialogFragment;
    private ShareLoginDialogFragment mShareLoginDialogFragment;
    private String mImagePath;
    private String mCommentText;
    private Integer mCampaignId;

    // TODO inject me
    private ApiService mApiService = new ApiService();
    private PreferenceManager mPreferenceManager = new PreferenceManager();
    private UserManager mUserManager = new UserManager(mPreferenceManager);

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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case SHARE_FRAGMENT_REQUEST_CODE:
                dismissProgressDialog();
                if (resultCode == Activity.RESULT_OK) {
                    share();
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
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

        mCommentEditText.setOnEditorActionListener(
                new EditText.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if (actionId == EditorInfo.IME_ACTION_DONE) {
                            KeyboardUtil.hideKeyboard(v);
                            return true;
                        }
                        return false;
                    }
                });
        mCommentEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                int count = mCommentEditText.getText().toString().length();
                if (count == 0) {
                    mCommentCharactersCountTextView.setVisibility(View.INVISIBLE);
                } else {
                    mCommentCharactersCountTextView.setVisibility(View.VISIBLE);
                    mCommentCharactersCountTextView.setText(getResources().getQuantityString(R.plurals.character_plural, count, count));
                }
            }
        });

        initializeSocialNetworks();
        return view;
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
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mListener = (Listener) activity;
            mCampaignId = mListener.getCampaignId();
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
            mListener.setSupportActionBar(mToolbar);
        }
        mListener.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mListener.getSupportActionBar().setDisplayShowTitleEnabled(true);
        mListener.getSupportActionBar().setTitle(StringUtil.getAppFontString(R.string.sharing));
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

    /**
     * Enables a social network button
     *
     * @param linearLayout
     */
    public void enableSocialNetworkBtn(LinearLayout linearLayout) {
        //noinspection deprecation
        linearLayout.setBackgroundDrawable(getResources().getDrawable(R.drawable.sel_app_btn));
        for (int i = 0; i < linearLayout.getChildCount(); i++) {
            ((TextView) linearLayout.getChildAt(i)).setTextColor(getResources().getColor(android.R.color.white));
        }
    }

    /**
     * Disables a social network button
     *
     * @param linearLayout
     */
    public void disableSocialNetworkBtn(LinearLayout linearLayout) {
        //noinspection deprecation
        linearLayout.setBackgroundDrawable(getResources().getDrawable(R.drawable.sel_app_btn_disabled));
        for (int i = 0; i < linearLayout.getChildCount(); i++) {
            ((TextView) linearLayout.getChildAt(i)).setTextColor(getResources().getColor(R.color.medium_gray));
        }
    }

    @OnClick(R.id.fragment_share_share_btn)
    /**
     * When the share button is clicked
     */
    public void onShareBtnClick() {
        mImagePath = mListener.getImagePath();
        mCommentText = mCommentEditText.getText().toString();
        mListener.setCommentText(mCommentText);

        boolean isLogin = mUserManager.isLogged();
        if (!isLogin) {
            showLoginDialog();
        } else {
            share();
        }
    }

    /**
     * Show the login dialog
     */
    private void showLoginDialog() {
        if (mShareLoginDialogFragment == null) {
            mShareLoginDialogFragment = ShareLoginDialogFragment.newInstance(this);
        }
        if (getFragmentManager().findFragmentByTag(ShareLoginDialogFragment.PROMPT_LOGIN_DIALOG_FRAGMENT_TAG) == null) {
            mShareLoginDialogFragment.show(getFragmentManager(), ShareLoginDialogFragment.PROMPT_LOGIN_DIALOG_FRAGMENT_TAG);
        }
    }

    /**
     * Dismiss login dialog
     */
    public void dismissLoginDialog() {
        if (getFragmentManager().findFragmentByTag(ShareLoginDialogFragment.PROMPT_LOGIN_DIALOG_FRAGMENT_TAG) != null) {
            mShareLoginDialogFragment.dismiss();
        }
    }

    /**
     * Share the media on the backend
     */
    public void share() {
        showProgressDialog();
        // TODO add campaign ID
        mApiService.sharePicture(mImagePath, mCommentText, mCampaignId, new retrofit.Callback<com.samsao.snapzi.api.entity.Response>() {
            @Override
            public void success(com.samsao.snapzi.api.entity.Response response, Response response2) {
                dismissProgressDialog();
                // TODO string resource
                Toast.makeText(getActivity(), "Share picture success!", Toast.LENGTH_SHORT).show();
                // TODO load the campaigns in the FanPage Activity
                mApiService.getCampaigns(new retrofit.Callback<CampaignList>() {
                    @Override
                    public void success(CampaignList campaignList, Response response) {
                        FanPageActivity.start(getActivity(), campaignList);
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        SelectMediaActivity.start(getActivity(), mListener.getCampaignId());
                    }
                });
                getActivity().finish();
            }

            @Override
            public void failure(RetrofitError error) {
                dismissProgressDialog();
                // TODO string resource
                Toast.makeText(getActivity(), "Failure sharing picture: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Show progress dialog
     */
    public void showProgressDialog() {
        if (mProgressDialogFragment == null) {
            // FIXME wont work with French
            mProgressDialogFragment = ProgressDialogFragment.newInstance(this, getString(R.string.sharing) + "...");
            mProgressDialogFragment.setCancelable(false);
        }

        if (getFragmentManager().findFragmentByTag(ProgressDialogFragment.PROGRESS_DIALOG_FRAGMENT_TAG) == null) {
            mProgressDialogFragment.show(getFragmentManager(), ProgressDialogFragment.PROGRESS_DIALOG_FRAGMENT_TAG);
        }
    }

    /**
     * Hide progress dialog
     */
    public void dismissProgressDialog() {
        if (getFragmentManager().findFragmentByTag(ProgressDialogFragment.PROGRESS_DIALOG_FRAGMENT_TAG) != null) {
            mProgressDialogFragment.dismiss();
        }
    }

    @Override
    /**
     * When the progress dialog gets cancelled
     */
    public void onProgressDialogCancel() {
        // nothing to do
    }

    @Override
    public void onLoginButtonClick(DialogFragment dialog) {
        Intent intent = new Intent(getActivity(), AuthenticationActivity.class);
        startActivityForResult(intent, SHARE_FRAGMENT_REQUEST_CODE);
        dismissLoginDialog();
    }

    @Override
    public void onCancelButtonClick(DialogFragment dialog) {
        dismissLoginDialog();
    }

    public interface Listener {
        Integer getCampaignId();

        String getMediaType();

        String getImagePath();

        String getVideoPath();

        void setCommentText(String commentText);

        ActionBar getSupportActionBar();

        void setSupportActionBar(Toolbar toolbar);
    }
}
