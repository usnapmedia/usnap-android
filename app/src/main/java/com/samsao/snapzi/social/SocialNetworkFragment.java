package com.samsao.snapzi.social;


import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.androidsocialnetworks.lib.AccessToken;
import com.androidsocialnetworks.lib.SocialNetworkManager;
import com.androidsocialnetworks.lib.listener.OnLoginCompleteListener;
import com.samsao.snapzi.R;

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
//                    .facebook()
//                    .googlePlus()
                    .build();
            getFragmentManager().beginTransaction().add(mSocialNetworkManager, SOCIAL_NETWORK_TAG).commit();
        }
    }

    /**
     * Login with Facebook
     */
    protected void loginWithFacebook(OnLoginCompleteListener onLoginCompleteListener) {
        mSocialNetworkManager.getFacebookSocialNetwork().requestLogin(onLoginCompleteListener);
    }

    /**
     * Is the user logged with Facebook?
     * @return
     */
    protected boolean isFacebookConnected() {
        return mSocialNetworkManager.getFacebookSocialNetwork().isConnected();
    }

    /**
     * Get Facebook access token
     * @return
     */
    protected AccessToken getFacebookAccessToken() {
        return mSocialNetworkManager.getFacebookSocialNetwork().getAccessToken();
    }

    /**
     * Login with Twitter
     */
    protected void loginWithTwitter(OnLoginCompleteListener onLoginCompleteListener) {
        mSocialNetworkManager.getTwitterSocialNetwork().requestLogin(onLoginCompleteListener);
    }

    /**
     * Is the user logged with Twitter?
     * @return
     */
    protected boolean isTwitterConnected() {
        return mSocialNetworkManager.getTwitterSocialNetwork().isConnected();
    }

    /**
     * Get Twitter access token
     * @return
     */
    protected AccessToken getTwitterAccessToken() {
        return mSocialNetworkManager.getTwitterSocialNetwork().getAccessToken();
    }

    /**
     * Login with Google+
     */
    protected void loginWithGooglePlus(OnLoginCompleteListener onLoginCompleteListener) {
        mSocialNetworkManager.getGooglePlusSocialNetwork().requestLogin(onLoginCompleteListener);
    }

    /**
     * Is the user logged with Google+?
     * @return
     */
    protected boolean isGooglePlusConnected() {
        return mSocialNetworkManager.getGooglePlusSocialNetwork().isConnected();
    }

    /**
     * Get Google+ access token
     * @return
     */
    protected AccessToken getGooglePlusAccessToken() {
        return mSocialNetworkManager.getGooglePlusSocialNetwork().getAccessToken();
    }
}
