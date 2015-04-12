package com.samsao.snapzi.util;

import com.samsao.snapzi.SnapziApplication;

/**
 * @author jfcartier
 * @since 15-04-07
 */
public class StringUtil {

    /**
     * Helper method to get a string
     * @param resId
     * @return
     */
    public static String getString(int resId) {
        return SnapziApplication.getContext().getString(resId);
    }
}
