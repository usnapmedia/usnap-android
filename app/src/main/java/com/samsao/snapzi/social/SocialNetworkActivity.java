package com.samsao.snapzi.social;

import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.facebook.Session;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.samsao.snapzi.util.UserManager;
import com.sromku.simple.fb.SimpleFacebook;
import com.sromku.simple.fb.listeners.OnLoginListener;
import com.sromku.simple.fb.listeners.OnLogoutListener;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

/**
 * @author jfcartier
 * @since 15-03-13
 */
public class SocialNetworkActivity extends ActionBarActivity implements FacebookProvider,
        TwitterProvider,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    /**
     * Request codes
     */
    private final int TWITTER_REQ_CODE = 140;
    private final int FACEBOOK_REQ_CODE = 64206;
    private final int RC_SIGN_IN = 56344;

    /**
     * Facebook utils
     */
    private SimpleFacebook mSimpleFacebook;

    /**
     * Twitter login button. We have to use this to login since the login method does not work
     */
    private TwitterLoginButton mTwitterLoginButton;

    /**
     * Google API client
     */
    private GoogleApiClient mGoogleApiClient;

    /**
     * Listener to send Google+ login callbacks
     */
    private OnGooglePlusLoginListener mOnGooglePlusLoginListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTwitterLoginButton = new TwitterLoginButton(this);
        mSimpleFacebook = SimpleFacebook.getInstance(this);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .build();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case RC_SIGN_IN:
                if (!mGoogleApiClient.isConnecting()) {
                    mGoogleApiClient.connect();
                }
                break;
            case FACEBOOK_REQ_CODE:
                mSimpleFacebook.onActivityResult(this, requestCode, resultCode, data);
                break;
            case TWITTER_REQ_CODE:
                mTwitterLoginButton.onActivityResult(requestCode, resultCode, data);
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                // deliver the result to the fragment
//                Fragment fragment = getFragmentManager().findFragmentByTag(SocialNetworkFragment.SOCIAL_NETWORK_TAG);
//                if (fragment != null) {
//                    fragment.onActivityResult(requestCode, resultCode, data);
//                }
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSimpleFacebook = SimpleFacebook.getInstance(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    public void onConnectionFailed(ConnectionResult result) {
        if (result.hasResolution()) {
            try {
                startIntentSenderForResult(result.getResolution().getIntentSender(),
                        RC_SIGN_IN, null, 0, 0, 0);
            } catch (IntentSender.SendIntentException e) {
                // The intent was canceled before it was sent.  Return to the default
                // state and attempt to connect to get an updated ConnectionResult.
                mGoogleApiClient.connect();
            }
        } else {
            mOnGooglePlusLoginListener.onFail();
        }
    }

    public void onConnected(Bundle connectionHint) {
        // We've resolved any connection errors.  mGoogleApiClient can be used to
        // access Google APIs on behalf of the user.
        mOnGooglePlusLoginListener.onSuccess();
    }

    public void onConnectionSuspended(int cause) {
        mGoogleApiClient.connect();
    }

    /**
     * Login with Facebook
     */
    public void loginWithFacebook(OnLoginListener onLoginListener) {
        if (!isFacebookConnected()) {
            mSimpleFacebook.login(onLoginListener);
        }
    }

    /**
     * Is the user logged with Facebook?
     *
     * @return
     */
    public boolean isFacebookConnected() {
        return mSimpleFacebook.isLogin();
    }

    /**
     * Logout from Facebook
     */
    public void logoutFromFacebook(OnLogoutListener onLogoutListener) {
        if (isFacebookConnected()) {
            mSimpleFacebook.logout(onLogoutListener);
        }
        UserManager.removeFacebookAccessToken();
    }

    /**
     * Set the facebook access token in preferences
     */
    public void setFacebookAccessToken() {
        Session session = mSimpleFacebook.getSession();
        if (session != null) {
            UserManager.setFacebookAccessToken(session.getAccessToken());
        }
    }

    /**
     * Remove the facebook access token in preferences
     */
    public void removeFacebookAccessToken() {
        UserManager.removeFacebookAccessToken();
    }

    /**
     * Login with Twitter
     */
    public void loginWithTwitter(Callback<TwitterSession> callback) {
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
    public boolean isTwitterConnected() {
        return TwitterCore.getInstance().getSessionManager().getActiveSession() != null;
    }

    /**
     * Logout from Twitter
     */
    public void logoutFromTwitter() {
        if (isTwitterConnected()) {
            TwitterCore.getInstance().logOut();
        }
        UserManager.removeTwitterAccessToken();
    }

    /**
     * Set the twitter access token in preferences
     */
    public void setTwitterAccessToken() {
        UserManager.setTwitterAccessToken(Twitter.getSessionManager().getActiveSession().getAuthToken().token);
    }

    /**
     * Remove the twitter access token in preferences
     */
    public void removeTwitterAccessToken() {
        UserManager.removeFacebookAccessToken();
    }

    /**
     * Login with Google+
     */
    public void loginWithGooglePlus(OnGooglePlusLoginListener listener) {
        mOnGooglePlusLoginListener = listener;
        if (!mGoogleApiClient.isConnecting() && !mGoogleApiClient.isConnected()) {
            mGoogleApiClient.connect();
        }
    }

    /**
     * Is Google+ connected?
     * @return
     */
    public boolean isGooglePlusConnected() {
        return mGoogleApiClient.isConnected();
    }

    /**
     * Get Google+ access token
     *
     * @return
     */
    public String getGooglePlusAccessToken() {
        // TODO
//        Bundle appActivities = new Bundle();
//        appActivities.putString(GoogleAuthUtil.KEY_REQUEST_VISIBLE_ACTIVITIES,
//                "<APP-ACTIVITY1> <APP-ACTIVITY2>");
//        String scopes = "oauth2:server:client_id:<SERVER-CLIENT-ID>:api_scope:<SCOPE1> <SCOPE2>";
//        String code = null;
//        try {
//            code = GoogleAuthUtil.getToken(
//                    this,                                              // Context context
//                    Plus.AccountApi.getAccountName(mGoogleApiClient),  // String accountName
//                    scopes,                                            // String scope
//                    appActivities                                      // Bundle bundle
//            );
//
//        } catch (IOException transientEx) {
//            // network or server error, the call is expected to succeed if you try again later.
//            // Don't attempt to call again immediately - the request is likely to
//            // fail, you'll hit quotas or back-off.
//            ...
//            return;
//        } catch (UserRecoverableAuthException e) {
//            // Requesting an authorization code will always throw
//            // UserRecoverableAuthException on the first call to GoogleAuthUtil.getToken
//            // because the user must consent to offline access to their data.  After
//            // consent is granted control is returned to your activity in onActivityResult
//            // and the second call to GoogleAuthUtil.getToken will succeed.
//            startActivityForResult(e.getIntent(), AUTH_CODE_REQUEST_CODE);
//            return;
//        } catch (GoogleAuthException authEx) {
//            // Failure. The call is not expected to ever succeed so it should not be
//            // retried.
//            ...
//            return;
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
        return null;
    }

    /**
     * Logout from Google+
     */
    public void logoutFromGooglePlus() {
        if (isGooglePlusConnected()) {
            Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
            Plus.AccountApi.revokeAccessAndDisconnect(mGoogleApiClient);
        }
        removeGooglePlusAccessToken();
    }

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
    public void removeGooglePlusAccessToken() {
        UserManager.removeFacebookAccessToken();
    }
}
