/*
 * Copyright (c) 2014 Samsao Development Inc.
 */

package com.samsao.snapzi.api;

import com.samsao.snapzi.api.entity.CampaignList;
import com.samsao.snapzi.api.entity.FeedImageList;
import com.samsao.snapzi.api.entity.Response;

import retrofit.Callback;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;

/**
 * @author jfcartier
 * @since 2014-05-29
 */
public interface SnapziApi {
    // TODO fix fields
    @FormUrlEncoded
    @POST("/register")
    public void register(@Field("username") String username,
                         @Field("password") String password,
                         @Field("email") String email,
                         @Field("first_name") String firstName,
                         @Field("last_name") String lastName,
                         @Field("birthday") String birthday,
                         Callback<Response> callback);


    // TODO rename email to username
    @FormUrlEncoded
    @POST("/login")
    public void login(@Field("email") String username,
                      @Field("password") String password,
                      Callback<Response> callback);

    @GET("/feed/live")
    public void getLiveFeed(Callback<FeedImageList> callback);

    @GET("/campaigns")
    public void getCampaigns(Callback<CampaignList> callback);

}