package com.samsao.snapzi.api.exception;

import retrofit.RetrofitError;

/**
 * @author jfcartier
 * @since 15-04-11
 */
public class HostUnreachableException extends RetrofitException {

    public HostUnreachableException(RetrofitError error) {
        super(error);
    }
}
