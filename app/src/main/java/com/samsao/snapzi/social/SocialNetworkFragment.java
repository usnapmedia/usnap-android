package com.samsao.snapzi.social;


import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;

import com.samsao.snapzi.util.UserManager;
import com.sromku.simple.fb.SimpleFacebook;
import com.sromku.simple.fb.entities.Account;
import com.sromku.simple.fb.listeners.OnAccountsListener;
import com.sromku.simple.fb.listeners.OnLoginListener;
import com.sromku.simple.fb.listeners.OnLogoutListener;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import java.util.List;

/**
 * @author jfcartier
 * @since 15-03-13
 */
public class SocialNetworkFragment extends Fragment {
    public static final String SOCIAL_NETWORK_TAG = "com.samsao.snapzi.social.SocialNetworkFragment.SOCIAL_NETWORK_TAG";

    /**
     * Request codes
     */
    private final int TWITTER_REQ_CODE = 140;
    private final int FACEBOOK_REQ_CODE = 64206;

    /**
     * Twitter login button. We have to use this to login since the login method does not work
     */
    private TwitterLoginButton mTwitterLoginButton;

    /**
     * Facebook utils
     */
    private SimpleFacebook mSimpleFacebook;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTwitterLoginButton = new TwitterLoginButton(getActivity());
        mSimpleFacebook = SimpleFacebook.getInstance(getActivity());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case TWITTER_REQ_CODE:
                mTwitterLoginButton.onActivityResult(requestCode, resultCode, data);
                break;
            case FACEBOOK_REQ_CODE:
                mSimpleFacebook.onActivityResult(getActivity(), requestCode, resultCode, data);
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

    /**
     * Login with Facebook
     */
    protected void loginWithFacebook(OnLoginListener onLoginListener) {
        if (!isFacebookConnected()) {
            mSimpleFacebook.login(onLoginListener);
        }
    }

    /**
     * Is the user logged with Facebook?
     *
     * @return
     */
    protected boolean isFacebookConnected() {
        return mSimpleFacebook.isLogin();
    }

    /**
     * Logout from Facebook
     */
    protected void logoutFromFacebook(OnLogoutListener onLogoutListener) {
        if (isFacebookConnected()) {
            mSimpleFacebook.logout(onLogoutListener);
        }
        UserManager.removeFacebookAccessToken();
    }

    /**
     * Set the facebook access token in preferences
     */
    protected void setFacebookAccessToken() {
        mSimpleFacebook.getAccounts(new OnAccountsListener() {
            @Override
            public void onComplete(List<Account> response) {
                if (response.size() > 0) {
                    UserManager.setFacebookAccessToken(response.get(0).getAccessToken());
                }
            }
        });
    }

    /**
     * Remove the facebook access token in preferences
     */
    protected void removeFacebookAccessToken() {
        UserManager.removeFacebookAccessToken();
    }

    /**
     * Login with Twitter
     */
    protected void loginWithTwitter(Callback<TwitterSession> callback) {
        if (!isTwitterConnected()) {
            mTwitterLoginButton.setCallback(callback);
            mTwitterLoginButton.performClick();
        }
    }

    /**
     * Is the user logged with Twitter?
     *
     * @return
     */
    protected boolean isTwitterConnected() {
        return TwitterCore.getInstance().getSessionManager().getActiveSession() != null;
    }

    /**
     * Get Twitter access token
     *
     * @return
     */
//    protected AccessToken getTwitterAccessToken() {
//        return mSocialNetworkManager.getTwitterSocialNetwork().getAccessToken();
//    }

    /**
     * Logout from Twitter
     */
    protected void logoutFromTwitter() {
        if (isTwitterConnected()) {
            TwitterCore.getInstance().logOut();
        }
        UserManager.removeTwitterAccessToken();
    }

    /**
     * Set the twitter access token in preferences
     */
    protected void setTwitterAccessToken() {
        UserManager.setTwitterAccessToken(Twitter.getSessionManager().getActiveSession().getAuthToken().token);
    }

    /**
     * Remove the twitter access token in preferences
     */
    protected void removeTwitterAccessToken() {
        UserManager.removeFacebookAccessToken();
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
