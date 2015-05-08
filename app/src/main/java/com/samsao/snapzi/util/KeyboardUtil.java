/*
 * Copyright (c) 2014 Samsao Development Inc.
 */

package com.samsao.snapzi.util;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.samsao.snapzi.SnapziApplication;

/**
 * @author jfcartier
 * @since 2014-07-24
 */
public class KeyboardUtil {

    /**
     * Show soft keyboard for a given view
     *
     * @param activity
     * @param view
     */
    public static void showKeyboard(Activity activity, View view) {
        final InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
    }

    /**
     * Hide soft keyboard
     *
     * @param activity
     */
    public static void hideKeyboard(Activity activity) {
        View viewWithFocus = activity.getCurrentFocus(); // check if a view has focus
        if (viewWithFocus != null) {
            InputMethodManager inputManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(viewWithFocus.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    /**
     * Hide soft keyboard
     *
     * @param view
     */
    public static void hideKeyboard(View view) {
        InputMethodManager inputManager = (InputMethodManager) SnapziApplication.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        view.clearFocus();
    }
}
