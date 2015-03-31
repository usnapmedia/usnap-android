/*
 * Copyright (c) 2014 Samsao Development Inc.
 */

package com.samsao.snapzi.api;

import com.samsao.snapzi.api.entity.Response;

import retrofit.Callback;
import retrofit.http.Field;
import retrofit.http.POST;

/**
 * @author jfcartier
 * @since 2014-05-29
 */
public interface SnapziApi {
    // TODO add fields
    @POST("/register")
    public void register(@Field("email") String email,
                      @Field("register") String password,
                      Callback<Response> callback);

    @POST("/login")
    public void login(@Field("email") String email,
                         @Field("register") String password,
                         Callback<Response> callback);

}