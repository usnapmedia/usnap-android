/*
 * Copyright (c) 2014 Samsao Development Inc.
 */

package com.samsao.snapzi.api;

import com.samsao.snapzi.api.entity.CampaignList;
import com.samsao.snapzi.api.entity.FeedImageList;
import com.samsao.snapzi.api.entity.Response;
import com.samsao.snapzi.api.entity.TopCampaignList;

import retrofit.Callback;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.mime.TypedFile;
import retrofit.mime.TypedString;

/**
 * @author jfcartier
 * @since 2014-05-29
 */
public interface SnapziApi {
    // FIXME add api_key
    @FormUrlEncoded
    @POST("/register")
    public void register(@Field("username") String username,
                         @Field("password") String password,
                         @Field("email") String email,
                         @Field("first_name") String firstName,
                         @Field("last_name") String lastName,
                         @Field("dob") String birthday,
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
    @GET("/feed/top")
    public void getTopCampaign(Callback<TopCampaignList> callback);

    @Multipart
    @POST("/share")
    public void share(@Part("image_data") TypedFile image,
                         @Part("meta") TypedString meta,
                         @Part("text") TypedString text,
                         @Part("fb") TypedString fbToken,
                         @Part("tw_key") TypedString twitterToken,
                         @Part("tw_secret") TypedString twitterSecret,
                         @Part("gp") TypedString googlePlusToken,
                         Callback<Response> callback);

}