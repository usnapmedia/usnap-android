package com.samsao.snapzi.authentication.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.LinearLayout;

import com.samsao.snapzi.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * @author jfcartier
 * @since 15-04-11
 */
public class LoginView extends LinearLayout {
    @InjectView(R.id.view_login_signupToggle_btn)
    public Button mSignupToggleButton;

    @InjectView(R.id.view_login_loginToggle_btn)
    public Button mLoginToggleButton;

    @InjectView(R.id.view_login_btn)
    public Button mButton;

    @InjectView(R.id.view_login_content_login)
    public ContentLoginView mContentLoginView;

    public LoginView(Context context) {
        super(context);
        initialize();
    }

    public LoginView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public LoginView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize();
    }

    @TargetApi(21)
    public LoginView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initialize();
    }

    public void initialize() {
        LayoutInflater.from(getContext()).inflate(R.layout.view_login, this, true);
        setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        setOrientation(VERTICAL);
        ButterKnife.inject(this, this);
        mButton.setText(getResources().getString(R.string.action_login));
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        ButterKnife.reset(this);
    }
}
