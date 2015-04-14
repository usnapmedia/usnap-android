/*
 * Copyright (c) 2014 Samsao Development Inc.
 */

package com.samsao.snapzi.api;

import android.text.TextUtils;
import android.util.Base64;

import com.samsao.snapzi.BuildConfig;
import com.samsao.snapzi.R;
import com.samsao.snapzi.SnapziApplication;
import com.samsao.snapzi.api.entity.Response;
import com.samsao.snapzi.util.PreferenceManager;
import com.samsao.snapzi.util.UserManager;
import com.squareup.okhttp.OkHttpClient;

import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.nio.charset.Charset;

import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Client;
import retrofit.client.OkClient;
import retrofit.converter.JacksonConverter;
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
    private final String ERROR_HOST_UNREACHABLE = "Host unreachable";
    private final String ERROR_TIMEOUT = "Network timeout";
    private final String ERROR_MALFORMED_URL = "Malformed URL";
    private final String ERROR_STATUS_500 = "Status 500";
    private final String ERROR_UNAUTHORIZED = "Unauthorized";
    private final String ERROR_CONFLICT = "Conflict";
    private final String ERROR_UNKNOWN = "Unkwnown error";

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
                        if (mUserManager.isLogged()) {
                            try {
                                request.addHeader("Authorization", getAuthenticationHeader());
                            } catch (UnauthorizedException exception) {
                                Timber.e(exception, "Unauthorized");
                            }
                        }
                    }
                })
                .build();

        mApiService = restAdapter.create(SnapziApi.class);
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
    }

    /**
     * Get the appropriate error message depending on the connection error cause
     *
     * @param error
     * @return Error message
     */
    public String getErrorMessage(RetrofitError error) {
        if (error != null) {
            if (error.getCause() != null) {
                if (error.getCause() instanceof UnknownHostException) {
                    return ERROR_HOST_UNREACHABLE;
                } else if (error.getCause() instanceof SocketTimeoutException) {
                    return ERROR_TIMEOUT;
                } else if (error.getCause() instanceof MalformedURLException) {
                    return ERROR_MALFORMED_URL;
                }
            } else if (error.getResponse() != null) {
                if (error.getResponse().getStatus() == 500) {
                    return ERROR_STATUS_500;
                } else if (error.getResponse().getStatus() == 401) {
                    return ERROR_UNAUTHORIZED;
                } else if (error.getResponse().getStatus() == 409) {
                    return ERROR_CONFLICT;
                }
            }
        }
        return ERROR_UNKNOWN;
    }

    private String getAuthenticationHeader() throws UnauthorizedException {
        String username = mUserManager.getUsername();
        String password = mUserManager.getPassword();
        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            throw new UnauthorizedException("Not logged in.");
        }
        return Base64.encodeToString(new String("Basic " + username + ":" + password).getBytes(Charset.forName("UTF-8")), Base64.DEFAULT);
    }

    /**
     * Login
     * @param username
     * @param password
     * @param callback
     */
    public void login(String username, String password, Callback<Response> callback) {
        mApiService.login(username, password, callback);
    }
}

