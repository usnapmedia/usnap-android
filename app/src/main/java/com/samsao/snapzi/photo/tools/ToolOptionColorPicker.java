package com.samsao.snapzi.photo.tools;

import android.os.Parcelable;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.hannesdorfmann.parcelableplease.annotation.ParcelableThisPlease;
import com.larswerkman.holocolorpicker.ColorPicker;
import com.samsao.snapzi.R;
import com.samsao.snapzi.photo.MenuItem;
import com.samsao.snapzi.util.StringUtil;

/**
 * @author jfcartier
 * @since 15-04-07
 */
public abstract class ToolOptionColorPicker extends ToolOption implements Parcelable {

    @ParcelableThisPlease
    public int mColor;

    private ToolCallback mToolCallback;
    private MaterialDialog mColorPickerDialog;
    private ColorPicker mColorPicker;

    @Override
    public ToolOption setTool(Tool tool) {
        super.setTool(tool);
        try {
            mToolCallback = (ToolCallback)tool;
        } catch (ClassCastException e) {
            throw new ClassCastException("Tool " + tool.getClass().getName() + " must implement ToolOptionColorPicker.ToolCallback");
        }
        return this;
    }

    @Override
    public MenuItem getMenuItem() {
        return new MenuItem() {
            @Override
            public String getName() {
                return StringUtil.getString(R.string.tool_option_color_name);
            }

            @Override
            public int getImageResource() {
                return 0;
            }

            @Override
            public void onSelected() {
                getColorPickerDialog().show();
            }
        };
    }

    /**
     * Get color picker dialog
     * @return
     */
    public MaterialDialog getColorPickerDialog() {
        if (mColorPickerDialog == null) {
            mColorPickerDialog = new MaterialDialog.Builder(getTool().getToolFragment().getActivity())
                    .customView(R.layout.dialog_color_picker, false)
                    .positiveText(android.R.string.ok)
                    .negativeText(android.R.string.cancel)
                    .callback(new MaterialDialog.ButtonCallback() {
                        @Override
                        public void onPositive(MaterialDialog dialog) {
                            mColor = mColorPicker.getColor();
                            mToolCallback.onColorSelected(mColor);
                        }
                    })
                    .build();
            View view = mColorPickerDialog.getCustomView();
            mColorPicker = (ColorPicker) view.findViewById(R.id.picker);
        }
        mColorPicker.setOldCenterColor(mColor);
        return mColorPickerDialog;
    }

    public int getColor() {
        return mColor;
    }

    public void setColor(int color) {
        mColor = color;
    }

    /**
     * Interface for the tool callback
     */
    public interface ToolCallback {
        public abstract void onColorSelected(int color);
    }
}
