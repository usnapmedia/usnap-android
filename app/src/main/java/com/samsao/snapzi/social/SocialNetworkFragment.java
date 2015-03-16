package com.samsao.snapzi.social;


import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.androidsocialnetworks.lib.AccessToken;
import com.androidsocialnetworks.lib.SocialNetworkManager;
import com.androidsocialnetworks.lib.listener.OnLoginCompleteListener;
import com.samsao.snapzi.R;
import com.samsao.snapzi.util.PreferenceManager;
import com.samsao.snapzi.util.UserManager;

/**
 * @author jfcartier
 * @since 15-03-13
 */
public class SocialNetworkFragment extends Fragment {
    public static final String SOCIAL_NETWORK_TAG = "com.samsao.snapzi.social.SocialNetworkFragment.SOCIAL_NETWORK_TAG";
    private SocialNetworkManager mSocialNetworkManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSocialNetworkManager = (SocialNetworkManager) getFragmentManager().findFragmentByTag(SOCIAL_NETWORK_TAG);

        if (mSocialNetworkManager == null) {
            mSocialNetworkManager = SocialNetworkManager.Builder.from(getActivity())
                    .twitter(getString(R.string.twitter_api_token), getString(R.string.twitter_api_secret))
                    .facebook()
                    .googlePlus()
                    .build();
            getFragmentManager().beginTransaction().add(mSocialNetworkManager, SOCIAL_NETWORK_TAG).commit();
        }
    }

    /**
     * Set a callback when the social network manager is initialized
     * @param onInitializationCompleteListener
     */
    public void setSocialNetworkManagerOnInitializationCompleteListener(SocialNetworkManager.OnInitializationCompleteListener onInitializationCompleteListener) {
        mSocialNetworkManager.setOnInitializationCompleteListener(onInitializationCompleteListener);
    }

    /**
     * Login with Facebook
     */
    protected void loginWithFacebook(OnLoginCompleteListener onLoginCompleteListener) {
        if (!isFacebookConnected()) {
            mSocialNetworkManager.getFacebookSocialNetwork().requestLogin(onLoginCompleteListener);
        }
    }

    /**
     * Is the user logged with Facebook?
     *
     * @return
     */
    protected boolean isFacebookConnected() {
        return mSocialNetworkManager.getFacebookSocialNetwork().isConnected();
    }

    /**
     * Get Facebook access token
     *
     * @return
     */
    protected AccessToken getFacebookAccessToken() {
        return mSocialNetworkManager.getFacebookSocialNetwork().getAccessToken();
    }

    /**
     * Logout from Facebook
     */
    protected void logoutFromFacebook() {
        if (isFacebookConnected()) {
            mSocialNetworkManager.getFacebookSocialNetwork().logout();
        }
        UserManager.removeFacebookAccessToken();
    }

    /**
     * Set the facebook access token in preferences
     */
    protected void setFacebookAccessToken() {
        AccessToken accessToken = getFacebookAccessToken();
        if (accessToken != null) {
            UserManager.setFacebookAccessToken(accessToken.token);
        }
    }

    /**
     * Remove the facebook access token in preferences
     */
    protected void removeFacebookAccessToken() {
        PreferenceManager.removeFacebookAccessToken();
    }

    /**
     * Login with Twitter
     */
    protected void loginWithTwitter(OnLoginCompleteListener onLoginCompleteListener) {
        if (!isTwitterConnected()) {
            mSocialNetworkManager.getTwitterSocialNetwork().requestLogin(onLoginCompleteListener);
        }
    }

    /**
     * Is the user logged with Twitter?
     *
     * @return
     */
    protected boolean isTwitterConnected() {
        return mSocialNetworkManager.getTwitterSocialNetwork().isConnected();
    }

    /**
     * Get Twitter access token
     *
     * @return
     */
    protected AccessToken getTwitterAccessToken() {
        return mSocialNetworkManager.getTwitterSocialNetwork().getAccessToken();
    }

    /**
     * Logout from Twitter
     */
    protected void logoutFromTwitter() {
        if (isTwitterConnected()) {
            mSocialNetworkManager.getTwitterSocialNetwork().logout();
        }
    }

    /**
     * Login with Google+
     */
    protected void loginWithGooglePlus(OnLoginCompleteListener onLoginCompleteListener) {
        if (!isGooglePlusConnected()) {
            mSocialNetworkManager.getGooglePlusSocialNetwork().requestLogin(onLoginCompleteListener);
        }
    }

    /**
     * Is the user logged with Google+?
     *
     * @return
     */
    protected boolean isGooglePlusConnected() {
        return mSocialNetworkManager.getGooglePlusSocialNetwork().isConnected();
    }

    /**
     * Get Google+ access token
     *
     * @return
     */
    protected AccessToken getGooglePlusAccessToken() {
        return mSocialNetworkManager.getGooglePlusSocialNetwork().getAccessToken();
    }

    /**
     * Logout from Google+
     */
    protected void logoutFromGooglePlus() {
        if (isGooglePlusConnected()) {
            mSocialNetworkManager.getGooglePlusSocialNetwork().logout();
        }
    }
}
