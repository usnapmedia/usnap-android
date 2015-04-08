package com.samsao.snapzi.photo.tools;

import android.os.Parcelable;

import com.samsao.snapzi.photo.MenuItem;

/**
 * @author jfcartier
 * @since 15-04-06
 */
public abstract class ToolOption implements Parcelable {
    protected Tool mTool;

    /**
     * Returns the menu item for this tool option
      * @return
     */
    public abstract MenuItem getMenuItem();

    public Tool getTool() {
        return mTool;
    }

    public ToolOption setTool(Tool tool) {
        mTool = tool;
        return this;
    }
}
