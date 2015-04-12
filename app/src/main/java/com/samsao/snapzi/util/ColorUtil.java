package com.samsao.snapzi.util;

import android.util.TypedValue;

import com.samsao.snapzi.R;
import com.samsao.snapzi.SnapziApplication;

/**
 * @author jfcartier
 * @since 15-04-08
 */
public class ColorUtil {

    /**
     * Returns the theme's primary color
     * @return
     */
    public static int getPrimaryColor() {
        TypedValue typedValue = new TypedValue();
        SnapziApplication.getContext().getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true);
        return SnapziApplication.getContext().getResources().getColor(typedValue.data);
    }
}
