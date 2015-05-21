package com.samsao.snapzi.camera.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/**
 * @author jingsilu
 * @since 2015-05-21
 */
public class SquareCameraFrameLayout extends FrameLayout{
    public SquareCameraFrameLayout(Context context) {
        super(context);
    }

    public SquareCameraFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SquareCameraFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int squareSize = width < height ? width : height;
        setMeasuredDimension(squareSize, squareSize);
    }
}
