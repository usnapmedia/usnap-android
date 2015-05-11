package com.samsao.snapzi.util;

import android.text.TextUtils;

/**
 * @author jfcartier
 * @since 15-03-16
 */
public class UserManager {

    // TODO inject me
    private PreferenceManager mPreferenceManager;

    public UserManager(PreferenceManager preferenceManager) {
        mPreferenceManager = preferenceManager;
    }

    /**
     * Check if the user is logged
     *
     * @return
     */
    public boolean isLogged() {
        return !TextUtils.isEmpty(getUsername());
    }

    /**
     * Returns the facebook access token
     *
     * @return
     */
    public String getFacebookAccessToken() {
        return mPreferenceManager.getFacebookAccessToken();
    }

    /**
     * Set the facebook access token in preferences
     *
     * @param token
     */
    public void setFacebookAccessToken(String token) {
        mPreferenceManager.setFacebookAccessToken(token);
    }

    /**
     * Clear the facebook access token in preferences
     */
    public void removeFacebookAccessToken() {
        mPreferenceManager.removeFacebookAccessToken();
    }

    /**
     * Returns the twitter access token
     *
     * @return
     */
    public String getTwitterAccessToken() {
        return mPreferenceManager.getTwitterAccessToken();
    }

    /**
     * Returns the twitter secret
     *
     * @return
     */
    public String getTwitterSecret() {
        return mPreferenceManager.getTwitterSecret();
    }

    /**
     * Set the twitter access token in preferences
     *
     * @param token
     * @param secret
     */
    public void setTwitterAccessToken(String token, String secret) {
        mPreferenceManager.setTwitterAccessToken(token);
        mPreferenceManager.setTwitterSecret(secret);
    }

    /**
     * Clear the twitter access token in preferences
     */
    public void removeTwitterAccessToken() {
        mPreferenceManager.removeTwitterAccessToken();
        mPreferenceManager.removeTwitterSecret();
    }

    /**
     * Returns the google+ access token
     *
     * @return
     */
    public String getGooglePlusAccessToken() {
        return mPreferenceManager.getGooglePlusAccessToken();
    }

    /**
     * Set the google+ access token in preferences
     *
     * @param token
     */
    public void setGooglePlusAccessToken(String token) {
        mPreferenceManager.setGooglePlusAccessToken(token);
    }

    /**
     * Clear the google+ access token in preferences
     */
    public void removeGooglePlusAccessToken() {
        mPreferenceManager.removeGooglePlusAccessToken();
    }

    /**
     * Returns the username
     *
     * @return
     */
    public String getUsername() {
        return mPreferenceManager.getUsername();
    }

    /**
     * Set the username in preferences
     *
     * @param username
     */
    public void setUsername(String username) {
        mPreferenceManager.setUsername(username);
    }

    /**
     * Clear the username in preferences
     */
    public void removeUsername() {
        mPreferenceManager.removeUsername();
    }

    /**
     * Returns the password
     *
     * @return
     */
    public String getPassword() {
        return mPreferenceManager.getPassword();
    }

    /**
     * Set the password in preferences
     *
     * @param password
     */
    public void setPassword(String password) {
        mPreferenceManager.setPassword(password);
    }

    /**
     * Clear the password in preferences
     */
    private void removePassword() {
        mPreferenceManager.removePassword();
    }

    /**
     * Returns the FirstName
     *
     * @return
     */
    public String getFirstName() {
        return mPreferenceManager.getFirstName();
    }

    /**
     * Set the FirstName in preferences
     *
     * @param firstname
     */
    public void setFirstName(String firstname) {
        mPreferenceManager.setFirstName(firstname);
    }

    /**
     * Clear the FirstName in preferences
     */
    public void removeFirstName() {
        mPreferenceManager.removeFirstName();
    }

    /**
     * Returns the LastName
     *
     * @return
     */
    public String getLastName() {
        return mPreferenceManager.getLastName();
    }

    /**
     * Set the LastName in preferences
     *
     * @param lastName
     */
    public void setLastName(String lastName) {
        mPreferenceManager.setLastName(lastName);
    }

    /**
     * Clear the LastName in preferences
     */
    public void removeLastName() {
        mPreferenceManager.removeLastName();
    }

    /**
     * Returns the Email
     *
     * @return
     */
    public String getEmail() {
        return mPreferenceManager.getEmail();
    }

    /**
     * Set the Email in preferences
     *
     * @param email
     */
    public void setEmail(String email) {
        mPreferenceManager.setEmail(email);
    }

    /**
     * Clear the Email in preferences
     */
    public void removeEmail() {
        mPreferenceManager.removeEmail();
    }

    /**
     * Returns the Birthday
     *
     * @return
     */
    public String getBirthday() {
        return mPreferenceManager.getBirthday();
    }

    /**
     * Set the Birthday in preferences
     *
     * @param birthday
     */
    public void setBirthday(String birthday) {
        mPreferenceManager.setBirthday(birthday);
    }

    /**
     * Clear the Birthday in preferences
     */
    public void removeBirthday() {
        mPreferenceManager.removeBirthday();
    }

    /**
     * Logs the user in
     *
     * @param username
     * @param password
     * @throws IllegalArgumentException
     */
    public void login(String username, String password) throws IllegalArgumentException {
        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            throw new IllegalArgumentException();
        }
        setUsername(username);
        setPassword(password);
    }

    /**
     * Logs the user out
     */
    public void logout() {
        removeUsername();
        removePassword();
        removeBirthday();
        removeEmail();
        removeFirstName();
        removeLastName();
        removeFacebookAccessToken();
        removeGooglePlusAccessToken();
        removeTwitterAccessToken();
    }
}
