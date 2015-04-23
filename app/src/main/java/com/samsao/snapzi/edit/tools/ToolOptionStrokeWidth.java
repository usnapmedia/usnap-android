package com.samsao.snapzi.edit.tools;

import android.os.Parcel;
import android.os.Parcelable;

import com.hannesdorfmann.parcelableplease.annotation.ParcelablePlease;
import com.hannesdorfmann.parcelableplease.annotation.ParcelableThisPlease;


/**
 * @author jfcartier
 * @since 15-04-07
 */
@ParcelablePlease
public class ToolOptionStrokeWidth extends ToolOption implements Parcelable {

    public static final float STROKE_WIDTH_SMALL = 10.0f;
    public static final float STROKE_WIDTH_MEDIUM = 20.0f;
    public static final float STROKE_WIDTH_LARGE = 30.0f;

    @ParcelableThisPlease
    public float mStrokeWidth;

    public ToolOptionStrokeWidth() {
        super();
        mStrokeWidth = STROKE_WIDTH_MEDIUM;
    }

    @Override
    public void onSelected() {
        ((ToolDraw) mTool).setStrokeWidth(mStrokeWidth);
    }

    @Override
    public void onUnselected() {

    }

    @Override
    public String getName() {
        return Integer.toString((int) mStrokeWidth / 10);
    }

    @Override
    public int getImageResource() {
        return 0;
    }

    public float getStrokeWidth() {
        return mStrokeWidth;
    }

    public ToolOptionStrokeWidth setStrokeWidth(float strokeWidth) {
        mStrokeWidth = strokeWidth;
        return this;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        ToolOptionStrokeWidthParcelablePlease.writeToParcel(this, dest, flags);
    }

    public static final Creator<ToolOptionStrokeWidth> CREATOR = new Creator<ToolOptionStrokeWidth>() {
        public ToolOptionStrokeWidth createFromParcel(Parcel source) {
            ToolOptionStrokeWidth target = new ToolOptionStrokeWidth();
            ToolOptionStrokeWidthParcelablePlease.readFromParcel(target, source);
            return target;
        }

        public ToolOptionStrokeWidth[] newArray(int size) {
            return new ToolOptionStrokeWidth[size];
        }
    };
}
