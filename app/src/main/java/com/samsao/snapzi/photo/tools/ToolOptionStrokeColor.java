package com.samsao.snapzi.photo.tools;

import android.os.Parcel;
import android.os.Parcelable;

import com.hannesdorfmann.parcelableplease.annotation.ParcelablePlease;
import com.samsao.snapzi.SnapziApplication;

/**
 * @author jfcartier
 * @since 15-04-07
 */
@ParcelablePlease
public class ToolOptionStrokeColor extends ToolOptionColorPicker implements Parcelable {

    public final static int DEFAULT_COLOR = SnapziApplication.getContext().getResources().getColor(android.R.color.holo_red_light);

    public ToolOptionStrokeColor() {
        super();
        mColor = DEFAULT_COLOR;
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
