package com.samsao.snapzi.social;

import com.sromku.simple.fb.listeners.OnLoginListener;
import com.sromku.simple.fb.listeners.OnLogoutListener;

/**
 * @author jfcartier
 * @since 15-03-17
 */
public interface FacebookProvider {
    public void loginWithFacebook(OnLoginListener onLoginListener);

    public boolean isFacebookConnected();

    public void logoutFromFacebook(OnLogoutListener onLogoutListener);

    public void setFacebookAccessToken();

    public void removeFacebookAccessToken();
}
