package com.samsao.snapzi.authentication.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.mobsandgeeks.saripaar.Rule;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Required;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.samsao.snapzi.R;

import java.lang.ref.WeakReference;

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
    private WeakReference<Callback> mCallback;

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
        ButterKnife.inject(this, this);
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
        if (mCallback != null && mCallback.get() != null) {
            mCallback.get().onLogin();
        }
    }

    @Override
    public void onValidationFailed(View failedView, Rule<?> failedRule) {
        String message = failedRule.getFailureMessage();
        failedView.requestFocus();
        ((EditText) failedView).setError(message);
    }

    public void setCallback(Callback callback) {
        mCallback = new WeakReference<>(callback);
    }

    @OnClick(R.id.view_login_content_login_btn)
    public void login() {
        mValidator.validate();
    }

    public interface Callback {
        void onLogin();
    }
}
