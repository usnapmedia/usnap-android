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

import jp.wasabeef.picasso.transformations.gpu.ContrastFilterTransformation;

/**
 * @author jfcartier
 * @since 15-04-07
 */
@ParcelablePlease(allFields = false)
public class ToolContrast extends ToolOption implements Parcelable {

    @ParcelableThisPlease
    public int mConstrast;

    public ToolContrast() {
        super();
        // contrast varies from 0 to 4.0, but progress bar from 0 to MAX -> initial contrast is 10 (1.0) and max is 40
        mConstrast = 10;
    }

    @Override
    public MenuItem getMenuItem() {
        return new MenuItem() {
            @Override
            public String getName() {
                return StringUtil.getString(R.string.tool_filters_filter_contrast_name);
            }

            @Override
            public int getImageResource() {
                return 0;
            }

            @Override
            public void onSelected() {
                View view = getTool().getMenuContainer().replaceToolContainer(R.layout.fragment_photo_edit_tool_seekbar);
                SeekBar seekBar = (SeekBar) view.findViewById(R.id.fragment_photo_edit_tool_seekbar);
                seekBar.setMax(40);
                seekBar.setProgress(mConstrast);
                seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, final int progress, boolean fromUser) {
                        getTool().getMenuContainer().refreshImage(new ContrastFilterTransformation(getTool().getMenuContainer().getContext(), progress / 10.0f));
                        mConstrast = progress;
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
        ToolContrastParcelablePlease.writeToParcel(this, dest, flags);
    }

    public static final Creator<ToolContrast> CREATOR = new Creator<ToolContrast>() {
        public ToolContrast createFromParcel(Parcel source) {
            ToolContrast target = new ToolContrast();
            ToolContrastParcelablePlease.readFromParcel(target, source);
            return target;
        }

        public ToolContrast[] newArray(int size) {
            return new ToolContrast[size];
        }
    };
}
