package com.samsao.snapzi.photo.tools;

import android.os.Parcelable;

import com.samsao.snapzi.photo.MenuContainer;
import com.samsao.snapzi.photo.MenuItem;

/**
 * @author jfcartier
 * @since 15-04-06
 */
public abstract class Tool implements Parcelable {
    protected MenuContainer mMenuContainer;

    /**
     * Returns the menu item for this tool
      * @return
     */
    public abstract MenuItem getMenuItem();

    public Tool setMenuContainer(MenuContainer menuContainer) {
        mMenuContainer = menuContainer;
        return this;
    }
}
