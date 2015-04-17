package com.samsao.snapzi.edit.tools;

import android.os.Parcel;
import android.os.Parcelable;

import com.hannesdorfmann.parcelableplease.annotation.ParcelablePlease;
import com.samsao.snapzi.SnapziApplication;


/**
 * @author jfcartier
 * @since 15-04-07
 */
@ParcelablePlease
public class ToolOptionTextColor extends ToolOptionColorPicker implements Parcelable {

    public final static int DEFAULT_COLOR = SnapziApplication.getContext().getResources().getColor(android.R.color.white);

    public ToolOptionTextColor() {
        super();
        mColor = DEFAULT_COLOR;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        ToolOptionTextColorParcelablePlease.writeToParcel(this, dest, flags);
    }

    public static final Creator<ToolOptionTextColor> CREATOR = new Creator<ToolOptionTextColor>() {
        public ToolOptionTextColor createFromParcel(Parcel source) {
            ToolOptionTextColor target = new ToolOptionTextColor();
            ToolOptionTextColorParcelablePlease.readFromParcel(target, source);
            return target;
        }

        public ToolOptionTextColor[] newArray(int size) {
            return new ToolOptionTextColor[size];
        }
    };
}
