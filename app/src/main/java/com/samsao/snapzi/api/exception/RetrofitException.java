package com.samsao.snapzi.api.exception;

import android.text.TextUtils;

import com.samsao.snapzi.R;
import com.samsao.snapzi.SnapziApplication;

/**
 * @author jfcartier
 * @since 15-04-13
 */
public abstract class RetrofitException extends Throwable {
    private String mMessage;

    protected RetrofitException() {
        super();
        mMessage = SnapziApplication.getContext().getString(R.string.error_unknown);
    }

    public RetrofitException(String message) {
        this();
        setMessage(message);
    }

    @Override
    public String getMessage() {
        return mMessage;
    }

    public void setMessage(String message) {
        if (!TextUtils.isEmpty(message)) {
            mMessage = message;
        }
    }
}
