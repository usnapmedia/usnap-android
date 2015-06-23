/*
 * Copyright (c) 2014 Samsao Development Inc.
 */

package com.samsao.snapzi.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.samsao.snapzi.SnapziApplication;

import java.util.Map;
import java.util.Set;

/**
 * @author jfcartier
 * @since 2015-03-13
 */
public class PreferenceManager {
    /**
     * Constants
     */
    private final String PREFERENCES_FILE_KEY = "com.samsao.snapzi.PREFERENCE_FILE_KEY";
    private final String FACEBOOK_ACCESS_TOKEN_KEY = "com.samsao.snapzi.preference.FACEBOOK_ACCESS_TOKEN_KEY";
    private final String TWITTER_ACCESS_TOKEN_KEY = "com.samsao.snapzi.preference.TWITTER_ACCESS_TOKEN_KEY";
    private final String TWITTER_ACCESS_SECRET_KEY = "com.samsao.snapzi.preference.TWITTER_ACCESS_SECRET_KEY";
    private final String GPLUS_ACCESS_TOKEN_KEY = "com.samsao.snapzi.preference.GPLUS_ACCESS_TOKEN_KEY";
    private final String USERNAME_KEY = "com.samsao.snapzi.preference.USERNAME_KEY";
    private final String PASSWORD_KEY = "com.samsao.snapzi.preference.PASSWORD_KEY";
    private final String FIRSTNAME_KEY = "com.samsao.snapzi.util.FIRSTNAME_KEY";
    private final String LASTNAME_KEY = "com.samsao.snapzi.util.LASTNAME_KEY";
    private final String EMAIL_KEY = "com.samsao.snapzi.util.EMAIL_KEY";
    private final String BIRTHDAY_KEY = "com.samsao.snapzi.util.BIRTHDAY_LONG_KEY";
    private final String CONTRIBUTION_KEY = "com.samsao.snapzi.util.CONTRIBUTION_KEY";
    private final String SCORE_KEY = "com.samsao.snapzi.util.SCORE_KEY";

    private SharedPreferences mSharedPreferences;

    public PreferenceManager() {
        mSharedPreferences = SnapziApplication.getContext().getSharedPreferences(PREFERENCES_FILE_KEY, Context.MODE_PRIVATE);
    }

    private SharedPreferences.Editor getEditor() {
        return mSharedPreferences.edit();
    }

    private boolean contains(String key) {
        return mSharedPreferences.contains(key);
    }

    private Map<String, ?> getAll() {
        return mSharedPreferences.getAll();
    }

    private boolean getBoolean(String key, boolean defValue) {
        return mSharedPreferences.getBoolean(key, defValue);
    }

    private float getFloat(String key, float defValue) {
        return mSharedPreferences.getFloat(key, defValue);
    }

    private int getInt(String key, int defValue) {
        return mSharedPreferences.getInt(key, defValue);
    }

    private long getLong(String key, long defValue) {
        return mSharedPreferences.getLong(key, defValue);
    }

    private String getString(String key, String defValue) {
        return mSharedPreferences.getString(key, defValue);
    }

    private Set<String> getSetString(String key, Set<String> defValue) {
        return mSharedPreferences.getStringSet(key, defValue);
    }

    public void registerOnSharedPreferenceChangeListener(SharedPreferences.OnSharedPreferenceChangeListener listener) {
        mSharedPreferences.registerOnSharedPreferenceChangeListener(listener);
    }

    public void unregisterOnSharedPreferenceChangeListener(SharedPreferences.OnSharedPreferenceChangeListener listener) {
        mSharedPreferences.unregisterOnSharedPreferenceChangeListener(listener);
    }

    public void clear() {
        getEditor().clear().apply();
    }

    private SharedPreferences.Editor putBoolean(String key, boolean value) {
        return getEditor().putBoolean(key, value);
    }

    private SharedPreferences.Editor putFloat(String key, float value) {
        return getEditor().putFloat(key, value);
    }

    private SharedPreferences.Editor putInt(String key, int value) {
        return getEditor().putInt(key, value);
    }

    private SharedPreferences.Editor putLong(String key, long value) {
        return getEditor().putLong(key, value);
    }

    private SharedPreferences.Editor putString(String key, String value) {
        return getEditor().putString(key, value);
    }

    private SharedPreferences.Editor putStringSet(String key, Set<String> value) {
        return getEditor().putStringSet(key, value);
    }

    public SharedPreferences.Editor remove(String key) {
        return getEditor().remove(key);
    }

    /**
     * START PUTTING GETTER/SETTER METHODS HERE
     */

    /**
     * Get facebook access token
     *
     * @return
     */
    public String getFacebookAccessToken() {
        return getString(FACEBOOK_ACCESS_TOKEN_KEY, null);
    }

    /**
     * Set facebook access token
     *
     * @param facebookAccessToken
     */
    public void setFacebookAccessToken(String facebookAccessToken) {
        putString(FACEBOOK_ACCESS_TOKEN_KEY, facebookAccessToken).apply();
    }

    /**
     * Remove facebook access token
     */
    public void removeFacebookAccessToken() {
        getEditor().remove(FACEBOOK_ACCESS_TOKEN_KEY).apply();
    }

