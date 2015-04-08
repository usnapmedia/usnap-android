package com.samsao.snapzi.photo.tools;

import android.os.Parcel;
import android.os.Parcelable;

import com.hannesdorfmann.parcelableplease.annotation.ParcelablePlease;
import com.samsao.snapzi.R;
import com.samsao.snapzi.photo.MenuItem;
import com.samsao.snapzi.util.StringUtil;

/**
 * @author jfcartier
 * @since 15-04-06
 */
@ParcelablePlease(allFields = false)
public class ToolFilters extends Tool implements Parcelable {

    public ToolFilters() {
        super();
        addOption(new ToolOptionBrightness().setTool(this));
        addOption(new ToolOptionContrast().setTool(this));
    }

    @Override
    public MenuItem getMenuItem() {
        return new MenuItem() {
            @Override
            public String getName() {
                return StringUtil.getString(R.string.tool_filters_name);
            }

            @Override
            public int getImageResource() {
                return 0;
            }

            @Override
            public void onSelected() {
                select();
            }
        };
    }

    @Override
    public void onOptionsClearSelected() {

    }

    @Override
    public void onOptionsUndoSelected() {

    }

    @Override
    public void unselect() {

    }

    @Override
    public boolean getClearEnabled() {
        return false;
    }

    @Override
    public boolean getUndoEnabled() {
        return false;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        ToolFiltersParcelablePlease.writeToParcel(this, dest, flags);
    }

    public static final Creator<ToolFilters> CREATOR = new Creator<ToolFilters>() {
        public ToolFilters createFromParcel(Parcel source) {
            ToolFilters target = new ToolFilters();
            ToolFiltersParcelablePlease.readFromParcel(target, source);
            return target;
        }

        public ToolFilters[] newArray(int size) {
            return new ToolFilters[size];
        }
    };
}
