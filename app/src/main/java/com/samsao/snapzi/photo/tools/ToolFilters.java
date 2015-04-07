package com.samsao.snapzi.photo.tools;

import android.os.Parcel;

import com.hannesdorfmann.parcelableplease.annotation.ParcelablePlease;
import com.hannesdorfmann.parcelableplease.annotation.ParcelableThisPlease;
import com.samsao.snapzi.R;
import com.samsao.snapzi.photo.MenuItem;
import com.samsao.snapzi.util.StringUtil;

import java.util.ArrayList;

/**
 * @author jfcartier
 * @since 15-04-06
 */
@ParcelablePlease(allFields = false)
public class ToolFilters extends Tool {

    /**
     * List of filters. Filters are also tools.
     */
    @ParcelableThisPlease
    public ArrayList<Tool> mTools;

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
                ArrayList<MenuItem> items = new ArrayList<>();
                for (Tool tool : mTools) {
                    items.add(tool.getMenuItem());
                }
                mMenuContainer.setMenuItems(items);
            }
        };
    }

    public void setTools(ArrayList<Tool> tools) {
        mTools = tools;
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
