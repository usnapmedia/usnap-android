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
    private static final String PREFERENCES_FILE_KEY = "com.samsao.snapzi.PREFERENCE_FILE_KEY";
    private static final String FACEBOOK_ACCESS_TOKEN_KEY = "com.samsao.snapzi.preference.FACEBOOK_ACCESS_TOKEN_KEY";
    private static final String TWITTER_ACCESS_TOKEN_KEY = "com.samsao.snapzi.preference.TWITTER_ACCESS_TOKEN_KEY";
    private static final String GPLUS_ACCESS_TOKEN_KEY = "com.samsao.snapzi..preference.GPLUS_ACCESS_TOKEN_KEY";

    private static SharedPreferences getSharedPreferences() {
        return SnapziApplication.getContext().getSharedPreferences(PREFERENCES_FILE_KEY, Context.MODE_PRIVATE);
    }

    private static SharedPreferences.Editor getEditor() {
        return getSharedPreferences().edit();
    }

    private static boolean contains(String key) {
        return getSharedPreferences().contains(key);
    }

    private static Map<String, ?> getAll() {
        return getSharedPreferences().getAll();
    }

    private static boolean getBoolean(String key, boolean defValue) {
        return getSharedPreferences().getBoolean(key, defValue);
    }

    private static float getFloat(String key, float defValue) {
        return getSharedPreferences().getFloat(key, defValue);
    }

    private static int getInt(String key, int defValue) {
        return getSharedPreferences().getInt(key, defValue);
    }

    private static long getLong(String key, long defValue) {
        return getSharedPreferences().getLong(key, defValue);
    }

    private static String getString(String key, String defValue) {
        return getSharedPreferences().getString(key, defValue);
    }

    private static Set<String> getSetString(String key, Set<String> defValue) {
        return getSharedPreferences().getStringSet(key, defValue);
    }

    public static void registerOnSharedPreferenceChangeListener(SharedPreferences.OnSharedPreferenceChangeListener listener) {
        getSharedPreferences().registerOnSharedPreferenceChangeListener(listener);
    }

    public static void unregisterOnSharedPreferenceChangeListener(SharedPreferences.OnSharedPreferenceChangeListener listener) {
        getSharedPreferences().unregisterOnSharedPreferenceChangeListener(listener);
    }

    public static void clear() {
        getEditor().clear().apply();
    }

    private static SharedPreferences.Editor putBoolean(String key, boolean value) {
        return getEditor().putBoolean(key, value);
    }

    private static SharedPreferences.Editor putFloat(String key, float value) {
        return getEditor().putFloat(key, value);
    }

    private static SharedPreferences.Editor putInt(String key, int value) {
        return getEditor().putInt(key, value);
    }

    private static SharedPreferences.Editor putLong(String key, long value) {
        return getEditor().putLong(key, value);
    }

    private static SharedPreferences.Editor putString(String key, String value) {
        return getEditor().putString(key, value);
    }

    private static SharedPreferences.Editor putStringSet(String key, Set<String> value) {
        return getEditor().putStringSet(key, value);
    }

    public static SharedPreferences.Editor remove(String key) {
        return getEditor().remove(key);
    }

    /**
     * START PUTTING GETTER/SETTER METHODS HERE
     */
    public static String getFacebookAccessToken() {
        return getString(FACEBOOK_ACCESS_TOKEN_KEY, null);
    }

    public static void setFacebookAccessToken(String facebookAccessToken) {
        SharedPreferences.Editor editor = putString(FACEBOOK_ACCESS_TOKEN_KEY, facebookAccessToken);
        editor.apply();
    }
}
