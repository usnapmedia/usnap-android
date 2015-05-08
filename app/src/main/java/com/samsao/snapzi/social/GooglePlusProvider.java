package com.samsao.snapzi.social;

/**
 * @author jfcartier
 * @since 15-03-17
 */
public interface GooglePlusProvider {
    public void loginWithGooglePlus(OnGooglePlusLoginListener listener);

    public void disconnectFromGooglePlus();

    public void logoutFromGooglePlus();

    public void setGooglePlusAccessToken();

    public void removeGooglePlusAccessToken();
}
