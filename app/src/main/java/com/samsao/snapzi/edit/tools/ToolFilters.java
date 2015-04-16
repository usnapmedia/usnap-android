package com.samsao.snapzi.edit.tools;

import android.os.Parcel;
import android.os.Parcelable;

import com.hannesdorfmann.parcelableplease.annotation.ParcelablePlease;
import com.samsao.snapzi.R;
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
    public String getName() {
        return StringUtil.getString(R.string.tool_filters_name);
    }

    @Override
    public int getImageResource() {
        return 0;
    }

    @Override
    public void onOptionsClearSelected() {

    }

    @Override
    public void onOptionsUndoSelected() {

    }

    /**
     * When options item DONE is selected
     */
    @Override
    public void onOptionsDoneSelected() {
        getToolFragment().saveImage();
        selectOption(null);
    }

    /**
     * When options item HOME is selected
     */
    @Override
    public void onOptionsHomeSelected() {
        if (hasOptionSelected()) {
            selectOption(null);
        } else {
            getToolFragment().resetCurrentTool();
            getToolFragment().resetOptionsMenu();
        }
    }

    @Override
    public void onSelected() {
        getToolFragment().showEditOptionsMenu(false, false, false);
    }

    @Override
    public void onUnselected() {

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
            target.setOptionsTool();
            return target;
        }

        public ToolFilters[] newArray(int size) {
            return new ToolFilters[size];
        }
    };
}
