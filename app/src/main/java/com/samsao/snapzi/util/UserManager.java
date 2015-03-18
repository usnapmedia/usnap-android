package com.samsao.snapzi.util;

import android.text.TextUtils;
import android.util.Log;

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
        return !TextUtils.isEmpty(getFacebookAccessToken());
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
        log();
    }

    /**
     * Clear the facebook access token in preferences
     *
     */
    public static void removeFacebookAccessToken() {
        PreferenceManager.removeFacebookAccessToken();
        log();
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
        log();
    }

    /**
     * Clear the twitter access token in preferences
     *
     */
    public static void removeTwitterAccessToken() {
        PreferenceManager.removeTwitterAccessToken();
        log();
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
        log();
    }

    /**
     * Clear the google+ access token in preferences
     *
     */
    public static void removeGooglePlusAccessToken() {
        PreferenceManager.removeGooglePlusAccessToken();
        log();
    }

    public static void log() {
        Log.i("UserManager", "FB token: " + getFacebookAccessToken());
        Log.i("UserManager", "Twitter token: " + getTwitterAccessToken());
        Log.i("UserManager", "Google+ token: " + getGooglePlusAccessToken());
    }
}
