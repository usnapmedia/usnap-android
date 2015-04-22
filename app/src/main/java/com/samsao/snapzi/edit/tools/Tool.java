package com.samsao.snapzi.edit.tools;

import android.os.Parcelable;

import com.hannesdorfmann.parcelableplease.annotation.ParcelableNoThanks;
import com.hannesdorfmann.parcelableplease.annotation.ParcelableThisPlease;
import com.samsao.snapzi.edit.EditFragment;
import com.samsao.snapzi.edit.MenuItem;

import java.lang.ref.WeakReference;
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
    @ParcelableNoThanks
    protected WeakReference<EditFragment> mToolFragment;

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
     * @param force If set to TRUE, force selection, that is select the tool even if mIsSelected is true
     * @return
     */
    public Tool select(boolean force) {
        if (force) {
            mIsSelected = false;
        }

        if (!mIsSelected) {
            mIsSelected = true;
            mToolFragment.get().setCurrentTool(this);
            setOptionsMenuItems();
            // select the currently selected option if restoring from a saved state
            if (mCurrentOption != null) {
                mCurrentOption.select(true); // force selection
            }
            onSelected();
        }
        return this;
    }

    /**
     * Select this tool
     */
    public Tool select() {
        return select(false);
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
        mToolFragment.get().setMenuItems(items);
    }

    public EditFragment getToolFragment() {
        return mToolFragment.get();
    }

    public Tool setToolFragment(EditFragment toolFragment) {
        if (mToolFragment != null && mToolFragment.get() != null) {
            mToolFragment.clear();
        }
        mToolFragment = new WeakReference<>(toolFragment);
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
        mToolFragment.get().notifyMenuItemAdapterDataSetChanged();
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
        mToolFragment.clear();
        mToolFragment = null;
    }
}
