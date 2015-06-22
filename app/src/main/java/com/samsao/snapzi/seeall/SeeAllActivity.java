package com.samsao.snapzi.seeall;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.astuetz.PagerSlidingTabStrip;
import com.samsao.snapzi.R;
import com.samsao.snapzi.SnapziApplication;
import com.samsao.snapzi.fan_page.FanPageActivity;

import butterknife.ButterKnife;
import butterknife.InjectView;
import icepick.Icepick;
import icepick.Icicle;

public class SeeAllActivity extends AppCompatActivity implements SeeAllFragment.Listener {
    private final static String EXTRA_MODE = "com.samsao.snapzi.seeall.SeeAllActivity.EXTRA_MODE";
    private final static String EXTRA_CAMPAIGN_ID = "com.samsao.snapzi.seeall.SeeAllActivity.EXTRA_CAMPAIGN_ID";
    public final static int SEE_ALL_TOP_10 = 0;
    public final static int SEE_ALL_LATEST = 1;

    // mode to know what images to fetch
    @Icicle
    public int mMode;
    @Icicle
    public int mCurrentTabPosition;
    @Icicle
    public Integer mCampaignId;

    @InjectView(R.id.activity_seeall_toolbar)
    public Toolbar mToolbar;

    @InjectView(R.id.activity_seeall_tabs)
    public PagerSlidingTabStrip mTabs;

    @InjectView(R.id.activity_seeall_view_pager)
    public ViewPager mViewPager;

    private SeeAllAdapter mSeeAllAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seeall);
        ButterKnife.inject(this);
        setupToolbar();

        if (savedInstanceState == null) {
            Intent intent = getIntent();
            if (intent != null) {
                mMode = intent.getIntExtra(EXTRA_MODE, SEE_ALL_TOP_10);
                mCampaignId = intent.getIntExtra(EXTRA_CAMPAIGN_ID, FanPageActivity.NO_CAMPAIGN_ID);
            } else {
                mMode = SEE_ALL_TOP_10;
            }
        } else {
            Icepick.restoreInstanceState(this, savedInstanceState);
        }

        mSeeAllAdapter = new SeeAllAdapter(getFragmentManager(), mMode);
        mViewPager.setAdapter(mSeeAllAdapter);

        // Bind the tabs to the ViewPager
        mTabs.setViewPager(mViewPager);
        mTabs.setTextColor(getResources().getColor(R.color.primary));

        mTabs.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//                refresh(position);
            }

            @Override
            public void onPageSelected(int position) {
                refresh(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        mTabs.setTypeface(getFont(), 0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        refresh(mCurrentTabPosition);
    }


    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this, outState);
    }

    /**
     * Remember the current tab position and refresh data according to see_all_mode
     * @param position
     */
    private void refresh(int position) {
        mCurrentTabPosition = position;
        mSeeAllAdapter.refresh(position);
    }

    /**
     * A helper class that gets GothamHTF-Book font
     * @return fontText
     */
    private Typeface getFont() {
        Typeface fontText = Typeface.createFromAsset(SnapziApplication.getContext().getAssets(), "fonts/GothamHTF-Book.ttf");
        return fontText;
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
     * Helper method to start this activity in top 10 mode
     *
     * @param context
     * @param campaignId
     */
    public static void startTop10(Context context, Integer campaignId) {
        Intent intent = new Intent(context, SeeAllActivity.class);
        if (campaignId != null) {
            intent.putExtra(EXTRA_CAMPAIGN_ID, campaignId);
        }
        intent.putExtra(EXTRA_MODE, SEE_ALL_TOP_10);
        context.startActivity(intent);
    }

    /**
     * Helper method to start this activity in latest uploads mode
     *
     * @param context
     * @param campaignId
     */
    public static void startLatestUploads(Context context, Integer campaignId) {
        Intent intent = new Intent(context, SeeAllActivity.class);
        if (campaignId != null) {
            intent.putExtra(EXTRA_CAMPAIGN_ID, campaignId);
        }
        intent.putExtra(EXTRA_MODE, SEE_ALL_LATEST);
        context.startActivity(intent);
    }

    @Override
    public Integer getCampaignId() {
        return mCampaignId;
    }
}
