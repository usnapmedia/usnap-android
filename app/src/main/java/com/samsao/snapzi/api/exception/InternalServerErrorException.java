package com.samsao.snapzi.api.exception;

import retrofit.RetrofitError;

/**
 * @author jfcartier
 * @since 15-04-11
 */
public class InternalServerErrorException extends RetrofitException {

    public InternalServerErrorException(RetrofitError error) {
        super(error);
    }
}
