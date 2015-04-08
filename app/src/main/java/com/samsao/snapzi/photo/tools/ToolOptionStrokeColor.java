package com.samsao.snapzi.photo.tools;

import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.hannesdorfmann.parcelableplease.annotation.ParcelablePlease;
import com.hannesdorfmann.parcelableplease.annotation.ParcelableThisPlease;
import com.larswerkman.holocolorpicker.ColorPicker;
import com.samsao.snapzi.R;
import com.samsao.snapzi.SnapziApplication;
import com.samsao.snapzi.photo.MenuItem;
import com.samsao.snapzi.util.StringUtil;

/**
 * @author jfcartier
 * @since 15-04-07
 */
@ParcelablePlease(allFields = false)
public class ToolOptionStrokeColor extends ToolOption implements Parcelable {

    public final static int DEFAULT_COLOR = SnapziApplication.getContext().getResources().getColor(android.R.color.holo_red_light);

    @ParcelableThisPlease
    public int mStrokeColor;

    private MaterialDialog mColorPickerDialog;
    private ColorPicker mColorPicker;

    public ToolOptionStrokeColor() {
        super();
        mStrokeColor = DEFAULT_COLOR;
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
                            setStrokeColor(mColorPicker.getColor());
                        }
                    })
                    .build();
            View view = mColorPickerDialog.getCustomView();
            mColorPicker = (ColorPicker) view.findViewById(R.id.picker);
        }
        mColorPicker.setOldCenterColor(mStrokeColor);
        return mColorPickerDialog;
    }

    public int getStrokeColor() {
        return mStrokeColor;
    }

    public void setStrokeColor(int strokeColor) {
        mStrokeColor = strokeColor;
        ((ToolDraw)getTool()).setStrokeColor(mStrokeColor);
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        ToolOptionStrokeColorParcelablePlease.writeToParcel(this, dest, flags);
    }

    public static final Creator<ToolOptionStrokeColor> CREATOR = new Creator<ToolOptionStrokeColor>() {
        public ToolOptionStrokeColor createFromParcel(Parcel source) {
            ToolOptionStrokeColor target = new ToolOptionStrokeColor();
            ToolOptionStrokeColorParcelablePlease.readFromParcel(target, source);
            return target;
        }

        public ToolOptionStrokeColor[] newArray(int size) {
            return new ToolOptionStrokeColor[size];
        }
    };
}
