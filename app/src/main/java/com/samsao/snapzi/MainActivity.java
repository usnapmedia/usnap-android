package com.samsao.snapzi;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.samsao.snapzi.api.ApiService;
import com.samsao.snapzi.api.entity.CampaignList;
import com.samsao.snapzi.fan_page.FanPageActivity;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;


public class MainActivity extends AppCompatActivity {
    // TODO inject me
    private ApiService mApiService = new ApiService();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO show loading dialog
        // fetch campaigns and start FanPageActivity
        mApiService.getCampaigns(new Callback<CampaignList>() {
            @Override
            public void success(CampaignList campaignList, Response response) {
                FanPageActivity.start(MainActivity.this, campaignList);
                finish();
            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(MainActivity.this, "Error fetching campaigns: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                Timber.e("Error Fetching Campaigns: " + error.getMessage());
                finish();
            }
        });
    }
}
