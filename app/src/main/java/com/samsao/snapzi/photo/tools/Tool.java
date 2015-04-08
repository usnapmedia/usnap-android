package com.samsao.snapzi.photo.tools;

import android.os.Parcelable;

import com.hannesdorfmann.parcelableplease.annotation.ParcelableThisPlease;
import com.samsao.snapzi.photo.MenuItem;
import com.samsao.snapzi.photo.PhotoEditFragment;

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
     * Associated Fragment
     */
    protected PhotoEditFragment mToolFragment;

    /**
     * Constructor
     */
    protected Tool() {
        mOptions = new ArrayList<>();
    }

    /**
     * Returns the menu item for this tool
     * @return
     */
    public abstract MenuItem getMenuItem();

    /**
     * When options item CLEAR is selected
     */
    public abstract void onOptionsClearSelected();

    /**
     * When options item UNDO is selected
     */
    public abstract void onOptionsUndoSelected();

    /**
     * Select this tool
     */
    public void select() {
        mToolFragment.setCurrentTool(this, getClearEnabled(), getUndoEnabled());
        setOptionsMenuItems();
    }

    /**
     * Unselect this tool
     */
    public abstract void unselect();

    /**
     * Check if CLEAR option is available for this tool
     * @return
     */
    public abstract boolean getClearEnabled();

    /**
     * Check if UNDO option is available for this tool
     * @return
     */
    public abstract boolean getUndoEnabled();

    /**
     * Set the options as menu items
     */
    public void setOptionsMenuItems() {
        ArrayList<MenuItem> items = new ArrayList<>();
        for (ToolOption option : mOptions) {
            items.add(option.getMenuItem());
        }
        mToolFragment.setMenuItems(items);
    }

    public PhotoEditFragment getToolFragment() {
        return mToolFragment;
    }

    public Tool setToolFragment(PhotoEditFragment toolFragment) {
        mToolFragment = toolFragment;
        return this;
    }

    public void addOption(ToolOption option) {
        mOptions.add(option);
    }
}
