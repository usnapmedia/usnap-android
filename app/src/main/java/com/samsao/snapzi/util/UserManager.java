package com.samsao.snapzi.util;

/**
 * @author jfcartier
 * @since 15-03-16
 */
public class UserManager {

    /**
     * Check if the user is logged
     * @return
     */
    public static boolean isLogged() {
        // TODO get logging with backend
        return true;
    }

    /**
     * Returns the facebook access token
     * @return
     */
    public static String getFacebookAccessToken() {
        return PreferenceManager.getFacebookAccessToken();
    }

    /**
     * Set the facebook access token in preferences
     *
     * @param token
     */
    public static void setFacebookAccessToken(String token) {
        PreferenceManager.setFacebookAccessToken(token);
    }

    /**
     * Clear the facebook access token in preferences
     *
     */
    public static void removeFacebookAccessToken() {
        PreferenceManager.removeFacebookAccessToken();
    }

    /**
     * Returns the twitter access token
     * @return
     */
    public static String getTwitterAccessToken() {
        return PreferenceManager.getTwitterAccessToken();
    }

    /**
     * Set the twitter access token in preferences
     *
     * @param token
     */
    public static void setTwitterAccessToken(String token) {
        PreferenceManager.setTwitterAccessToken(token);
    }

    /**
     * Clear the twitter access token in preferences
     *
     */
    public static void removeTwitterAccessToken() {
        PreferenceManager.removeTwitterAccessToken();
    }

    /**
     * Returns the google+ access token
     * @return
     */
    public static String getGooglePlusAccessToken() {
        return PreferenceManager.getGooglePlusAccessToken();
    }

    /**
     * Set the google+ access token in preferences
     *
     * @param token
     */
    public static void setGooglePlusAccessToken(String token) {
        PreferenceManager.setGooglePlusAccessToken(token);
    }

    /**
     * Clear the google+ access token in preferences
     *
     */
    public static void removeGooglePlusAccessToken() {
        PreferenceManager.removeGooglePlusAccessToken();
    }
}
