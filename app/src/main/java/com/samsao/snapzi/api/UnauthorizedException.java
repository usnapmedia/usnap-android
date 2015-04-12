package com.samsao.snapzi.api;

/**
 * @author jfcartier
 * @since 15-04-11
 */
public class UnauthorizedException extends Throwable {
    public UnauthorizedException(String detailMessage) {
        super(detailMessage);
    }
}
