package com.samsao.snapzi.photo.tools;

import android.os.Parcelable;

import com.samsao.snapzi.SnapziApplication;
import com.samsao.snapzi.photo.MenuContainer;
import com.samsao.snapzi.photo.MenuItem;

/**
 * @author jfcartier
 * @since 15-04-06
 */
public abstract class Tool implements Parcelable {
    protected MenuContainer mMenuContainer;

    public Tool(MenuContainer menuContainer) {
        mMenuContainer = menuContainer;
    }

    /**
     * Returns the menu item for this tool
      * @return
     */
    public abstract MenuItem getMenuItem();

    /**
     * Helper method to get a string
     * @param resId
     * @return
     */
    protected String getString(int resId) {
        return SnapziApplication.getContext().getString(resId);
    }
}
