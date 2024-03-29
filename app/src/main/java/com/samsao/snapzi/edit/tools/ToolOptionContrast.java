package com.samsao.snapzi.edit.tools;

import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;
import android.widget.SeekBar;

import com.hannesdorfmann.parcelableplease.annotation.ParcelablePlease;
import com.hannesdorfmann.parcelableplease.annotation.ParcelableThisPlease;
import com.samsao.snapzi.R;
import com.samsao.snapzi.util.StringUtil;

import jp.wasabeef.picasso.transformations.gpu.ContrastFilterTransformation;


/**
 * @author jfcartier
 * @since 15-04-07
 */
@ParcelablePlease
public class ToolOptionContrast extends ToolOption implements Parcelable {

    @ParcelableThisPlease
    public int mConstrast;

    public ToolOptionContrast() {
        super();
        // contrast varies from 0 to 4.0, but progress bar from 0 to MAX -> initial contrast is 10 (1.0) and max is 40
        mConstrast = 10;
    }

    @Override
    public void onSelected() {
        mTool.getToolFragment().hideMenu();
        mTool.getToolFragment().showEditOptionsMenu(true, false, false);
        View view = mTool.getToolFragment().showToolContainer(R.layout.fragment_edit_tool_seekbar);
        SeekBar seekBar = (SeekBar) view.findViewById(R.id.fragment_edit_tool_seekbar);
        seekBar.setMax(40);
        seekBar.setProgress(mConstrast);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, final int progress, boolean fromUser) {
                mTool.getToolFragment().refreshImage(new ContrastFilterTransformation(mTool.getToolFragment().getActivity(), progress / 10.0f));
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

    @Override
    public void onUnselected() {
        mTool.getToolFragment().showMenu();
        mTool.getToolFragment().showEditOptionsMenu(false, false, false);
        mTool.getToolFragment().hideToolContainer();
    }

    @Override
    public String getName() {
        return StringUtil.getString(R.string.tool_option_contrast_name);
    }

    @Override
    public int getImageResource() {
        return 0;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        ToolOptionContrastParcelablePlease.writeToParcel(this, dest, flags);
    }

    public static final Creator<ToolOptionContrast> CREATOR = new Creator<ToolOptionContrast>() {
        public ToolOptionContrast createFromParcel(Parcel source) {
            ToolOptionContrast target = new ToolOptionContrast();
            ToolOptionContrastParcelablePlease.readFromParcel(target, source);
            return target;
        }

        public ToolOptionContrast[] newArray(int size) {
            return new ToolOptionContrast[size];
        }
    };
}
