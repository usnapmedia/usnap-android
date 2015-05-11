/*
 * Copyright (c) 2014 Samsao Development Inc.
 */

package com.samsao.snapzi.api;

import com.samsao.snapzi.api.entity.CampaignList;
import com.samsao.snapzi.api.entity.FeedImageList;
import com.samsao.snapzi.api.entity.Response;
import com.samsao.snapzi.api.entity.TopCampaignList;
import com.samsao.snapzi.api.entity.UserList;

import retrofit.Callback;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.http.Path;
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
    void register(@Field("username") String username,
                  @Field("password") String password,
                  @Field("email") String email,
                  @Field("first_name") String firstName,
                  @Field("last_name") String lastName,
                  @Field("dob") String birthday,
                  Callback<Response> callback);


    @FormUrlEncoded
    @POST("/login")
    void login(@Field("username") String username,
               @Field("password") String password,
               Callback<Response> callback);

    @GET("/feed/live/{campaign_id}")
    void getLiveFeed(@Path("campaign_id") Integer campaignId, Callback<FeedImageList> callback);

    @GET("/feed/live/me")
    void getMyLiveFeed(Callback<FeedImageList> callback);

    @GET("/campaigns")
    void getCampaigns(Callback<CampaignList> callback);

    @GET("/feed/top/{campaign_id}")
    void getTopCampaign(@Path("campaign_id") Integer campaignId, Callback<TopCampaignList> callback);

    @GET("/users/me")
    void getUserInfo(Callback<UserList> callback);

    @Multipart
    @POST("/share")
    void share(@Part("image_data") TypedFile image,
               @Part("text") TypedString text,
               @Part("fb") TypedString fbToken,
               @Part("tw_key") TypedString twitterToken,
               @Part("tw_secret") TypedString twitterSecret,
               @Part("gp") TypedString googlePlusToken,
               @Part("campaign_id") Integer campaignId,
               Callback<Response> callback);

}