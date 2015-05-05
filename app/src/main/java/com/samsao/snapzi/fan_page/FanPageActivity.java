package com.samsao.snapzi.fan_page;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;

import com.astuetz.PagerSlidingTabStrip;
import com.samsao.snapzi.R;
import com.samsao.snapzi.SnapziApplication;
import com.samsao.snapzi.api.entity.CampaignList;
import com.samsao.snapzi.camera.SelectMediaActivity;
import com.samsao.snapzi.profile.ProfileActivity;

import butterknife.ButterKnife;
import butterknife.InjectView;
import icepick.Icepick;
import icepick.Icicle;


public class FanPageActivity extends AppCompatActivity {

    private final static String EXTRA_CAMPAIGNS = "com.samsao.snapzi.fan_page.FanPageActivity.EXTRA_CAMPAIGNS";

    @InjectView(R.id.activity_fan_page_toolbar)
    public Toolbar mToolbar;

    @InjectView(R.id.activity_fan_page_tabs)
    public PagerSlidingTabStrip mTabs;

    @InjectView(R.id.activity_fan_page_view_pager)
    public ViewPager mViewPager;

    @Icicle
    public CampaignList mCampaigns;

    private CampaignAdapter mCampaignAdapter;


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

        // Set campaign adapter
        mCampaignAdapter = new CampaignAdapter(getFragmentManager(), mCampaigns);
        mViewPager.setAdapter(mCampaignAdapter);

        // Bind the tabs to the ViewPager
        mTabs.setViewPager(mViewPager);
        mTabs.setBackgroundColor(getResources().getColor(R.color.primary));
        mTabs.setIndicatorColorResource(android.R.color.white);
        mTabs.setDividerColorResource(android.R.color.white);
        mTabs.setTextColorResource(android.R.color.white);
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
        //setTypeface(android.graphics.Typeface typeface, int style)
        mTabs.setTypeface(getFont(),0);
    }

    private Typeface getFont() {
        Typeface fontText = Typeface.createFromAsset(SnapziApplication.getContext().getAssets(), "fonts/GothamHTF-Book.ttf");
        return fontText;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this, outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_fan_page, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.activity_fan_page_menu_profile:
                ProfileActivity.start(FanPageActivity.this);
                return true;
            case R.id.activity_fan_page_menu_camera:
                SelectMediaActivity.start(this,null);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
     *
     * @param campaigns
     */
    public static void start(Context context, CampaignList campaigns) {
        Intent intent = new Intent(context, FanPageActivity.class);
        // FIXME
//        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(EXTRA_CAMPAIGNS, campaigns);
        context.startActivity(intent);
    }
}
