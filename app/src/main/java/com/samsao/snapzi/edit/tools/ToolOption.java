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
     */
    public ToolOption select() {
        if (!mIsSelected) {
            mIsSelected = true;
            mTool.getToolFragment().notifyMenuItemAdapterDataSetChanged();
            onSelected();
        }
        return this;
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
            mTool.getToolFragment().notifyMenuItemAdapterDataSetChanged();
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
     * @return
     */
    public abstract String getName();

    /**
     * Returns the option's name
     * @return
     */
    public abstract int getImageResource();

    /**
     * Set the option's tool
     * @param tool
     * @return
     */
    public ToolOption setTool(Tool tool) {
        mTool = tool;
        return this;
    }
}
