package com.samsao.snapzi.photo.tools;

import android.os.Parcelable;

import com.hannesdorfmann.parcelableplease.annotation.ParcelableThisPlease;
import com.samsao.snapzi.photo.EditFragment;
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
    // FIXME saving tools does not work
    @ParcelableThisPlease
    public ArrayList<ToolOption> mOptions;

    /**
     * Selected option
     */
    @ParcelableThisPlease
    public ToolOption mCurrentOption;

    /**
     * is the tool selected?
     */
    @ParcelableThisPlease
    public boolean mIsSelected;

    /**
     * Associated Fragment
     */
    protected EditFragment mToolFragment;

    /**
     * Constructor
     */
    protected Tool() {
        mOptions = new ArrayList<>();
        mCurrentOption = null;
        mIsSelected = false;
    }

    /**
     * Return the menu item for this tool
     *
     * @return
     */
    public MenuItem getMenuItem() {
        return new MenuItem() {
            @Override
            public String getName() {
                return Tool.this.getName();
            }

            @Override
            public int getImageResource() {
                return Tool.this.getImageResource();
            }

            @Override
            public void onSelected() {
                select();
            }

            @Override
            public boolean isSelected() {
                return mIsSelected;
            }
        };
    }

    /**
     * Return the name of this tool
     *
     * @return
     */
    public abstract String getName();

    /**
     * Return the image resource of this tool
     *
     * @return
     */
    public abstract int getImageResource();

    /**
     * When options item CLEAR is selected
     */
    public abstract void onOptionsClearSelected();

    /**
     * When options item UNDO is selected
     */
    public abstract void onOptionsUndoSelected();

    /**
     * When options item DONE is selected
     */
    public abstract void onOptionsDoneSelected();

    /**
     * When options item HOME is selected
     */
    public abstract void onOptionsHomeSelected();

    /**
     * Select this tool
     */
    public Tool select() {
        if (!mIsSelected) {
            mIsSelected = true;
            mToolFragment.setCurrentTool(this);
            setOptionsMenuItems();
            onSelected();
        }
        return this;
    }

    /**
     * What to do when selected
     */
    public abstract void onSelected();

    /**
     * Unselect this tool
     */
    public Tool unselect() {
        if (mIsSelected) {
            mIsSelected = false;
            selectOption(null);
            onUnselected();
        }
        return this;
    }

    /**
     * What to do when unselected
     */
    public abstract void onUnselected();

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

    public EditFragment getToolFragment() {
        return mToolFragment;
    }

    public Tool setToolFragment(EditFragment toolFragment) {
        mToolFragment = toolFragment;
        return this;
    }

    /**
     * Select an option
     *
     * @param toolOption
     */
    public void selectOption(ToolOption toolOption) {
        mCurrentOption = toolOption;
        for (ToolOption option : mOptions) {
            option.unselect();
        }
        if (toolOption != null) {
            toolOption.select();
        }
    }

    /**
     * Add an option to the options list
     *
     * @param option
     */
    public void addOption(ToolOption option) {
        mOptions.add(option);
    }

    /**
     * Check if there's an option selected
     *
     * @return
     */
    public boolean hasOptionSelected() {
        return mCurrentOption != null;
    }

    /**
     * A Tool must be destroyed to avoid memory leaks
     */
    public void destroy() {
        for (ToolOption option : mOptions) {
            option.destroy();
        }
        mToolFragment = null;
    }
}
