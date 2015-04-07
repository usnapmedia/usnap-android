package com.samsao.snapzi.photo;

import android.view.View;

/**
 * @author jfcartier
 * @since 15-04-06
 */
public interface MenuItem {
    public String getName();
    public int getImageResource();
    public View.OnClickListener getOnClickListener();
}
