package com.samsao.snapzi.authentication.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.LinearLayout;

import com.rengwuxian.materialedittext.MaterialEditText;
import com.samsao.snapzi.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * @author jfcartier
 * @since 15-04-11
 */
public class ContentLoginView extends LinearLayout {
    @InjectView(R.id.view_login_content_login_username_editText)
    public MaterialEditText mUsernameEditText;

    @InjectView(R.id.view_login_content_login_password_editText)
    public MaterialEditText mPasswordEditText;

    @InjectView(R.id.view_login_content_login_resetPassword_btn)
    public Button mResetPasswordButton;

    public ContentLoginView(Context context) {
        super(context);
        initialize();
    }

    public ContentLoginView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public ContentLoginView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize();
    }

    @TargetApi(21)
    public ContentLoginView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initialize();
    }

    public void initialize() {
        LayoutInflater.from(getContext()).inflate(R.layout.view_login_content_login, this, true);
        setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        setOrientation(VERTICAL);
        setPadding((int) getResources().getDimension(R.dimen.elements_horizontal_margin),
                (int) getResources().getDimension(R.dimen.elements_double_vertical_margin),
                (int) getResources().getDimension(R.dimen.elements_horizontal_margin),
                (int) getResources().getDimension(R.dimen.elements_double_vertical_margin));
        setBackgroundColor(getResources().getColor(R.color.light_gray));
        ButterKnife.inject(this, this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        ButterKnife.reset(this);
    }
}
