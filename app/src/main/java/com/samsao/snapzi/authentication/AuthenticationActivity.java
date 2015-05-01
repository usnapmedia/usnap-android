package com.samsao.snapzi.authentication;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;

import com.astuetz.PagerSlidingTabStrip;
import com.samsao.snapzi.R;
import com.samsao.snapzi.util.KeyboardUtil;

import butterknife.ButterKnife;
import butterknife.InjectView;
import icepick.Icepick;


public class AuthenticationActivity extends ActionBarActivity {
    @InjectView(R.id.activity_authentication_toolbar)
    public Toolbar mToolbar;

    @InjectView(R.id.activity_authentication_tabs)
    public PagerSlidingTabStrip mTabs;

    @InjectView(R.id.activity_authentication_view_pager)
    public ViewPager mViewPager;

    private AuthenticationAdapter mAuthenticationAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);
        ButterKnife.inject(this);
        setupToolbar();

        // restore saved state
        if (savedInstanceState != null) {
            Icepick.restoreInstanceState(this, savedInstanceState);
        }

        // Set campaign adapter
        mAuthenticationAdapter = new AuthenticationAdapter(getFragmentManager());
        mViewPager.setAdapter(mAuthenticationAdapter);
        mTabs.setShouldExpand(true);
        // Bind the tabs to the ViewPager
        mTabs.setViewPager(mViewPager);
        mTabs.setBackgroundColor(getResources().getColor(R.color.primary));
        mTabs.setTextColorResource(android.R.color.white);
        mTabs.setIndicatorColorResource(android.R.color.white);
        mTabs.setDividerColorResource(android.R.color.white);
        mTabs.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                KeyboardUtil.hideKeyboard(AuthenticationActivity.this);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
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
     * Setup the toolbar for this activity
     */
    private void setupToolbar() {
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
    }

    /**
     * Helper method to start this activity
     *
     */
    public static void start(Context context) {
        Intent intent = new Intent(context, AuthenticationActivity.class);
        context.startActivity(intent);
    }


}
