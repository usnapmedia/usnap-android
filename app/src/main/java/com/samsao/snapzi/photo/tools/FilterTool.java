package com.samsao.snapzi.photo.tools;

import android.os.Parcel;
import android.view.View;

import com.samsao.snapzi.R;
import com.samsao.snapzi.photo.MenuItem;

/**
 * @author jfcartier
 * @since 15-04-06
 */
public class FilterTool extends Tool {

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }

    @Override
    public MenuItem getMenuItem() {
        return new MenuItem() {
            @Override
            public String getName() {
                return getString(R.string.tool_filters_name);
            }

            @Override
            public int getImageResource() {
                return 0;
            }

            @Override
            public View.OnClickListener getOnClickListener() {
                return null;
            }
        };
    }
}
