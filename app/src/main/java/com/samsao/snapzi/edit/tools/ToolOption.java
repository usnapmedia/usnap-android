package com.samsao.snapzi.edit.tools;

import android.os.Parcelable;

import com.hannesdorfmann.parcelableplease.annotation.ParcelableNoThanks;
import com.hannesdorfmann.parcelableplease.annotation.ParcelableThisPlease;
import com.samsao.snapzi.edit.MenuItem;

/**
 * @author jfcartier
 * @since 15-04-06
 */
public abstract class ToolOption implements Parcelable {
    // tool will be set by the tool itself
    @ParcelableNoThanks
    public Tool mTool;
    @ParcelableThisPlease
    public Boolean mIsSelected;

    public ToolOption() {
        mIsSelected = false;
    }

    /**
     * Select this tool option
     *
     * @param force If set to TRUE, force selection, that is select the tool even if mIsSelected is true
     * @return
     */
    public ToolOption select(boolean force) {
        if (force) {
            mIsSelected = false;
        }

        if (!mIsSelected) {
            mIsSelected = true;
            onSelected();
        }
        return this;
    }

    /**
     * Select this tool option
     */
    public ToolOption select() {
        return select(false);
    }

    /**
     * What to do when selected
     */
    public abstract void onSelected();

    /**
     * Unselect this tool option
     */
    public ToolOption unselect() {
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
     * Get the menu item associated with this tool option
     *
     * @return
     */
    public MenuItem getMenuItem() {
        return new MenuItem() {
            @Override
            public String getName() {
                return ToolOption.this.getName();
            }

            @Override
            public int getImageResource() {
                return ToolOption.this.getImageResource();
            }

            @Override
            public void onSelected() {
                mTool.selectOption(ToolOption.this);
            }

            @Override
            public boolean isSelected() {
                return mIsSelected;
            }
        };
    }

    /**
     * Returns the option's name
     *
     * @return
     */
    public abstract String getName();

    /**
     * Returns the option's name
     *
     * @return
     */
    public abstract int getImageResource();

    /**
     * Set the option's tool
     *
     * @param tool
     * @return
     */
    public ToolOption setTool(Tool tool) {
        mTool = tool;
        return this;
    }

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
}
