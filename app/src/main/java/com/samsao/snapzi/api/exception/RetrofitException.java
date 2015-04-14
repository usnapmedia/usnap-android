package com.samsao.snapzi.api.exception;

import retrofit.RetrofitError;

/**
 * @author jfcartier
 * @since 15-04-13
 */
public abstract class RetrofitException extends Throwable {
    public RetrofitError mError;

    public RetrofitException(RetrofitError error) {
        super();
        mError = error;
    }

    public RetrofitError getError() {
        return mError;
    }
}
