package com.samsao.snapzi.photo.tools;

import android.os.Parcel;
import android.view.View;

/**
 * @author jfcartier
 * @since 15-04-06
 */
public class FilterTool extends Tool {

    @Override
    public String getName() {
        return null;
    }

    @Override
    public int getImageResource() {
        return 0;
    }

    @Override
    public View.OnClickListener getOnClickListener() {
        return null;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }
}
