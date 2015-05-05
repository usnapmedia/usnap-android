package com.samsao.snapzi.seeall;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.samsao.snapzi.api.ApiService;
import com.samsao.snapzi.api.entity.FeedImageList;
import com.samsao.snapzi.api.entity.TopCampaignList;

import icepick.Icepick;
import icepick.Icicle;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class SeeAllActivity extends AppCompatActivity implements SeeAllActivityFragment.Listener {
    private final static String EXTRA_MODE = "com.samsao.snapzi.seeall.SeeAllActivity.EXTRA_MODE";
    private final static int SEE_ALL_TOP_10 = 0;
    private final static int SEE_ALL_LATEST = 1;

    // mode to know what images to fetch
    @Icicle
    public Integer mMode;

    private SeeAllActivityFragment mSeeAllActivityFragment;
    // TODO inject me
    private ApiService mApiService = new ApiService();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            Intent intent = getIntent();
            if (intent != null) {
                mMode = intent.getIntExtra(EXTRA_MODE, SEE_ALL_TOP_10);
            } else {
                mMode = SEE_ALL_TOP_10;
            }
            mSeeAllActivityFragment = SeeAllActivityFragment.newInstance();
            getFragmentManager().beginTransaction().replace(android.R.id.content, mSeeAllActivityFragment, SeeAllActivityFragment.FRAGMENT_TAG).commit();
        } else {
            Icepick.restoreInstanceState(this, savedInstanceState);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        switch (mMode) {
            case SEE_ALL_TOP_10:
                mApiService.getTopCampaign(new Callback<TopCampaignList>() {
                    @Override
                    public void success(TopCampaignList topCampaignList, Response response) {

                    }

                    @Override
                    public void failure(RetrofitError error) {
                        // TODO string resource
                        Toast.makeText(SeeAllActivity.this, "Error fetching top 10 snaps", Toast.LENGTH_SHORT).show();
                        SeeAllActivity.this.finish();
                    }
                });
                break;
            case SEE_ALL_LATEST:
                mApiService.getLiveFeed(new Callback<FeedImageList>() {
                    @Override
                    public void success(FeedImageList feedImageList, Response response) {

                    }

                    @Override
                    public void failure(RetrofitError error) {
                        // TODO string resource
                        Toast.makeText(SeeAllActivity.this, "Error fetching top 10 snaps", Toast.LENGTH_SHORT).show();
                        SeeAllActivity.this.finish();
                    }
                });
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (mSeeAllActivityFragment != null) {
                    mSeeAllActivityFragment.onOptionsItemSelected(item);
                } else {
                    finish();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Helper method to start this activity in top 10 mode
     * @param context
     */
    public static void startTop10(Context context) {
        Intent intent = new Intent(context, SeeAllActivity.class);
        intent.putExtra(EXTRA_MODE, SEE_ALL_TOP_10);
        context.startActivity(intent);
    }

    /**
     * Helper method to start this activity in latest uploads mode
     * @param context
     */
    public static void startLatestUploads(Context context) {
        Intent intent = new Intent(context, SeeAllActivity.class);
        intent.putExtra(EXTRA_MODE, SEE_ALL_LATEST);
        context.startActivity(intent);
    }
}
