package com.samsao.snapzi.fan_page;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;

import com.astuetz.PagerSlidingTabStrip;
import com.samsao.snapzi.R;
import com.samsao.snapzi.api.entity.Campaign;
import com.samsao.snapzi.api.entity.CampaignList;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import icepick.Icepick;
import icepick.Icicle;

public class FanPageActivity extends ActionBarActivity {

    private final static String EXTRA_CAMPAIGNS = "com.samsao.snapzi.fan_page.FanPageActivity";

    @InjectView(R.id.activity_fan_page_viewPager)
    public ViewPager mViewPager;
    @InjectView(R.id.activity_fan_page_viewPager_pagerTabStrip)
    public PagerSlidingTabStrip mTabs;
    @InjectView(R.id.activity_fan_page_toolbar)
    public Toolbar mToolbar;
    @Icicle
    public CampaignList mCampaigns;

    private FanPageAdapter mFanPageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fan_page);
        ButterKnife.inject(this);
        setupToolbar();

        Intent intent = getIntent();
        if (intent != null) {
            mCampaigns = intent.getParcelableExtra(EXTRA_CAMPAIGNS);
        }

        // restore saved state
        if (savedInstanceState != null) {
            Icepick.restoreInstanceState(this, savedInstanceState);
        }

        mFanPageAdapter = new FanPageAdapter(getFragmentManager(), mCampaigns);
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
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this, outState);
    }

    /**
     * Setup the toolbar for this activity
     */
    public void setupToolbar() {
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    /**
     * Helper method to start this activity
     * @param list
     */
    public static void start(Context context, CampaignList list) {
        Intent intent = new Intent(context, FanPageActivity.class);
        intent.putExtra(EXTRA_CAMPAIGNS, list);
        context.startActivity(intent);
    }

    public static class FanPageAdapter extends FragmentStatePagerAdapter {
        /**
         * List of fragments (campaigns) in the activity
         */
        private List<WeakReference<CampaignFragment>> mFanPageFragments;

        public FanPageAdapter(FragmentManager fragmentManager, CampaignList list) {
            super(fragmentManager);
            mFanPageFragments = new ArrayList<>();
            for (Campaign campaign : list.getResponse()) {
                mFanPageFragments.add(new WeakReference<>(CampaignFragment.newInstance(campaign.getName(), campaign.getBannerImgUrl())));
            }
        }

        @Override
        public int getCount() {
            return mFanPageFragments.size();
        }

        @Override
        public Fragment getItem(int position) {
            return mFanPageFragments.get(position).get();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFanPageFragments.get(position).get().getName();
        }
    }
}
