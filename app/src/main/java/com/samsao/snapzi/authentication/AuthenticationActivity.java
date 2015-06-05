package com.samsao.snapzi.authentication;

import android.app.Activity;
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
import com.samsao.snapzi.util.KeyboardUtil;
import com.samsao.snapzi.util.StringUtil;

import butterknife.ButterKnife;
import butterknife.InjectView;
import icepick.Icepick;


public class AuthenticationActivity extends AppCompatActivity {
    public final static int REQ_CODE = 35343;

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
        mTabs.setTypeface(getFont(), 0);
    }

    private Typeface getFont() {
        Typeface fontText = Typeface.createFromAsset(SnapziApplication.getContext().getAssets(), "fonts/GothamHTF-Book.ttf");
        return fontText;
    }

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent data = new Intent();
                setResult(Activity.RESULT_CANCELED, data);
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
        getSupportActionBar().setTitle(StringUtil.getAppFontString(R.string.authentication));
    }

    /**
     * Helper method to start this activity
     */
    public static void start(Context context) {
        Intent intent = new Intent(context, AuthenticationActivity.class);
        context.startActivity(intent);
    }

    /**
     * Helper method to start this activity for result
     */
    public static void startForResult(Activity activity) {
        Intent intent = new Intent(activity, AuthenticationActivity.class);
        activity.startActivityForResult(intent, REQ_CODE);
    }
}
