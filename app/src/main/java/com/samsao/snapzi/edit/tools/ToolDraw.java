package com.samsao.snapzi.edit.tools;

import android.os.Parcel;
import android.os.Parcelable;
import android.view.MotionEvent;
import android.view.View;

import com.hannesdorfmann.parcelableplease.annotation.ParcelableNoThanks;
import com.hannesdorfmann.parcelableplease.annotation.ParcelablePlease;
import com.hannesdorfmann.parcelableplease.annotation.ParcelableThisPlease;
import com.samsao.snapzi.R;
import com.samsao.snapzi.edit.EditFragment;
import com.samsao.snapzi.util.StringUtil;

import me.panavtec.drawableview.DrawableViewConfig;


/**
 * @author jfcartier
 * @since 15-04-06
 */
@ParcelablePlease(allFields = false)
public class ToolDraw extends Tool implements Parcelable, ToolOptionColorPicker.ToolCallback {

    @ParcelableNoThanks
    private final int DEFAULT_OPTION_INDEX = 2;
    @ParcelableThisPlease
    public DrawableViewConfig mDrawableViewConfig;

    public ToolDraw() {
        super();
        mDrawableViewConfig = new DrawableViewConfig();
        mDrawableViewConfig.setStrokeColor(ToolOptionStrokeColor.DEFAULT_COLOR);
        mDrawableViewConfig.setStrokeWidth(20.0f);
        mDrawableViewConfig.setMinZoom(1.0f);
        mDrawableViewConfig.setMaxZoom(3.0f);
        // FIXME
        mDrawableViewConfig.setCanvasHeight(0);
        mDrawableViewConfig.setCanvasWidth(0);
        addOption(new ToolOptionStrokeColor().setTool(this));
        addOption(new ToolOptionStrokeWidth().setStrokeWidth(ToolOptionStrokeWidth.STROKE_WIDTH_SMALL).setTool(this));
        addOption(new ToolOptionStrokeWidth().setStrokeWidth(ToolOptionStrokeWidth.STROKE_WIDTH_MEDIUM).setTool(this));
        addOption(new ToolOptionStrokeWidth().setStrokeWidth(ToolOptionStrokeWidth.STROKE_WIDTH_LARGE).setTool(this));
    }

    @Override
    public String getName() {
        return StringUtil.getString(R.string.tool_draw_name);
    }

    @Override
    public int getImageResource() {
        return 0;
    }

    @Override
    public void onSelected() {
        if (mCurrentOption == null) {
            selectOption(mOptions.get(DEFAULT_OPTION_INDEX));
        }
        getToolFragment().showEditOptionsMenu(true, true, true);
        getToolFragment().getDrawAnnotationContainer().setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getActionMasked()) {
                    case MotionEvent.ACTION_DOWN:
                        getToolFragment().hideOverlays();
                        break;
                    case MotionEvent.ACTION_UP:
                        getToolFragment().showOverlays();
                        break;
                }
                return getToolFragment().getDrawAnnotationContainer().onTouch(v, event);
            }
        });
        getToolFragment().disableTextAnnotationContainerTouchEvent();
    }

    @Override
    public void onUnselected() {
        getToolFragment().getDrawAnnotationContainer().setOnTouchListener(null);
        getToolFragment().enableTextAnnotationContainerTouchEvent();
    }

    @Override
    public Tool setToolFragment(EditFragment toolFragment) {
        super.setToolFragment(toolFragment);
        toolFragment.getDrawAnnotationContainer().setConfig(mDrawableViewConfig);
        return this;
    }

    public ToolDraw setStrokeColor(int strokeColor) {
        mDrawableViewConfig.setStrokeColor(strokeColor);
        return this;
    }

    public ToolDraw setStrokeWidth(float strokeWidth) {
        mDrawableViewConfig.setStrokeWidth(strokeWidth);
        return this;
    }

    public ToolDraw setCanvasHeight(int canvasHeight) {
        mDrawableViewConfig.setCanvasHeight(canvasHeight);
        return this;
    }

    public ToolDraw setCanvasWidth(int canvasWidth) {
        mDrawableViewConfig.setCanvasWidth(canvasWidth);
        return this;
    }

    @Override
    public void onOptionsClearSelected() {
        getToolFragment().getDrawAnnotationContainer().clear();
    }

    @Override
    public void onOptionsUndoSelected() {
        getToolFragment().getDrawAnnotationContainer().undo();
    }

    @Override
    public void onOptionsDoneSelected() {
        getToolFragment().resetCurrentTool();
        getToolFragment().resetOptionsMenu();
    }

    @Override
    public void onOptionsHomeSelected() {
        onOptionsDoneSelected();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        ToolDrawParcelablePlease.writeToParcel(this, dest, flags);
    }

    public static final Creator<ToolDraw> CREATOR = new Creator<ToolDraw>() {
        public ToolDraw createFromParcel(Parcel source) {
            ToolDraw target = new ToolDraw();
            ToolDrawParcelablePlease.readFromParcel(target, source);
            return target;
        }

        public ToolDraw[] newArray(int size) {
            return new ToolDraw[size];
        }
    };

    @Override
    public void onColorSelected(int color) {
        setStrokeColor(color);
    }
}
