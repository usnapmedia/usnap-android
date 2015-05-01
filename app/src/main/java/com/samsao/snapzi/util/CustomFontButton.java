package com.samsao.snapzi.util;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;

import com.samsao.snapzi.R;

import java.util.HashMap;
import java.util.Map;

/**
 * @author jingsilu
 * @since 2015-05-01
 */
public class CustomFontButton extends Button {
    private static Map<String, Typeface> mTypefaces;

    public CustomFontButton(final Context context) {
        this(context, null);
    }

    public CustomFontButton(final Context context, final AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomFontButton(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);
        if (mTypefaces == null) {
            mTypefaces = new HashMap<>();
        }

        final TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.CustomFontButton);
        if (array != null) {
            final String typefaceAssetPath = array.getString(
                    R.styleable.CustomFontButton_Button_customTypeFace);

            if (typefaceAssetPath != null) {
                Typeface typeface;

                if (mTypefaces.containsKey(typefaceAssetPath)) {
                    typeface = mTypefaces.get(typefaceAssetPath);
                } else {
                    AssetManager assets = context.getAssets();
                    typeface = Typeface.createFromAsset(assets, typefaceAssetPath);
                    mTypefaces.put(typefaceAssetPath, typeface);
                }
                setTypeface(typeface);
            }
            array.recycle();
        }
    }
}
