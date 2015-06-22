package com.samsao.snapzi.seeall.state;

import com.samsao.snapzi.api.ApiService;
import com.samsao.snapzi.api.entity.SnapList;

import java.io.Serializable;

import retrofit.Callback;

/**
 * @author jfcartier
 * @since 15-06-22
 */
public abstract class State implements Serializable {
    public abstract void fetchData(ApiService apiService, Integer campaignId, Callback<SnapList> callback);
}
