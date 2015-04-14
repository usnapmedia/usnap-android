package com.samsao.snapzi.authentication.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mobsandgeeks.saripaar.Rule;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Email;
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
public class ContentSignupView extends LinearLayout implements Validator.ValidationListener {

    @Required(order = 1, messageResId = R.string.error_empty_first_name)
    @InjectView(R.id.view_login_content_signup_firstName_editText)
    public MaterialEditText mFirstNameEditText;

    @Required(order = 2, messageResId = R.string.error_empty_last_name)
    @InjectView(R.id.view_login_content_signup_lastName_editText)
    public MaterialEditText mLastNameEditText;

    @Required(order = 3, messageResId = R.string.error_empty_email)
    @Email(order = 4, messageResId = R.string.error_invalid_email)
    @InjectView(R.id.view_login_content_signup_email_editText)
    public MaterialEditText mEmailEditText;

    @Required(order = 5, messageResId = R.string.error_empty_username)
    @InjectView(R.id.view_login_content_signup_username_editText)
    public MaterialEditText mUsernameEditText;

    @Required(order = 6, messageResId = R.string.error_empty_password)
    @InjectView(R.id.view_login_content_signup_password_editText)
    public MaterialEditText mPasswordEditText;

    @Required(order = 7, messageResId = R.string.error_empty_birthday)
    @InjectView(R.id.view_login_content_signup_birthday_editText)
    public MaterialEditText mBirthdayEditText;

    private Validator mValidator;
    private Callback mCallback;

    public ContentSignupView(Context context) {
        super(context);
        initialize();
    }

    public ContentSignupView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public ContentSignupView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ContentSignupView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initialize();
    }

    public void initialize() {
        LayoutInflater.from(getContext()).inflate(R.layout.view_login_content_signup, this, true);
        setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        setOrientation(VERTICAL);
        ButterKnife.inject(this, this);
        mBirthdayEditText.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.showBirthdayDatePicker(mBirthdayEditText.getText().toString());
            }
        });
        mBirthdayEditText.setOnEditorActionListener(
                new EditText.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if (actionId == EditorInfo.IME_ACTION_DONE) {
                            KeyboardUtil.hideKeyboard(mBirthdayEditText);
                            signup();
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
            mCallback.onSignup();
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

    @OnClick(R.id.view_login_content_signup_btn)
    public void signup() {
        mValidator.validate();
    }

    public String getFirstName() {
        return mFirstNameEditText.getText().toString();
    }

    public String getLastName() {
        return mLastNameEditText.getText().toString();
    }

    public String getEmail() {
        return mEmailEditText.getText().toString();
    }

    public String getUsername() {
        return mUsernameEditText.getText().toString();
    }

    public String getPassword() {
        return mPasswordEditText.getText().toString();
    }

    public String getBirthday() {
        return mBirthdayEditText.getText().toString();
    }

    public void setBirthday(String birthday) {
        mBirthdayEditText.setText(birthday);
    }

    @OnClick(R.id.view_login_content_signup_birthday_editText)
    public void showBirthdayDatePicker() {
        mCallback.showBirthdayDatePicker(getBirthday());
    }

    public interface Callback {
        void onSignup();
        void showBirthdayDatePicker(String date);
    }
}
