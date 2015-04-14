package com.samsao.snapzi.api.exception;

import retrofit.RetrofitError;

/**
 * @author jfcartier
 * @since 15-04-11
 */
public class NetworkTimeoutException extends RetrofitException {

    public NetworkTimeoutException(RetrofitError error) {
        super(error);
    }
}
