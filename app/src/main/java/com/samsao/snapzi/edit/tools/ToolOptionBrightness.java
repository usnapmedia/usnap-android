package com.samsao.snapzi.edit.tools;

import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;
import android.widget.SeekBar;

import com.hannesdorfmann.parcelableplease.annotation.ParcelableNoThanks;
import com.hannesdorfmann.parcelableplease.annotation.ParcelablePlease;
import com.hannesdorfmann.parcelableplease.annotation.ParcelableThisPlease;
import com.samsao.snapzi.R;
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
    @ParcelableNoThanks
    // brightness varies from -1.0 to 1.0, but progress bar from 0 to MAX -> initial brightness is 10 (0.0) and max is 20
    private final int INITIAL_BRIGHTNESS = 10;
    @ParcelableNoThanks
    private SeekBar mSeekBar;


    public ToolOptionBrightness() {
        super();
        mBrightness = INITIAL_BRIGHTNESS;
    }

    @Override
    public void onSelected() {
        mTool.getToolFragment().hideMenu();
        mTool.getToolFragment().showEditOptionsMenu(true, true, false, false);
        View view = mTool.getToolFragment().showToolContainer(R.layout.fragment_edit_tool_seekbar);
        mSeekBar = (SeekBar) view.findViewById(R.id.fragment_edit_tool_seekbar);
        mSeekBar.setMax(20);
        mSeekBar.setProgress(mBrightness);
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, final int progress, boolean fromUser) {
                mTool.getToolFragment().refreshImage(new BrightnessFilterTransformation(mTool.getToolFragment().getActivity(), (progress - 10) / 10.0f));
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

    @Override
    public void onUnselected() {
        mTool.getToolFragment().showMenu();
        mTool.getToolFragment().showEditOptionsMenu(false, false, false, true);
        mTool.getToolFragment().hideToolContainer();
    }

    @Override
    public String getName() {
        return StringUtil.getString(R.string.tool_option_brightness_name);
    }

    @Override
    public int getImageResource() {
        return R.drawable.ic_brightness;
    }

    @Override
    public void onOptionsClearSelected() {
        mSeekBar.setProgress(INITIAL_BRIGHTNESS);
    }

    @Override
    public void onOptionsUndoSelected() {

    }

    @Override
    public void onOptionsDoneSelected() {

    }

    @Override
    public void onOptionsHomeSelected() {

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
