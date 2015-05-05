package com.samsao.snapzi.social;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.facebook.Session;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.samsao.snapzi.util.PreferenceManager;
import com.samsao.snapzi.util.UserManager;
import com.sromku.simple.fb.SimpleFacebook;
import com.sromku.simple.fb.listeners.OnLoginListener;
import com.sromku.simple.fb.listeners.OnLogoutListener;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import java.io.IOException;

/**
 * @author jfcartier
 * @since 15-03-13
 */
public class SocialNetworkActivity extends AppCompatActivity implements FacebookProvider,
        TwitterProvider,
        GooglePlusProvider,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    /**
     * Request codes
     */
    private final int TWITTER_REQ_CODE = 140;
    private final int FACEBOOK_REQ_CODE = 64206;
    private final int GPLUS_REQ_CODE = 56344;

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
    
    // TODO inject me
    private UserManager mUserManager = new UserManager(new PreferenceManager());

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
            case GPLUS_REQ_CODE:
                if (resultCode == Activity.RESULT_CANCELED) {
                    mOnGooglePlusLoginListener.onFail();
                } else if (!mGoogleApiClient.isConnecting()) {
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
                        GPLUS_REQ_CODE, null, 0, 0, 0);
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
        mUserManager.removeFacebookAccessToken();
    }

    /**
     * Set the facebook access token in preferences
     */
    public void setFacebookAccessToken() {
        Session session = mSimpleFacebook.getSession();
        if (session != null) {
            mUserManager.setFacebookAccessToken(session.getAccessToken());
        }
    }

    /**
     * Remove the facebook access token in preferences
     */
    public void removeFacebookAccessToken() {
        mUserManager.removeFacebookAccessToken();
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
        mUserManager.removeTwitterAccessToken();
    }

    /**
     * Set the twitter access token in preferences
     */
    public void setTwitterAccessToken() {
        mUserManager.setTwitterAccessToken(Twitter.getSessionManager().getActiveSession().getAuthToken().token,
                Twitter.getSessionManager().getActiveSession().getAuthToken().secret);
    }

    /**
     * Remove the twitter access token in preferences
     */
    public void removeTwitterAccessToken() {
        mUserManager.removeFacebookAccessToken();
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
     * Disconnect from Google+
     */
    public void disconnectFromGooglePlus() {
        if (isGooglePlusConnected()) {
            mGoogleApiClient.disconnect();
        }
        removeGooglePlusAccessToken();
        mOnGooglePlusLoginListener = null;
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
        mOnGooglePlusLoginListener = null;
    }

    /**
     * Set the google+ access token in preferences
     */
    public void setGooglePlusAccessToken() {
        new SetGooglePlusAccessTokenTask().execute();
    }

    /**
     * Remove the google+ access token in preferences
     */
    public void removeGooglePlusAccessToken() {
        mUserManager.removeGooglePlusAccessToken();
    }

    /**
     * Task to set Google+ access token in the background
     */
    public class SetGooglePlusAccessTokenTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            try {
                mUserManager.setGooglePlusAccessToken(GoogleAuthUtil.getToken(SocialNetworkActivity.this,
                        Plus.AccountApi.getAccountName(mGoogleApiClient),
                        "oauth2:https://www.googleapis.com/auth/plus.login"));
            } catch (IOException transientEx) {
                // network or server error, the call is expected to succeed if you try again later.
                // Don't attempt to call again immediately - the request is likely to
                // fail, you'll hit quotas or back-off.
                return null;
            } catch (UserRecoverableAuthException e) {
                // Recover
                return null;
            } catch (GoogleAuthException authEx) {
                // Failure. The call is not expected to ever succeed so it should not be retried.
                return null;
            } catch (Exception e) {
                return null;
            }
            return null;
        }
    }
}
