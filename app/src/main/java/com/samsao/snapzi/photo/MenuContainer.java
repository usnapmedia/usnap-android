package com.samsao.snapzi.photo;

import android.content.Context;
import android.view.View;

import com.squareup.picasso.Transformation;

import java.util.ArrayList;

/**
 * @author jfcartier
 * @since 15-04-06
 */
public interface MenuContainer {
    void setMenuItems(ArrayList<MenuItem> items);
    void resetMenu();
    View replaceToolContainer(int resId);
    void refreshImage(Transformation transformation);
    Context getContext();
}
