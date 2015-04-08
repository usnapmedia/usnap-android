package com.samsao.snapzi.photo.tools;

import android.os.Parcelable;

import com.samsao.snapzi.photo.MenuItem;

/**
 * @author jfcartier
 * @since 15-04-06
 */
public abstract class ToolOption implements Parcelable {
    public Tool mTool;
    public boolean mIsSelected;

    public ToolOption() {
        mIsSelected = false;
    }

    /**
     * Select this tool option
     */
    public void select() {
        if (!mIsSelected) {
            mIsSelected = true;
            onSelected();
        }
    }

    /**
     * What to do when selected
     */
    public abstract void onSelected();

    /**
     * Unselect this tool option
     */
    public void unselect() {
        if (mIsSelected) {
            mIsSelected = false;
            onUnselected();
        }
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
