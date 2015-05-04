package com.samsao.snapzi.util;

import android.text.SpannableStringBuilder;
import android.text.Spanned;

import com.samsao.snapzi.SnapziApplication;

import uk.co.chrisjenx.calligraphy.CalligraphyTypefaceSpan;
import uk.co.chrisjenx.calligraphy.TypefaceUtils;

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

    /**
     * Returns a SpannableString with the app font
     * @param resId
     * @return
     */
    public static SpannableStringBuilder getAppFontString(int resId) {
        SpannableStringBuilder sBuilder = new SpannableStringBuilder();
        sBuilder.append(SnapziApplication.getContext().getString(resId));
        CalligraphyTypefaceSpan typefaceSpan = new CalligraphyTypefaceSpan(TypefaceUtils.load(SnapziApplication.getContext().getAssets(), "fonts/GothamHTF-Book.ttf"));
        sBuilder.setSpan(typefaceSpan, 0, sBuilder.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        return sBuilder;
    }
}
