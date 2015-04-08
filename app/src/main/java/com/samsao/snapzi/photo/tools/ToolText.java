package com.samsao.snapzi.photo.tools;

import android.graphics.Typeface;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.hannesdorfmann.parcelableplease.annotation.ParcelablePlease;
import com.samsao.snapzi.R;
import com.samsao.snapzi.photo.MenuItem;
import com.samsao.snapzi.photo.PhotoEditFragment;
import com.samsao.snapzi.photo.util.TextAnnotationTouchListener;
import com.samsao.snapzi.util.KeyboardUtil;
import com.samsao.snapzi.util.StringUtil;

/**
 * @author jfcartier
 * @since 15-04-06
 */
@ParcelablePlease(allFields = false)
public class ToolText extends Tool implements Parcelable, ToolOptionColorPicker.ToolCallback, ToolOptionTextTypeFace.ToolCallback {

    public ToolText() {
        super();
        addOption(new ToolOptionTextTypeFace().setTypeFaceName("futura.ttc").setTool(this));
        addOption(new ToolOptionTextTypeFace().setTypeFaceName("georgia.ttf").setTool(this));
        addOption(new ToolOptionTextTypeFace().setTypeFaceName("impact.ttf").setTool(this));
        addOption(new ToolOptionTextColor().setTool(this));
    }

    @Override
    public MenuItem getMenuItem() {
        return new MenuItem() {
            @Override
            public String getName() {
                return StringUtil.getString(R.string.tool_text_name);
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
    public Tool setToolFragment(PhotoEditFragment toolFragment) {
        super.setToolFragment(toolFragment);
        mToolFragment.getTextAnnotation().setTextColor(ToolOptionTextColor.DEFAULT_COLOR);
        mToolFragment.getTextAnnotation().setOnEditorActionListener(
                new EditText.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if (actionId == EditorInfo.IME_ACTION_DONE) {
                            if (!TextUtils.isEmpty(mToolFragment.getTextAnnotation().getText())) {
                                lockText();
                                return false;
                            } else {
                                return true;
                            }
                        }
                        return false;
                    }
                });
        return this;
    }

    @Override
    public void onOptionsClearSelected() {
        mToolFragment.getTextAnnotation().setTranslationX(0);
        mToolFragment.getTextAnnotation().setTranslationY(0);
        mToolFragment.getTextAnnotation().setText("");
        unlockText();
    }

    @Override
    public void onOptionsUndoSelected() {

    }

    @Override
    public void select() {
        super.select();
        mToolFragment.enableTextAnnotationContainerTouchEvent();
        // lock the text if the user presses anywhere on the screen
        mToolFragment.getTextAnnotationContainer().setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (!TextUtils.isEmpty(mToolFragment.getTextAnnotation().getText())) {
                    KeyboardUtil.hideKeyboard(mToolFragment.getActivity());
                    lockText();
                }
                return false;
            }
        });

        if (!TextUtils.isEmpty(mToolFragment.getTextAnnotation().getText())) {
            lockText();
        } else {
            mToolFragment.getTextAnnotation().setVisibility(View.VISIBLE);
            unlockText();
        }
    }

    @Override
    public void unselect() {
        KeyboardUtil.hideKeyboard(mToolFragment.getActivity()); // in case the keyboard is still shown
        mToolFragment.disableTextAnnotationContainerTouchEvent();
        if (!TextUtils.isEmpty(mToolFragment.getTextAnnotation().getText())) {
            // in case the done button is clicked before the keyboard was dismissed
            mToolFragment.getTextAnnotation().setFocusableInTouchMode(false);
            mToolFragment.getTextAnnotation().clearFocus();
            // remove the touch listener so the text cant be dragged
            mToolFragment.getTextAnnotation().setOnTouchListener(null);
        } else {
            mToolFragment.getTextAnnotation().setVisibility(View.GONE);
        }
    }

    /**
     * Lock the text so it can only be moved
     */
    protected void lockText() {
        mToolFragment.getTextAnnotation().setFocusableInTouchMode(false);
        mToolFragment.getTextAnnotation().clearFocus();
        mToolFragment.getTextAnnotation().setOnTouchListener(new TextAnnotationTouchListener(mToolFragment.getTextAnnotation()));
    }

    /**
     * Unlock text and request user input
     */
    protected void unlockText() {
        mToolFragment.getTextAnnotation().setFocusableInTouchMode(true);
        mToolFragment.getTextAnnotation().requestFocus();
        KeyboardUtil.showKeyboard(mToolFragment.getActivity(), mToolFragment.getTextAnnotation());
    }

    @Override
    public boolean getClearEnabled() {
        return true;
    }

    @Override
    public boolean getUndoEnabled() {
        return false;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        ToolTextParcelablePlease.writeToParcel(this, dest, flags);
    }

    public static final Creator<ToolText> CREATOR = new Creator<ToolText>() {
        public ToolText createFromParcel(Parcel source) {
            ToolText target = new ToolText();
            ToolTextParcelablePlease.readFromParcel(target, source);
            return target;
        }

        public ToolText[] newArray(int size) {
            return new ToolText[size];
        }
    };

    @Override
    public void onColorSelected(int color) {
        mToolFragment.getTextAnnotation().setTextColor(color);
    }

    @Override
    public void setTypeFace(Typeface font) {
        mToolFragment.getTextAnnotation().setTypeface(font);
    }
}
