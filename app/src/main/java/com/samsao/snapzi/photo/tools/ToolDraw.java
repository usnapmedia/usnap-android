package com.samsao.snapzi.photo.tools;

import android.os.Parcel;
import android.os.Parcelable;

import com.hannesdorfmann.parcelableplease.annotation.ParcelablePlease;
import com.hannesdorfmann.parcelableplease.annotation.ParcelableThisPlease;
import com.samsao.snapzi.R;
import com.samsao.snapzi.SnapziApplication;
import com.samsao.snapzi.photo.MenuItem;
import com.samsao.snapzi.photo.PhotoEditFragment;
import com.samsao.snapzi.util.StringUtil;

import me.panavtec.drawableview.DrawableViewConfig;

/**
 * @author jfcartier
 * @since 15-04-06
 */
@ParcelablePlease(allFields = false)
public class ToolDraw extends Tool implements Parcelable {

    @ParcelableThisPlease
    public DrawableViewConfig mDrawableViewConfig;

    public ToolDraw() {
        super();
        mDrawableViewConfig = new DrawableViewConfig();
        mDrawableViewConfig.setStrokeColor(SnapziApplication.getContext().getResources().getColor(android.R.color.holo_red_light));
        mDrawableViewConfig.setStrokeWidth(20.0f);
        mDrawableViewConfig.setMinZoom(1.0f);
        mDrawableViewConfig.setMaxZoom(3.0f);
        mDrawableViewConfig.setCanvasHeight(0);
        mDrawableViewConfig.setCanvasWidth(0);
        addOption(new ToolOptionStrokeWidth().setStrokeWidth(ToolOptionStrokeWidth.STROKE_WIDTH_SMALL).setTool(this));
        addOption(new ToolOptionStrokeWidth().setStrokeWidth(ToolOptionStrokeWidth.STROKE_WIDTH_MEDIUM).setTool(this));
        addOption(new ToolOptionStrokeWidth().setStrokeWidth(ToolOptionStrokeWidth.STROKE_WIDTH_LARGE).setTool(this));
    }

    @Override
    public MenuItem getMenuItem() {
        return new MenuItem() {
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
                select();
            }
        };
    }

    @Override
    public void select() {
        super.select();
        mToolFragment.getDrawAnnotationContainer().setOnTouchListener(mToolFragment.getDrawAnnotationContainer());
        mToolFragment.disableTextAnnotationContainerTouchEvent();
    }

    @Override
    public void unselect() {
        mToolFragment.getDrawAnnotationContainer().setOnTouchListener(null);
        mToolFragment.enableTextAnnotationContainerTouchEvent();
    }

    @Override
    public boolean getClearEnabled() {
        return true;
    }

    @Override
    public boolean getUndoEnabled() {
        return true;
    }

    @Override
    public Tool setToolFragment(PhotoEditFragment toolFragment) {
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
        mToolFragment.getDrawAnnotationContainer().clear();
    }

    @Override
    public void onOptionsUndoSelected() {
        mToolFragment.getDrawAnnotationContainer().undo();
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
}
