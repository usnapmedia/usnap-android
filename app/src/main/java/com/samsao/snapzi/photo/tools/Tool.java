package com.samsao.snapzi.photo.tools;

import android.os.Parcelable;

import com.hannesdorfmann.parcelableplease.annotation.ParcelableThisPlease;
import com.samsao.snapzi.photo.MenuContainer;
import com.samsao.snapzi.photo.MenuItem;

import java.util.ArrayList;

/**
 * @author jfcartier
 * @since 15-04-06
 */
public abstract class Tool implements Parcelable {
    /**
     * List of options
     */
    @ParcelableThisPlease
    public ArrayList<ToolOption> mOptions;

    /**
     * Menu container
     */
    protected MenuContainer mMenuContainer;

    /**
     * Returns the menu item for this tool
      * @return
     */
    public abstract MenuItem getMenuItem();
    public abstract void onOptionsClearSelected();
    public abstract void onOptionsUndoSelected();

    public MenuContainer getMenuContainer() {
        return mMenuContainer;
    }

    public Tool setMenuContainer(MenuContainer menuContainer) {
        mMenuContainer = menuContainer;
        return this;
    }

    public ArrayList<ToolOption> getOptions() {
        return mOptions;
    }

    public void setOptions(ArrayList<ToolOption> options) {
        mOptions = options;
    }

    public void addOption(ToolOption option) {
        if (mOptions == null) {
            mOptions = new ArrayList<>();
        }
        mOptions.add(option);
    }
}
