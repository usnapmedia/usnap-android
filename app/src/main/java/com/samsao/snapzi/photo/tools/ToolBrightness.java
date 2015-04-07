package com.samsao.snapzi.photo.tools;

import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;
import android.widget.SeekBar;

import com.hannesdorfmann.parcelableplease.annotation.ParcelablePlease;
import com.hannesdorfmann.parcelableplease.annotation.ParcelableThisPlease;
import com.samsao.snapzi.R;
import com.samsao.snapzi.photo.MenuItem;
import com.samsao.snapzi.util.StringUtil;

import jp.wasabeef.picasso.transformations.gpu.BrightnessFilterTransformation;

/**
 * @author jfcartier
 * @since 15-04-07
 */
@ParcelablePlease(allFields = false)
public class ToolBrightness extends Tool implements Parcelable {

    @ParcelableThisPlease
    public int mBrightness;

    public ToolBrightness() {
        super();
        // brightness varies from -1.0 to 1.0, but progress bar from 0 to MAX -> initial brightness is 10 (0.0) and max is 20
        mBrightness = 10;
    }

    @Override
    public MenuItem getMenuItem() {
        return new MenuItem() {
            @Override
            public String getName() {
                return StringUtil.getString(R.string.tool_filters_filter_brightness_name);
            }

            @Override
            public int getImageResource() {
                return 0;
            }

            @Override
            public void onSelected() {
                View view = mMenuContainer.replaceToolContainer(R.layout.fragment_photo_edit_tool_seekbar);
                SeekBar seekBar = (SeekBar) view.findViewById(R.id.fragment_photo_edit_tool_seekbar);
                seekBar.setMax(20);
                seekBar.setProgress(mBrightness);
                seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, final int progress, boolean fromUser) {
                        mMenuContainer.refreshImage(new BrightnessFilterTransformation(mMenuContainer.getContext(), (progress - 10) / 10.0f));
                        mBrightness = progress;
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                });
            }
        };
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        ToolBrightnessParcelablePlease.writeToParcel(this, dest, flags);
    }

    public static final Creator<ToolBrightness> CREATOR = new Creator<ToolBrightness>() {
        public ToolBrightness createFromParcel(Parcel source) {
            ToolBrightness target = new ToolBrightness();
            ToolBrightnessParcelablePlease.readFromParcel(target, source);
            return target;
        }

        public ToolBrightness[] newArray(int size) {
            return new ToolBrightness[size];
        }
    };
}
