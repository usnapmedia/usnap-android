package com.samsao.snapzi.fan_page.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.samsao.snapzi.R;

/**
 * @author jingsilu
 * @since 2015-05-08
 */
public class AspectRatioImageView extends ImageView {
    private float mRatio;

    public AspectRatioImageView(Context context) {
        super(context);
    }

    public AspectRatioImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.AspectRatioImageView,
                0, 0);

        try {
            mRatio = a.getFloat(R.styleable.AspectRatioImageView_aspect_ratio, 0f);
        } finally {
            a.recycle();
        }
    }

    public AspectRatioImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int width = View.MeasureSpec.getSize(widthMeasureSpec);
        int height = (int)(width*mRatio);
        setMeasuredDimension(width, height);
    }
}
