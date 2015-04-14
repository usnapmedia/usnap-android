package com.samsao.snapzi.authentication.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mobsandgeeks.saripaar.Rule;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Required;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.samsao.snapzi.R;
import com.samsao.snapzi.util.KeyboardUtil;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * @author jfcartier
 * @since 15-04-11
 */
public class ContentLoginView extends LinearLayout implements com.mobsandgeeks.saripaar.Validator.ValidationListener {

    @Required(order = 1, messageResId = R.string.error_empty_username)
    @InjectView(R.id.view_login_content_login_username_editText)
    public MaterialEditText mUsernameEditText;

    @Required(order = 2, messageResId = R.string.error_empty_password)
    @InjectView(R.id.view_login_content_login_password_editText)
    public MaterialEditText mPasswordEditText;

    @InjectView(R.id.view_login_content_login_resetPassword_btn)
    public Button mResetPasswordButton;

    private Validator mValidator;
    private Callback mCallback;

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

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ContentLoginView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initialize();
    }

    public void initialize() {
        LayoutInflater.from(getContext()).inflate(R.layout.view_login_content_login, this, true);
        setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        setOrientation(VERTICAL);
        ButterKnife.inject(this, this);
        mPasswordEditText.setOnEditorActionListener(
                new EditText.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if (actionId == EditorInfo.IME_ACTION_DONE) {
                            KeyboardUtil.hideKeyboard(mPasswordEditText);
                            login();
                            return true;
                        }
                        return false;
                    }
                });
        mValidator = new Validator(this);
        mValidator.setValidationListener(this);
        mCallback = null;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        ButterKnife.reset(this);
        mValidator = null;
        mCallback = null;
    }

    @Override
    public void onValidationSucceeded() {
        if (mCallback != null) {
            mCallback.onLogin();
        }
    }

    @Override
    public void onValidationFailed(View failedView, Rule<?> failedRule) {
        String message = failedRule.getFailureMessage();
        failedView.requestFocus();
        ((EditText) failedView).setError(message);
    }

    public void setCallback(Callback callback) {
        mCallback = callback;
    }

    public String getUsername() {
        return mUsernameEditText.getText().toString();
    }

    public String getPassword() {
        return mPasswordEditText.getText().toString();
    }

    @OnClick(R.id.view_login_content_login_btn)
    public void login() {
        mValidator.validate();
    }

    @OnClick(R.id.view_login_content_login_resetPassword_btn)
    public void onResetPasswordClick() {
        mCallback.onResetPasswordClick();
    }

    public interface Callback {
        void onLogin();
        void onResetPasswordClick();
    }
}
