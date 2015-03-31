/*
 * Copyright (c) 2014 Samsao Development Inc.
 */

package com.samsao.snapzi.api;

import com.samsao.snapzi.R;
import com.samsao.snapzi.SnapziApplication;
import com.samsao.snapzi.api.entity.Response;

import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.converter.JacksonConverter;

/**
 * @author jfcartier
 * @since 2014-05-29
 */
@SuppressWarnings("FieldCanBeLocal")
public final class ApiService {

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
     */
    private SnapziApi mApiService;

    /**
     * Constructor
     */
    public ApiService() {
        this(SnapziApplication.getContext().getString(R.string.api_endpoint));
    }

    /**
     * Constructor with api endpoint
     *
     * @param endpoint
     */
    public ApiService(String endpoint) {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(endpoint)
                .setConverter(new JacksonConverter())
                .build();

        mApiService = restAdapter.create(SnapziApi.class);
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

    /**
     * Register
     * @param email
     * @param password
     * @param callback
     */
    public void register(String email, String password, Callback<Response> callback) {
        mApiService.register(email, password, callback);
    }

    /**
     * Login
     * @param email
     * @param password
     * @param callback
     */
    public void login(String email, String password, Callback<Response> callback) {
        mApiService.login(email, password, callback);
    }
}

