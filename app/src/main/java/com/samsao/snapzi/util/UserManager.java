package com.samsao.snapzi.util;

import android.text.TextUtils;

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
        // TODO add other social networks
        return !TextUtils.isEmpty(PreferenceManager.getFacebookAccessToken());
    }
}
