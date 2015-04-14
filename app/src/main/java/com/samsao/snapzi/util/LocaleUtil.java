/*
 * Copyright (c) 2014 Samsao Development Inc.
 */

package com.samsao.snapzi.util;

import com.samsao.snapzi.SnapziApplication;

import java.util.Locale;

/**
 * @author jfcartier
 * @since 2014-07-09
 */
public class LocaleUtil {
    public static Locale getLocale() {
        return SnapziApplication.getContext().getResources().getConfiguration().locale;
    }
}
