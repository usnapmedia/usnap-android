package com.samsao.snapzi.fan_page;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;

import com.astuetz.PagerSlidingTabStrip;
import com.samsao.snapzi.R;
import com.samsao.snapzi.api.ApiService;
import com.samsao.snapzi.api.entity.Campaigns;
import com.samsao.snapzi.api.entity.CampaignsList;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

public class FanPageActivity extends ActionBarActivity {

    @InjectView(R.id.activity_fan_page_viewPager)
    public ViewPager mViewPager;
    @InjectView(R.id.activity_fan_page_viewPager_pagerTabStrip)
    public PagerSlidingTabStrip mTabs;
    @InjectView(R.id.activity_fan_page_toolbar)
    public Toolbar mToolbar;
    private FanPageAdapter mFanPageAdapter;
    private ArrayList<FanPageFragment> mFanPageFragments;
    private ApiService mApiService = new ApiService();;
    private List<Campaigns> mCampaignsList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fan_page);
        ButterKnife.inject(this);
        setupToolbar();
        mCampaignsList = new ArrayList<>();
        mFanPageFragments = new ArrayList<>();
        mFanPageAdapter = new FanPageAdapter(getFragmentManager(), mFanPageFragments);
        mViewPager.setAdapter(mFanPageAdapter);
        mTabs = (PagerSlidingTabStrip) findViewById(R.id.activity_fan_page_viewPager_pagerTabStrip);
        // Bind the tabs to the ViewPager
        mTabs.setViewPager(mViewPager);
        mTabs.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        getCampaigns();
    }

    public void getCampaigns() {
        mApiService.getCampaigns(new Callback<CampaignsList>() {
            @Override
            public void success(CampaignsList campaignsList, Response response) {
                setCampaignsList(campaignsList.getResponse());
                initCampaigns();
            }

            @Override
            public void failure(RetrofitError error) {
                Timber.e("Error Fetching Campaigns!");
            }
        });
    }

    public void initCampaigns() {
        for (int i = 0; i < mCampaignsList.size(); i++) {
            Campaigns campaigns = mCampaignsList.get(i);
            mFanPageFragments.add(FanPageFragment.newInstance(campaigns.getName(), campaigns.getBannerImgUrl()));
        }
    }

    public void setupToolbar() {
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    public void setCampaignsList(List<Campaigns> campaignsList) {
        mCampaignsList = campaignsList;
    }

    public static class FanPageAdapter extends FragmentStatePagerAdapter{
        private final ArrayList<FanPageFragment> mFanPageFragments;

        public FanPageAdapter(FragmentManager fragmentManager, ArrayList<FanPageFragment> fanPageFragments) {
            super(fragmentManager);
            this.mFanPageFragments = fanPageFragments;
        }

        @Override
        public int getCount() {
            return mFanPageFragments.size();
        }

        @Override
        public Fragment getItem(int position) {
            return mFanPageFragments.get(position);
        }


        @Override
        public CharSequence getPageTitle(int position) {
            return mFanPageFragments.get(position).getName();
        }
    }
}
