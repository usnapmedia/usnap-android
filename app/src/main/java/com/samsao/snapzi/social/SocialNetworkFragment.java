package com.samsao.snapzi.social;


import android.app.Activity;
import android.app.Fragment;

import com.samsao.snapzi.util.UserManager;
import com.sromku.simple.fb.listeners.OnLoginListener;
import com.sromku.simple.fb.listeners.OnLogoutListener;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.TwitterSession;

/**
 * @author jfcartier
 * @since 15-03-13
 */
public class SocialNetworkFragment extends Fragment {
    public static final String SOCIAL_NETWORK_TAG = "com.samsao.snapzi.social.SocialNetworkFragment.SOCIAL_NETWORK_TAG";

    /**
     * Attached activity providing SimpleFacebook
     */
    private FacebookProvider mFacebookProvider;

    /**
     * Attached activity providing Twitter button
     */
    private TwitterProvider mTwitterProvider;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mFacebookProvider = (FacebookProvider) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement FacebookProvider");
        }
        try {
            mTwitterProvider = (TwitterProvider) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement TwitterProvider");
        }
    }

    /**
     * Login with Facebook
     */
    protected void loginWithFacebook(OnLoginListener onLoginListener) {
        mFacebookProvider.loginWithFacebook(onLoginListener);
    }

    /**
     * Is the user logged with Facebook?
     *
     * @return
     */
    protected boolean isFacebookConnected() {
        return mFacebookProvider.isFacebookConnected();
    }

    /**
     * Logout from Facebook
     */
    protected void logoutFromFacebook(OnLogoutListener onLogoutListener) {
        mFacebookProvider.logoutFromFacebook(onLogoutListener);
    }

    /**
     * Set the facebook access token in preferences
     */
    protected void setFacebookAccessToken() {
        mFacebookProvider.setFacebookAccessToken();
    }

    /**
     * Remove the facebook access token in preferences
     */
    protected void removeFacebookAccessToken() {
        mFacebookProvider.removeFacebookAccessToken();
    }

    /**
     * Login with Twitter
     */
    protected void loginWithTwitter(Callback<TwitterSession> callback) {
        mTwitterProvider.loginWithTwitter(callback);
    }

    /**
     * Is the user logged with Twitter?
     *
     * @return
     */
    protected boolean isTwitterConnected() {
        return mTwitterProvider.isTwitterConnected();
    }

    /**
     * Logout from Twitter
     */
    protected void logoutFromTwitter() {
        mTwitterProvider.logoutFromTwitter();
    }

    /**
     * Set the twitter access token in preferences
     */
    protected void setTwitterAccessToken() {
        mTwitterProvider.setTwitterAccessToken();
    }

    /**
     * Remove the twitter access token in preferences
     */
    protected void removeTwitterAccessToken() {
        mTwitterProvider.removeTwitterAccessToken();
    }

    /**
     * Login with Google+
     */
//    protected void loginWithGooglePlus(OnLoginCompleteListener onLoginCompleteListener) {
//        if (!isGooglePlusConnected()) {
//            mSocialNetworkManager.getGooglePlusSocialNetwork().requestLogin(onLoginCompleteListener);
//        }
//    }

    /**
     * Is the user logged with Google+?
     *
     * @return
     */
//    protected boolean isGooglePlusConnected() {
//        return mSocialNetworkManager.getGooglePlusSocialNetwork().isConnected();
//    }

    /**
     * Get Google+ access token
     *
     * @return
     */
//    protected AccessToken getGooglePlusAccessToken() {
//        return mSocialNetworkManager.getGooglePlusSocialNetwork().getAccessToken();
//    }

    /**
     * Logout from Google+
     */
//    protected void logoutFromGooglePlus() {
//        if (isGooglePlusConnected()) {
//            mSocialNetworkManager.getGooglePlusSocialNetwork().logout();
//        }
//    }

    /**
     * Set the google+ access token in preferences
     */
//    protected void setGooglePlusAccessToken() {
//        AccessToken accessToken = getGooglePlusAccessToken();
//        if (accessToken != null) {
//            UserManager.setGooglePlusAccessToken(accessToken.token);
//        }
//    }

    /**
     * Remove the google+ access token in preferences
     */
    protected void removeGooglePlusAccessToken() {
        UserManager.removeFacebookAccessToken();
    }
}
