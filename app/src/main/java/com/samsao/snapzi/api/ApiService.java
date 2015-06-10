/*
 * Copyright (c) 2014 Samsao Development Inc.
 */

package com.samsao.snapzi.api;

import android.text.TextUtils;
import android.util.Base64;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.samsao.snapzi.BuildConfig;
import com.samsao.snapzi.R;
import com.samsao.snapzi.SnapziApplication;
import com.samsao.snapzi.api.entity.CampaignList;
import com.samsao.snapzi.api.entity.FeedImageList;
import com.samsao.snapzi.api.entity.Response;
import com.samsao.snapzi.api.entity.TopCampaignList;
import com.samsao.snapzi.api.entity.UserList;
import com.samsao.snapzi.api.exception.ApiException;
import com.samsao.snapzi.api.exception.HostUnreachableException;
import com.samsao.snapzi.api.exception.InternalServerErrorException;
import com.samsao.snapzi.api.exception.MalformedUrlException;
import com.samsao.snapzi.api.exception.NetworkTimeoutException;
import com.samsao.snapzi.api.exception.RetrofitException;
import com.samsao.snapzi.api.exception.UnauthorizedException;
import com.samsao.snapzi.util.PreferenceManager;
import com.samsao.snapzi.util.UserManager;
import com.squareup.okhttp.OkHttpClient;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import retrofit.Callback;
import retrofit.ErrorHandler;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Client;
import retrofit.client.OkClient;
import retrofit.converter.JacksonConverter;
import retrofit.mime.TypedFile;
import timber.log.Timber;

/**
 * @author jfcartier
 * @since 2014-05-29
 */
@SuppressWarnings("FieldCanBeLocal")
public class ApiService {

    /**
     * Constants
     */
    private final String LOG_TAG = getClass().getName();

    /**
     * Service to communicate with the API
     * TODO inject me
     */
    private SnapziApi mApiService;

    /**
     * Client
     * TODO inject me
     */
    private Client mClient;

    /**
     * UserManager
     * TODO inject me
     */
    private UserManager mUserManager = new UserManager(new PreferenceManager());

    /**
     * Constructor
     */
    public ApiService() {
        this(SnapziApplication.getContext().getString(R.string.api_endpoint), new OkClient(new OkHttpClient()));
    }

    /**
     * Constructor with api endpoint
     *
     * @param endpoint
     */
    public ApiService(String endpoint, Client client) {
        mClient = client;

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setClient(client)
                .setEndpoint(endpoint)
                .setConverter(new JacksonConverter())
                .setRequestInterceptor(new RequestInterceptor() {
                    @Override
                    public void intercept(RequestFacade request) {
                        request.addHeader("api_key", getApiKey());
                        if (mUserManager.isLogged()) {
                            try {
                                request.addHeader("Authorization", getAuthenticationHeader());
                            } catch (UnauthorizedException exception) {
                                Timber.e(exception, "Unauthorized");
                            }
                        }
                    }
                })
                .setErrorHandler(new ErrorHandler() {
                    @Override
                    public Throwable handleError(RetrofitError retrofitError) {
                        if (retrofitError.getCause() != null) {
                            final Throwable cause = retrofitError.getCause();
                            if (cause instanceof UnknownHostException) {
                                return new HostUnreachableException();
                            } else if (cause instanceof SocketTimeoutException) {
                                return new NetworkTimeoutException();
                            } else if (cause instanceof MalformedURLException) {
                                return new MalformedUrlException();
                            }
                        } else if (retrofitError.getResponse() != null) {
                            final retrofit.client.Response response = retrofitError.getResponse();
                            RetrofitException exception;
                            switch (response.getStatus()) {
                                case 500:
                                    exception = new InternalServerErrorException();
                                    break;
                                case 401:
                                    exception = new UnauthorizedException();
                                    break;
                                default:
                                    exception = new ApiException();
                                    break;
                            }
                            exception.setMessage(getErrorMessage(retrofitError));
                            return exception;
                        }
                        return retrofitError;
                    }
                })
                .build();

        mApiService = restAdapter.create(SnapziApi.class);
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
    }

    /**
     * Convert an input stream to a string
     *
     * @param is
     * @return
     */
    private String convertResponseStreamToString(InputStream is) {
        int k;
        StringBuffer sb = new StringBuffer();
        try {
            while ((k = is.read()) != -1) {
                sb.append((char) k);
            }
        } catch (IOException e) {
            return null;
        }
        return sb.toString();
    }

