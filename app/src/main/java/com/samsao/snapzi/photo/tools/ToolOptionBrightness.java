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
public class ToolOptionBrightness extends ToolOption implements Parcelable {

    @ParcelableThisPlease
    public int mBrightness;

    public ToolOptionBrightness() {
        super();
        // brightness varies from -1.0 to 1.0, but progress bar from 0 to MAX -> initial brightness is 10 (0.0) and max is 20
        mBrightness = 10;
    }

    @Override
    public MenuItem getMenuItem() {
        return new MenuItem() {
            @Override
            public String getName() {
                return StringUtil.getString(R.string.tool_option_brightness_name);
            }

            @Override
            public int getImageResource() {
                return 0;
            }

            @Override
            public void onSelected() {
                View view = getTool().getToolFragment().replaceToolContainer(R.layout.fragment_photo_edit_tool_seekbar);
                SeekBar seekBar = (SeekBar) view.findViewById(R.id.fragment_photo_edit_tool_seekbar);
                seekBar.setMax(20);
                seekBar.setProgress(mBrightness);
                seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, final int progress, boolean fromUser) {
                        getTool().getToolFragment().refreshImage(new BrightnessFilterTransformation(getTool().getToolFragment().getActivity(), (progress - 10) / 10.0f));
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
        ToolOptionBrightnessParcelablePlease.writeToParcel(this, dest, flags);
    }

    public static final Creator<ToolOptionBrightness> CREATOR = new Creator<ToolOptionBrightness>() {
        public ToolOptionBrightness createFromParcel(Parcel source) {
            ToolOptionBrightness target = new ToolOptionBrightness();
            ToolOptionBrightnessParcelablePlease.readFromParcel(target, source);
            return target;
        }

        public ToolOptionBrightness[] newArray(int size) {
            return new ToolOptionBrightness[size];
        }
    };
}
