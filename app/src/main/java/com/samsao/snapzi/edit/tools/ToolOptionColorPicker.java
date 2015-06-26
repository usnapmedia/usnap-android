package com.samsao.snapzi.edit.tools;

import android.app.FragmentManager;
import android.os.Parcelable;

import com.hannesdorfmann.parcelableplease.annotation.ParcelableThisPlease;
import com.larswerkman.holocolorpicker.ColorPicker;
import com.samsao.snapzi.R;
import com.samsao.snapzi.edit.MenuItem;
import com.samsao.snapzi.util.StringUtil;

/**
 * @author jfcartier
 * @since 15-04-07
 */
public abstract class ToolOptionColorPicker extends ToolOption implements Parcelable, ToolColorPickerDialogFragment.Listener {

    @ParcelableThisPlease
    public int mColor;
    private ToolCallback mToolCallback;
    private ToolColorPickerDialogFragment mColorPickerDialog;
    private ColorPicker mColorPicker;

    @Override
    public ToolOption setTool(Tool tool) {
        super.setTool(tool);
        try {
            mToolCallback = (ToolCallback) tool;
        } catch (ClassCastException e) {
            throw new ClassCastException("Tool " + tool.getClass().getName() + " must implement ToolOptionColorPicker.ToolCallback");
        }
        return this;
    }

    private FragmentManager getMyFragmentManager() {
        FragmentManager fragmentManager = mTool.getToolFragment().getFragmentManager();
        return fragmentManager;
    }
    /**
     * We need to override this method because this option can't be selected
     *
     * @return
     */
    @Override
    public MenuItem getMenuItem() {
        return new MenuItem() {
            @Override
            public String getName() {
                return ToolOptionColorPicker.this.getName();
            }

            @Override
            public int getImageResource() {
                return ToolOptionColorPicker.this.getImageResource();
            }

            @Override
            public void onSelected() {
//                getColorPickerDialog().show(getMyFragmentManager(), ToolColorPickerDialogFragment.ToolColorPickerDialogFragment_TAG);
                getMyFragmentManager().beginTransaction().add(getColorPickerDialog(), ToolColorPickerDialogFragment.ToolColorPickerDialogFragment_TAG).commitAllowingStateLoss();
            }

            @Override
            public boolean isSelected() {
                return mIsSelected;
            }
        };
    }

    @Override
    public void onSelected() {

    }

    @Override
    public void onUnselected() {

    }

    @Override
    public String getName() {
        return StringUtil.getString(R.string.tool_option_color_name);
    }

    @Override
    public int getImageResource() {
        return R.drawable.ic_colour;
    }

    /**
     * Get color picker dialogFragment
     *
     * @return
     */
    public ToolColorPickerDialogFragment getColorPickerDialog() {
        if (mColorPickerDialog == null) {
            mColorPickerDialog = ToolColorPickerDialogFragment.newInstance(this);
        }
        return mColorPickerDialog;
    }

    public int getColor() {
        return mColor;
    }

    public void setColor(int color) {
        mColor = color;
    }

    public ToolCallback getToolCallback() {
        return mToolCallback;
    }

    public ColorPicker getColorPicker() {
        return mColorPicker;
    }

    @Override
    public void onOptionsClearSelected() {

    }

    @Override
    public void onOptionsUndoSelected() {

    }

    @Override
    public void onOptionsDoneSelected() {

    }

    @Override
    public void onOptionsHomeSelected() {

    }

    /**
     * Interface for the tool callback
     */
    public interface ToolCallback {
        void onColorSelected(int color);
    }
}
