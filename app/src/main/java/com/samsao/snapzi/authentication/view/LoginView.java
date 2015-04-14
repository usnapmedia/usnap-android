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
import butterknife.OnClick;

/**
 * @author jfcartier
 * @since 15-04-11
 */
public class LoginView extends LinearLayout {

    @InjectView(R.id.view_login_signupToggle_btn)
    public Button mSignupToggleButton;

    @InjectView(R.id.view_login_loginToggle_btn)
    public Button mLoginToggleButton;

    @InjectView(R.id.view_login_content_login)
    public ContentLoginView mContentLoginView;

    @InjectView(R.id.view_login_content_signup)
    public ContentSignupView mContentSignupView;

    private LoginCallback mLoginCallback;
    private SignupCallback mSignupCallback;

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
        mContentLoginView.setCallback(new ContentLoginView.Callback() {
            @Override
            public void onLogin() {
                if (mLoginCallback != null) {
                    mLoginCallback.onLoginValidated();
                }
            }

            @Override
            public void onResetPasswordClick() {
                if (mLoginCallback != null) {
                    mLoginCallback.onResetPasswordClick();
                }
            }
        });

        mContentSignupView.setCallback(new ContentSignupView.Callback() {
            @Override
            public void onSignup() {
                if (mSignupCallback != null) {
                    mSignupCallback.onSignupValidated();
                }
            }

            @Override
            public void showBirthdayDatePicker() {
                if (mSignupCallback != null) {
                    mSignupCallback.showBirthdayDatePicker();
                }
            }
        });
        showLoginContent();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        ButterKnife.reset(this);
        mLoginCallback = null;
        mSignupCallback = null;
    }

    /**
     * Show login content
     */
    @OnClick(R.id.view_login_loginToggle_btn)
    public void showLoginContent() {
        mSignupToggleButton.setEnabled(true);
        mLoginToggleButton.setEnabled(false);
        mContentSignupView.setVisibility(GONE);
        mContentLoginView.setVisibility(VISIBLE);
    }

    /**
     * Show signup content
     */
    @OnClick(R.id.view_login_signupToggle_btn)
    public void showSignupContent() {
        mSignupToggleButton.setEnabled(false);
        mLoginToggleButton.setEnabled(true);
        mContentSignupView.setVisibility(VISIBLE);
        mContentLoginView.setVisibility(GONE);
    }

    public void setLoginCallback(LoginCallback loginCallback) {
        mLoginCallback = loginCallback;
    }

    public void setSignupCallback(SignupCallback signupCallback) {
        mSignupCallback = signupCallback;
    }

    public String getLoginUsername() {
        return mContentLoginView.getUsername();
    }

    public String getLoginPassword() {
        return mContentLoginView.getPassword();
    }

    public String getSignupFirstName() {
        return mContentSignupView.getFirstName();
    }

    public String getSignupLastName() {
        return mContentSignupView.getLastName();
    }

    public String getSignupEmail() {
        return mContentSignupView.getEmail();
    }

    public String getSignupUsername() {
        return mContentSignupView.getUsername();
    }

    public String getSignupPassword() {
        return mContentSignupView.getPassword();
    }

    public String getSignupBirthday() {
        return mContentSignupView.getBirthday();
    }

    /**
     * Set signup birthday text
     * @param birthday
     */
    public void setSignupBirthdayText(String birthday) {
        mContentSignupView.setBirthday(birthday);
    }

    /**
     * Login callback
     */
    public interface LoginCallback {
        void onLoginValidated();
        void onResetPasswordClick();
    }

    /**
     * Signup callback
     */
    public interface SignupCallback {
        void onSignupValidated();
        void showBirthdayDatePicker();
    }
}
