package com.samsao.snapzi.api.exception;

import retrofit.RetrofitError;

/**
 * @author jfcartier
 * @since 15-04-11
 */
public class MalformedUrlException extends RetrofitException {

    public MalformedUrlException(RetrofitError error) {
        super(error);
    }
}
