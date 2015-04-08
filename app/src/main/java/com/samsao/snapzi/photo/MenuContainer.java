package com.samsao.snapzi.photo;

import android.content.Context;
import android.view.View;

import com.samsao.snapzi.photo.tools.Tool;
import com.squareup.picasso.Transformation;

import java.util.ArrayList;

/**
 * @author jfcartier
 * @since 15-04-06
 */
public interface MenuContainer {
    void setCurrentTool(Tool currentTool, boolean enableClear, boolean enableUndo);
    void setMenuItems(ArrayList<MenuItem> items);
    View replaceToolContainer(int resId);
    void refreshImage(Transformation transformation);
    Context getContext();
}
