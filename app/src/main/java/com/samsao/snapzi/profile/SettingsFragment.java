package com.samsao.snapzi.profile;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ivankocijan.magicviews.views.MagicButton;
import com.ivankocijan.magicviews.views.MagicEditText;
import com.samsao.snapzi.R;
import com.samsao.snapzi.social.OnGooglePlusLoginListener;
import com.samsao.snapzi.social.SocialNetworkFragment;
import com.samsao.snapzi.util.PreferenceManager;
import com.samsao.snapzi.util.StringUtil;
import com.samsao.snapzi.util.UserManager;
import com.sromku.simple.fb.Permission;
import com.sromku.simple.fb.listeners.OnLoginListener;
import com.sromku.simple.fb.listeners.OnLogoutListener;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * @author jingsilu
 * @since 2015-05-05
 */
public class SettingsFragment extends SocialNetworkFragment {
    private PreferenceManager mPreferenceManager = new PreferenceManager();
    private UserManager mUserManager = new UserManager(mPreferenceManager);

    @InjectView(R.id.fragment_settings_name_editText)
    public MagicEditText mName;

    @InjectView(R.id.fragment_settings_birthday_editText)
    public MagicEditText mBirthday;

    @InjectView(R.id.fragment_settings_image_id)
    public ImageView mImageView;

    @InjectView(R.id.fragment_settings_help_center_btn)
    public MagicButton mHelpCenterBtn;

    @InjectView(R.id.fragment_settings_report_a_problem_btn)
    public MagicButton mProblemBtn;

    @InjectView(R.id.fragment_settings_facebook)
    public LinearLayout mFacebookBtn;
    @InjectView(R.id.fragment_settings_twitter)
    public LinearLayout mTwitterBtn;
    @InjectView(R.id.fragment_settings_gplus)
    public LinearLayout mGooglePlusBtn;

    @InjectView(R.id.fragment_settings_toolbar)

    public Toolbar mToolbar;

    private Listener mListener;


    public static SettingsFragment newInstance() {
        SettingsFragment settingsFragment = new SettingsFragment();
        return settingsFragment;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        ButterKnife.inject(this, view);
        setupToolbar();
        initializeSocialNetworks();
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mListener = (Listener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement SettingsFragment.Listener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @OnClick(R.id.fragment_settings_facebook)
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

    @OnClick(R.id.fragment_settings_twitter)
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

    @OnClick(R.id.fragment_settings_gplus)
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
     * Setup the toolbar
     */
    public void setupToolbar() {
        if (mToolbar != null) {
            mListener.setSupportActionBar(mToolbar);
        }
        mListener.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mListener.getSupportActionBar().setDisplayShowTitleEnabled(true);
        mListener.getSupportActionBar().setTitle(StringUtil.getAppFontString(R.string.settings));
    }

    @OnClick(R.id.fragment_settings_save_btn)
    public void saveSettings() {
        //TODO save settings
        Toast.makeText(getActivity(),"TODO save settings",Toast.LENGTH_SHORT);
    }

    @OnClick(R.id.fragment_settings_help_center_btn)
    public void helpCenter() {
        //TODO help center
        Toast.makeText(getActivity(),"TODO help center",Toast.LENGTH_SHORT);
    }

    @OnClick(R.id.fragment_settings_report_a_problem_btn)
    public void reportAProblem() {
        //TODO report a problem
        Toast.makeText(getActivity(),"TODO report a problem",Toast.LENGTH_SHORT);
    }

    @OnClick(R.id.fragment_settings_log_out_btn)
    public void logout() {
         mUserManager.logout();
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
     * Enables a social network button
     *
     * @param linearLayout
     */
    public void enableSocialNetworkBtn(LinearLayout linearLayout) {
        //noinspection deprecation
        linearLayout.setBackgroundDrawable(getResources().getDrawable(R.drawable.sel_app_btn));
        for (int i = 0; i < linearLayout.getChildCount(); i++) {
            ((TextView)linearLayout.getChildAt(i)).setTextColor(getResources().getColor(android.R.color.white));
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
            ((TextView)linearLayout.getChildAt(i)).setTextColor(getResources().getColor(R.color.medium_gray));
        }
    }

    public interface Listener {
        ActionBar getSupportActionBar();
        void setSupportActionBar(Toolbar toolbar);
    }


}
