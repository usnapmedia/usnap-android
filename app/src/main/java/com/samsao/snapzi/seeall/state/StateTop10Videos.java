package com.samsao.snapzi.seeall.state;

import com.samsao.snapzi.api.ApiService;
import com.samsao.snapzi.api.entity.SnapList;

import retrofit.Callback;

/**
 * @author jfcartier
 * @since 15-06-22
 */
public class StateTop10Videos extends State {
    @Override
    public void fetchData(ApiService apiService, Integer campaignId, Callback<SnapList> callback) {
        apiService.getTopSnaps(campaignId, ApiService.FILTER_VIDEO, callback);
    }
}