    /**
     * Get twitter access token
     *
     * @return
     */
    public String getTwitterAccessToken() {
        return getString(TWITTER_ACCESS_TOKEN_KEY, null);
    }

    /**
     * Set twitter access token
     *
     * @param twitterAccessToken
     */
    public void setTwitterAccessToken(String twitterAccessToken) {
        putString(TWITTER_ACCESS_TOKEN_KEY, twitterAccessToken).apply();
    }

    /**
     * Remove twitter access token
     */
    public void removeTwitterAccessToken() {
        getEditor().remove(TWITTER_ACCESS_TOKEN_KEY).apply();
    }

    /**
     * Get twitter secret
     *
     * @return
     */
    public String getTwitterSecret() {
        return getString(TWITTER_ACCESS_SECRET_KEY, null);
    }

    /**
     * Set twitter secret
     *
     * @param twitterSecret
     */
    public void setTwitterSecret(String twitterSecret) {
        putString(TWITTER_ACCESS_SECRET_KEY, twitterSecret).apply();
    }

    /**
     * Remove twitter secret
     */
    public void removeTwitterSecret() {
        getEditor().remove(TWITTER_ACCESS_SECRET_KEY).apply();
    }

    /**
     * Get google+ access token
     *
     * @return
     */
    public String getGooglePlusAccessToken() {
        return getString(GPLUS_ACCESS_TOKEN_KEY, null);
    }

    /**
     * Set google+ access token
     *
     * @param googlePlusAccessToken
     */
    public void setGooglePlusAccessToken(String googlePlusAccessToken) {
        putString(GPLUS_ACCESS_TOKEN_KEY, googlePlusAccessToken).apply();
    }

    /**
     * Remove google+ access token
     */
    public void removeGooglePlusAccessToken() {
        getEditor().remove(GPLUS_ACCESS_TOKEN_KEY).apply();
    }

    /**
     * Get username
     *
     * @return
     */
    public String getUsername() {
        return getString(USERNAME_KEY, null);
    }

    /**
     * Set username
     *
     * @param username
     */
    public void setUsername(String username) {
        putString(USERNAME_KEY, username).apply();
    }

    /**
     * Remove username
     */
    public void removeUsername() {
        getEditor().remove(USERNAME_KEY).apply();
    }

    /**
     * Get password
     *
     * @return
     */
    public String getPassword() {
        return getString(PASSWORD_KEY, null);
    }

    /**
     * Set password
     *
     * @param password
     */
    public void setPassword(String password) {
        putString(PASSWORD_KEY, password).apply();
    }

    /**
     * Remove password
     */
    public void removePassword() {
        getEditor().remove(PASSWORD_KEY).apply();
    }

    /**
     * Get FirstName
     *
     * @return
     */
    public String getFirstName() {
        return getString(FIRSTNAME_KEY, null);
    }

    /**
     * Set FirstName
     *
     * @param firstName
     */
    public void setFirstName(String firstName) {
        putString(FIRSTNAME_KEY, firstName).apply();
    }

    /**
     * Remove FirstName
     */
    public void removeFirstName() {
        getEditor().remove(FIRSTNAME_KEY).apply();
    }

    /**
     * Get LastName
     *
     * @return
     */
    public String getLastName() {
        return getString(LASTNAME_KEY, null);
    }

    /**
     * Set LastName
     *
     * @param lastName
     */
    public void setLastName(String lastName) {
        putString(LASTNAME_KEY, lastName).apply();
    }

    /**
     * Remove LastName
     */
    public void removeLastName() {
        getEditor().remove(LASTNAME_KEY).apply();
    }

    /**
     * Get Email
     *
     * @return
     */
    public String getEmail() {
        return getString(EMAIL_KEY, null);
    }

    /**
     * Set Email
     *
     * @param email
     */
    public void setEmail(String email) {
        putString(EMAIL_KEY, email).apply();
    }

    /**
     * Remove Email
     */
    public void removeEmail() {
        getEditor().remove(EMAIL_KEY).apply();
    }

    /**
     * Get Birthday
     *
     * @return
     */
    public String getBirthday() {
        return getString(BIRTHDAY_KEY, null);
    }

    /**
     * Set Birthday
     *
     * @param birthday
     */
    public void setBirthday(String birthday) {
        putString(BIRTHDAY_KEY, birthday).apply();
    }

    /**
     * Remove Birthday
     */
    public void removeBirthday() {
        getEditor().remove(BIRTHDAY_KEY).apply();
    }

    /**
     * Get contribution
     *
     * @return
     */
    public Integer getContribution() {
        return getInt(CONTRIBUTION_KEY, 0);
    }

    /**
     * Set Email
     *
     * @param contribution
     */
    public void setContribution(Integer contribution) {
        putInt(CONTRIBUTION_KEY, contribution).apply();
    }

    /**
     * Remove Contribution
     */
    public void removeContribution() {
        getEditor().remove(CONTRIBUTION_KEY).apply();
    }

    /**
     * Get Score
     *
     * @return
     */
    public Integer getScore() {
        return getInt(SCORE_KEY, 0);
    }

    /**
     * Set Score
     *
     * @param contribution
     */
    public void setScore(Integer contribution) {
        putInt(SCORE_KEY, contribution).apply();
    }

    /**
     * Remove Score
     */
    public void removeScore() {
        getEditor().remove(SCORE_KEY).apply();
    }
}
