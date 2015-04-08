package com.samsao.snapzi.photo.tools;

import android.os.Parcel;
import android.os.Parcelable;

import com.hannesdorfmann.parcelableplease.annotation.ParcelablePlease;
import com.hannesdorfmann.parcelableplease.annotation.ParcelableThisPlease;
import com.samsao.snapzi.photo.MenuItem;

/**
 * @author jfcartier
 * @since 15-04-07
 */
@ParcelablePlease(allFields = false)
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
    public MenuItem getMenuItem() {
        return new MenuItem() {
            @Override
            public String getName() {
                return Integer.toString((int)mStrokeWidth/10);
            }

            @Override
            public int getImageResource() {
                return 0;
            }

            @Override
            public void onSelected() {
                ((ToolDraw)getTool()).setStrokeWidth(mStrokeWidth);
            }
        };
    }

    public float getStrokeWidth() {
        return mStrokeWidth;
    }

    public ToolOptionStrokeWidth setStrokeWidth(float strokeWidth) {
        mStrokeWidth = strokeWidth;
        return this;
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
