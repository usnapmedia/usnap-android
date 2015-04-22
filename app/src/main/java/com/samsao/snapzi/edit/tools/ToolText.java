package com.samsao.snapzi.edit.tools;

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

import com.hannesdorfmann.parcelableplease.annotation.ParcelableNoThanks;
import com.hannesdorfmann.parcelableplease.annotation.ParcelablePlease;
import com.hannesdorfmann.parcelableplease.annotation.ParcelableThisPlease;
import com.samsao.snapzi.R;
import com.samsao.snapzi.edit.EditFragment;
import com.samsao.snapzi.edit.util.TextAnnotationTouchListener;
import com.samsao.snapzi.util.KeyboardUtil;
import com.samsao.snapzi.util.StringUtil;


/**
 * @author jfcartier
 * @since 15-04-06
 */
@ParcelablePlease(allFields = false)
public class ToolText extends Tool implements Parcelable, ToolOptionColorPicker.ToolCallback,
        ToolOptionTextTypeFace.ToolCallback,
        TextAnnotationTouchListener.Callback {

    @ParcelableNoThanks
    private final int DEFAULT_OPTION_INDEX = 1;

    @ParcelableThisPlease
    public String mText;

    public ToolText() {
        super();
        addOption(new ToolOptionTextColor().setTool(this));
        addOption(new ToolOptionTextTypeFace().setTypeFaceName(ToolOptionTextTypeFace.DEFAULT_TYPEFACE_NAME).setTool(this));
        addOption(new ToolOptionTextTypeFace().setTypeFaceName(ToolOptionTextTypeFace.FUTURA_TYPEFACE_NAME).setTool(this));
        addOption(new ToolOptionTextTypeFace().setTypeFaceName(ToolOptionTextTypeFace.GEORGIA_TYPEFACE_NAME).setTool(this));
        addOption(new ToolOptionTextTypeFace().setTypeFaceName(ToolOptionTextTypeFace.IMPACT_TYPEFACE_NAME).setTool(this));
    }

    @Override
    public String getName() {
        return StringUtil.getString(R.string.tool_text_name);
    }

    @Override
    public int getImageResource() {
        return 0;
    }

    @Override
    public Tool setToolFragment(EditFragment toolFragment) {
        super.setToolFragment(toolFragment);
        getToolFragment().getTextAnnotation().setTextColor(ToolOptionTextColor.DEFAULT_COLOR);
        getToolFragment().getTextAnnotation().setOnEditorActionListener(
                new EditText.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if (actionId == EditorInfo.IME_ACTION_DONE) {
                            if (!TextUtils.isEmpty(getToolFragment().getTextAnnotation().getText())) {
                                mText = getToolFragment().getTextAnnotation().getText().toString();
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
        mText = null;
        getToolFragment().getTextAnnotation().setTranslationX(0);
        getToolFragment().getTextAnnotation().setTranslationY(0);
        getToolFragment().getTextAnnotation().setText("");
        unlockText();
    }

    @Override
    public void onOptionsUndoSelected() {

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
    public void onSelected() {
        if (mCurrentOption == null) {
            selectOption(mOptions.get(DEFAULT_OPTION_INDEX));
        }
        getToolFragment().showEditOptionsMenu(true, true, false);
        getToolFragment().enableTextAnnotationContainerTouchEvent();
        // lock the text if the user presses anywhere on the screen
        getToolFragment().getTextAnnotationContainer().setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (!TextUtils.isEmpty(getToolFragment().getTextAnnotation().getText())) {
                    KeyboardUtil.hideKeyboard(getToolFragment().getActivity());
                    mText = getToolFragment().getTextAnnotation().getText().toString();
                    lockText();
                }
                return false;
            }
        });
        unlockText();
    }

    @Override
    public void onUnselected() {
        mText = getToolFragment().getTextAnnotation().getText().toString();
        KeyboardUtil.hideKeyboard(getToolFragment().getActivity()); // in case the keyboard is still shown
        getToolFragment().disableTextAnnotationContainerTouchEvent();
        if (!TextUtils.isEmpty(getToolFragment().getTextAnnotation().getText())) {
            // in case the done button is clicked before the keyboard was dismissed
            getToolFragment().getTextAnnotation().setFocusableInTouchMode(false);
            getToolFragment().getTextAnnotation().clearFocus();
            // remove the touch listener so the text cant be dragged
            getToolFragment().getTextAnnotation().setOnTouchListener(null);
        }
    }

    /**
     * Lock the text so it can only be moved
     */
    protected void lockText() {
        getToolFragment().getTextAnnotation().setFocusableInTouchMode(false);
        getToolFragment().getTextAnnotation().clearFocus();
        getToolFragment().getTextAnnotation().setOnTouchListener(new TextAnnotationTouchListener(getToolFragment().getTextAnnotation(), this));
    }

    /**
     * Unlock text and request user input
     */
    protected void unlockText() {
        getToolFragment().getTextAnnotation().setFocusableInTouchMode(true);
        getToolFragment().getTextAnnotation().requestFocus();
        KeyboardUtil.showKeyboard(getToolFragment().getActivity(), getToolFragment().getTextAnnotation());
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
        getToolFragment().getTextAnnotation().setTextColor(color);
    }

    @Override
    public void setTypeFace(Typeface font) {
        getToolFragment().getTextAnnotation().setTypeface(font);
        // if the editText has focus, show the keyboard
        if (getToolFragment().getTextAnnotation().hasFocus()) {
            KeyboardUtil.showKeyboard(getToolFragment().getActivity(), getToolFragment().getTextAnnotation());
        }
    }

    @Override
    public void hideOverlays() {
        getToolFragment().hideOverlays();
    }

    @Override
    public void showOverlays() {
        getToolFragment().showOverlays();
    }
}
