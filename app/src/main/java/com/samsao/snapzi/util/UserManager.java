package com.samsao.snapzi.util;

import android.text.TextUtils;

import com.samsao.snapzi.api.entity.User;

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
    private void removeUsername() {
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
    private String getFirstName() {
        return mPreferenceManager.getFirstName();
    }

    /**
     * Set the FirstName in preferences
     *
     * @param firstname
     */
    private void setFirstName(String firstname) {
        mPreferenceManager.setFirstName(firstname);
    }

    /**
     * Clear the FirstName in preferences
     */
    private void removeFirstName() {
        mPreferenceManager.removeFirstName();
    }

    /**
     * Returns the LastName
     *
     * @return
     */
    private String getLastName() {
        return mPreferenceManager.getLastName();
    }

    /**
     * Set the LastName in preferences
     *
     * @param lastName
     */
    private void setLastName(String lastName) {
        mPreferenceManager.setLastName(lastName);
    }

    /**
     * Clear the LastName in preferences
     */
    private void removeLastName() {
        mPreferenceManager.removeLastName();
    }

    /**
     * Returns the Email
     *
     * @return
     */
    private String getEmail() {
        return mPreferenceManager.getEmail();
    }

    /**
     * Set the Email in preferences
     *
     * @param email
     */
    private void setEmail(String email) {
        mPreferenceManager.setEmail(email);
    }

    /**
     * Clear the Email in preferences
     */
    private void removeEmail() {
        mPreferenceManager.removeEmail();
    }

    /**
     * Returns the Birthday
     *
     * @return
     */
    private String getBirthday() {
        return mPreferenceManager.getBirthday();
    }

    /**
     * Set the Birthday in preferences
     *
     * @param birthday
     */
    private void setBirthday(String birthday) {
        mPreferenceManager.setBirthday(birthday);
    }

    /**
     * Clear the Birthday in preferences
     */
    private void removeBirthday() {
        mPreferenceManager.removeBirthday();
    }

    /**
     * Returns the Contribution
     *
     * @return
     */
    private Integer getContribution() {
        return mPreferenceManager.getContribution();
    }

    /**
     * Set the Contribution in preferences
     *
     * @param contribution
     */
    private void setContribution(Integer contribution) {
        mPreferenceManager.setContribution(contribution);
    }

    /**
     * Clear the Contribution in preferences
     */
    private void removeContribution() {
        mPreferenceManager.removeContribution();
    }

    /**
     * Returns the Score
     *
     * @return
     */
    private Integer getScore() {
        return mPreferenceManager.getScore();
    }

    /**
     * Set the Score in preferences
     *
     * @param score
     */
    private void setScore(Integer score) {
        mPreferenceManager.setScore(score);
    }

    /**
     * Clear the Score in preferences
     */
    private void removeScore() {
        mPreferenceManager.removeScore();
    }

    public void saveUser(User user) {
        setFirstName(user.getFirstName());
        setLastName(user.getLastName());
        setEmail(user.getEmail());
        setBirthday(user.getDob());
        setContribution(user.getContribution());
        setScore(user.getScore());
    }

    public User getUser() {
        User user = new User();
        user.setFirstName(getFirstName());
        user.setLastName(getLastName());
        user.setEmail(getEmail());
        user.setDob(getBirthday());
        user.setContribution(getContribution());
        user.setScore(getScore());
        return user;
    }

    private void removeUser() {
        removeBirthday();
        removeEmail();
        removeFirstName();
        removeLastName();
        removeContribution();
        removeScore();
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
        removeUser();
        removeUsername();
        removePassword();
        removeFacebookAccessToken();
        removeGooglePlusAccessToken();
        removeTwitterAccessToken();
    }
}
