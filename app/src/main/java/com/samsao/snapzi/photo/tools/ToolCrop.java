package com.samsao.snapzi.photo.tools;

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
public class ToolCrop extends Tool implements Parcelable {

    @Override
    public String getName() {
        return StringUtil.getString(R.string.tool_crop_name);
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

    @Override
    public void onOptionsDoneSelected() {

    }

    @Override
    public void onOptionsHomeSelected() {
        if (hasOptionSelected()) {
            selectOption(null);
        } else {
            mToolFragment.resetCurrentTool();
            mToolFragment.resetOptionsMenu();
        }
    }

    /**
     * select() needs to be overridden because Crop tool can't be selected
     * @return
     */
    @Override
    public Tool select() {
        mToolFragment.startCropActivity();
        return this;
    }

    @Override
    public void onSelected() {

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
        ToolCropParcelablePlease.writeToParcel(this, dest, flags);
    }

    public static final Creator<ToolCrop> CREATOR = new Creator<ToolCrop>() {
        public ToolCrop createFromParcel(Parcel source) {
            ToolCrop target = new ToolCrop();
            ToolCropParcelablePlease.readFromParcel(target, source);
            return target;
        }

        public ToolCrop[] newArray(int size) {
            return new ToolCrop[size];
        }
    };
}
