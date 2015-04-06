package com.samsao.snapzi.photo.tools;

import android.os.Parcelable;
import android.view.View;

/**
 * @author jfcartier
 * @since 15-04-06
 */
public abstract class Tool implements Parcelable {
    public abstract String getName();
    public abstract int getImageResource();
    public abstract View.OnClickListener getOnClickListener();
}
