package com.samsao.snapzi.util;

/**
 * @author jfcartier
 * @since 15-03-23
 */
public interface SaveImageCallback {
    public void onSuccess(String destFilePath);
    public void onFailure();
}
