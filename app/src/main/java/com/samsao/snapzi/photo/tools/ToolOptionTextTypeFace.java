package com.samsao.snapzi.photo.tools;

import android.graphics.Typeface;
import android.os.Parcel;
import android.os.Parcelable;

import com.hannesdorfmann.parcelableplease.annotation.ParcelableNoThanks;
import com.hannesdorfmann.parcelableplease.annotation.ParcelablePlease;
import com.hannesdorfmann.parcelableplease.annotation.ParcelableThisPlease;
import com.samsao.snapzi.SnapziApplication;

import java.util.Locale;

/**
 * @author jfcartier
 * @since 15-04-07
 */
@ParcelablePlease(ignorePrivateFields = true)
public class ToolOptionTextTypeFace extends ToolOption implements Parcelable {

    @ParcelableThisPlease
    public String mTypeFaceName;
    @ParcelableNoThanks
    private ToolCallback mToolCallback;

    @Override
    public ToolOption setTool(Tool tool) {
        super.setTool(tool);
        try {
            mToolCallback = (ToolCallback)tool;
        } catch (ClassCastException e) {
            throw new ClassCastException("Tool " + tool.getClass().getName() + " must implement ToolOptionTextTypeFace.ToolCallback");
        }
        return this;
    }

    @Override
    public void onSelected() {
        Typeface font = Typeface.createFromAsset(SnapziApplication.getContext().getAssets(), "fonts/" + mTypeFaceName);
        mToolCallback.setTypeFace(font);
    }

    @Override
    public void onUnselected() {

    }

    @Override
    public String getName() {
        // remove the file type and replace "_" with spaces
        String name = mTypeFaceName;
        int index = mTypeFaceName.indexOf('.');
        if (index > 0) {
            name = mTypeFaceName.substring(0, index);
        }
        name.replaceAll("_", " ");
        return name.toLowerCase(Locale.US);
    }

    @Override
    public int getImageResource() {
        return 0;
    }

    public ToolOptionTextTypeFace setTypeFaceName(String typeFaceName) {
        mTypeFaceName = typeFaceName;
        return this;
    }

    /**
     * Interface for the tool callback
     */
    public interface ToolCallback {
        public abstract void setTypeFace(Typeface font);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        ToolOptionTextTypeFaceParcelablePlease.writeToParcel(this, dest, flags);
    }

    public static final Creator<ToolOptionTextTypeFace> CREATOR = new Creator<ToolOptionTextTypeFace>() {
        public ToolOptionTextTypeFace createFromParcel(Parcel source) {
            ToolOptionTextTypeFace target = new ToolOptionTextTypeFace();
            ToolOptionTextTypeFaceParcelablePlease.readFromParcel(target, source);
            return target;
        }

        public ToolOptionTextTypeFace[] newArray(int size) {
            return new ToolOptionTextTypeFace[size];
        }
    };
}
