package com.samsao.snapzi.api.exception;

import retrofit.RetrofitError;

/**
 * @author jfcartier
 * @since 15-04-11
 */
public class UnauthorizedException extends Throwable {
    public RetrofitError mError;

    public UnauthorizedException() {
        super();
    }

    public UnauthorizedException(RetrofitError error) {
        this();
        mError = error;
    }

    public RetrofitError getError() {
        return mError;
    }
}
