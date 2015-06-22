/*
 * Copyright (c) 2014 Samsao Development Inc.
 */

package com.samsao.snapzi.api;

import com.samsao.snapzi.api.entity.CampaignList;
import com.samsao.snapzi.api.entity.Response;
import com.samsao.snapzi.api.entity.SnapList;
import com.samsao.snapzi.api.entity.UserList;

import retrofit.Callback;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.http.Path;
import retrofit.http.Query;
import retrofit.mime.TypedFile;

/**
 * @author jfcartier
 * @since 2014-05-29
 */
public interface SnapziApi {
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
    void getLiveFeed(@Path("campaign_id") Integer campaignId,
                     @Query("type") String filter,
                     Callback<SnapList> callback);

    @GET("/feed/live/me")
    void getMyLiveFeed(Callback<SnapList> callback);

    @GET("/campaigns")
    void getCampaigns(Callback<CampaignList> callback);

    @GET("/feed/top/{campaign_id}")
    void getTopSnaps(@Path("campaign_id") Integer campaignId,
                     @Query("type") String filter,
                     Callback<SnapList> callback);

    @GET("/users/me")
    void getUserInfo(Callback<UserList> callback);

    @Multipart
    @POST("/share")
    void sharePicture(@Part("image_data") TypedFile image,
                      @Part("text") String text,
                      @Part("fb") String fbToken,
                      @Part("tw_key") String twitterToken,
                      @Part("tw_secret") String twitterSecret,
                      @Part("gp") String googlePlusToken,
                      @Part("campaign_id") Integer campaignId,
                      Callback<Response> callback);

    @Multipart
    @POST("/share/video")
    void shareVideo(@Part("image_data") TypedFile image,
                    @Part("video_data") TypedFile video,
                    @Part("text") String text,
                    @Part("fb") String fbToken,
                    @Part("tw_key") String twitterToken,
                    @Part("tw_secret") String twitterSecret,
                    @Part("gp") String googlePlusToken,
                    @Part("campaign_id") Integer campaignId,
                    Callback<Response> callback);

    @FormUrlEncoded
    @POST("/feed/report")
    void reportImage(@Field("image_id") Integer imageId,
                     @Field("username") String username,
                     Callback<Response> callback);
}