    /**
     * Get a request error message
     *
     * @param error
     * @return
     */
    private String getErrorMessage(RetrofitError error) {
        if (error.getResponse() != null && error.getResponse().getBody() != null) {
            try {
                final String body = convertResponseStreamToString(error.getResponse().getBody().in());
                if (!TextUtils.isEmpty(body)) {
                    ObjectMapper mapper = new ObjectMapper();
                    return mapper.readValue(body, com.samsao.snapzi.api.entity.Error.class).getResponse();
                } else {
                    return null;
                }
            } catch (Exception e) {
                return null;
            }
        } else {
            return null;
        }
    }

    private String getAuthenticationHeader() throws UnauthorizedException {
        String username = mUserManager.getUsername();
        String password = mUserManager.getPassword();
        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            throw new UnauthorizedException();
        }
        return "Basic " + Base64.encodeToString(String.format("%s:%s", username, password).getBytes(), Base64.NO_WRAP);
    }

    private String getApiKey() {
        return SnapziApplication.getContext().getString(R.string.api_key);
    }

    /**
     * Login
     *
     * @param username
     * @param password
     * @param callback
     */
    public void login(String username, String password, Callback<Response> callback) {
        mApiService.login(username, password, callback);
    }

    /**
     * Register
     *
     * @param username
     * @param password
     * @param email
     * @param firstName
     * @param lastName
     * @param birthday
     * @param callback
     */
    public void register(String username, String password, String email, String firstName, String lastName, String birthday, Callback<Response> callback) {
        mApiService.register(username, password, email, firstName, lastName, birthday, callback);
    }

    /**
     * Get the live feed images
     *
     * @param callback
     */
    public void getLiveFeed(Integer campaignId, Callback<FeedImageList> callback) {
        mApiService.getLiveFeed(campaignId, callback);
    }

    /**
     * Get the live feed of the user
     */
    public void getMyLiveFeed(Callback<FeedImageList> callback) {
        //TODO pass user id to get live feed of the user
        mApiService.getMyLiveFeed(callback);
    }

    /**
     * Get the campaigns
     *
     * @param callback
     */
    public void getCampaigns(Callback<CampaignList> callback) {
        mApiService.getCampaigns(callback);
    }

    /**
     * Share a picture
     *
     * @param imagePath
     * @param text
     * @param callback
     */
    public void sharePicture(String imagePath, String text, Integer campaignId, Callback<Response> callback) {
        mApiService.sharePicture(new TypedFile("application/octet-stream", new File(imagePath)),
                text,
                !TextUtils.isEmpty(mUserManager.getFacebookAccessToken()) ? mUserManager.getFacebookAccessToken() : "",
                !TextUtils.isEmpty(mUserManager.getTwitterAccessToken()) ? mUserManager.getTwitterAccessToken() : "",
                !TextUtils.isEmpty(mUserManager.getTwitterSecret()) ? mUserManager.getTwitterSecret() : "",
                !TextUtils.isEmpty(mUserManager.getGooglePlusAccessToken()) ? mUserManager.getGooglePlusAccessToken() : "",
                campaignId,
                callback);
    }

    /**
     * Share a video
     *
     * @param imagePath
     * @param videoPath
     * @param text
     * @param callback
     */
    public void shareVideo(String imagePath, String videoPath, String text, Integer campaignId, Callback<Response> callback) {
        mApiService.shareVideo(new TypedFile("application/octet-stream", new File(imagePath)),
                new TypedFile("application/octet-stream", new File(videoPath)),
                text,
                !TextUtils.isEmpty(mUserManager.getFacebookAccessToken()) ? mUserManager.getFacebookAccessToken() : "",
                !TextUtils.isEmpty(mUserManager.getTwitterAccessToken()) ? mUserManager.getTwitterAccessToken() : "",
                !TextUtils.isEmpty(mUserManager.getTwitterSecret()) ? mUserManager.getTwitterSecret() : "",
                !TextUtils.isEmpty(mUserManager.getGooglePlusAccessToken()) ? mUserManager.getGooglePlusAccessToken() : "",
                campaignId,
                callback);
    }

    /**
     * Get the top campaign
     *
     * @param callback
     */
    public void getTopCampaign(Integer campaignId, Callback<TopCampaignList> callback) {
        mApiService.getTopCampaign(campaignId, callback);
    }

    /**
     * Get the currently logged in user's information
     * @param callback
     */
    public void getUserInfo(Callback<UserList> callback) {
        mApiService.getUserInfo(callback);
    }

    /**
     * Reports an image on the backend
     * @param imageId
     * @param callback
     */
    public void reportImage(Integer imageId, Callback<Response> callback) {
        mApiService.reportImage(imageId, mUserManager.getUsername(), callback);
    }
}

