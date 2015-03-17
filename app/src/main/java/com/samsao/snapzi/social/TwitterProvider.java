package com.samsao.snapzi.social;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.TwitterSession;

/**
 * @author jfcartier
 * @since 15-03-17
 */
public interface TwitterProvider {
    public void loginWithTwitter(Callback<TwitterSession> callback);
    public boolean isTwitterConnected();
    public void logoutFromTwitter();
    public void setTwitterAccessToken();
    public void removeTwitterAccessToken();
}
