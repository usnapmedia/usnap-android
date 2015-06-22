package com.samsao.snapzi.seeall.state;

import com.samsao.snapzi.api.ApiService;
import com.samsao.snapzi.api.entity.SnapList;

import retrofit.Callback;

/**
 * @author jfcartier
 * @since 15-06-22
 */
public class StateLatestAll extends State {
    @Override
    public void fetchData(ApiService apiService, Integer campaignId, Callback<SnapList> callback) {
        apiService.getLiveFeed(campaignId, callback);
    }
}
