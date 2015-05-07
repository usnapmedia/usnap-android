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

import butterknife.ButterKnife;
import butterknife.InjectView;
import icepick.Icepick;
import icepick.Icicle;

public class SeeAllActivity extends AppCompatActivity implements SeeAllFragment.Listener {
    private final static String EXTRA_MODE = "com.samsao.snapzi.seeall.SeeAllActivity.EXTRA_MODE";
    private final static int SEE_ALL_TOP_10 = 0;
    private final static int SEE_ALL_LATEST = 1;

    // mode to know what images to fetch
    @Icicle
    public int mMode;

//    private SeeAllFragment mSeeAllFragment;


    @InjectView(R.id.activity_seeall_toolbar)
    public Toolbar mToolbar;

    @InjectView(R.id.activity_seeall_tabs)
    public PagerSlidingTabStrip mTabs;

    @InjectView(R.id.activity_seeall_view_pager)
    public ViewPager mViewPager;

    private SeeAllAdapter mSeeAllAdapter;

    @Icicle
    public int mCurrentTabPosition;

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

            } else {
                mMode = SEE_ALL_TOP_10;
            }
//            // create the fragment
//            mSeeAllFragment = SeeAllFragment.newInstance();
//            getFragmentManager().beginTransaction().replace(android.R.id.content, mSeeAllFragment, SeeAllFragment.FRAGMENT_TAG).commit();
        } else {
            Icepick.restoreInstanceState(this, savedInstanceState);
        }

        //TODO /feed/top/photos
        //TODO /feed/top/videos

        mSeeAllAdapter = new SeeAllAdapter(getFragmentManager());
        mViewPager.setAdapter(mSeeAllAdapter);

        // Bind the tabs to the ViewPager
        mTabs.setViewPager(mViewPager);
        mTabs.setBackgroundColor(getResources().getColor(R.color.primary));
        mTabs.setIndicatorColorResource(android.R.color.white);
        mTabs.setDividerColorResource(android.R.color.white);
        mTabs.setTextColorResource(android.R.color.white);
        mTabs.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                refresh(position);
            }

            @Override
            public void onPageSelected(int position) {
                refresh(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        //setTypeface(android.graphics.Typeface typeface, int style)
        mTabs.setTypeface(getFont(),0);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this, outState);
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

    /**
     * Remember the current tab position and refresh data according to see_all_mode
     * @param position
     */

    private void refresh(int position) {
        mCurrentTabPosition = position;
        System.out.println("##################################");
        switch (position) {
            case SeeAllAdapter.FRAGMENT_SEE_ALL_PHOTOS:
                mSeeAllAdapter.refreshPhotos();
                break;
            case SeeAllAdapter.FRAGMENT_SEE_ALL_VIDEOS:
                mSeeAllAdapter.refreshVideos();
                break;
            case SeeAllAdapter.FRAGMENT_SEE_ALL_ALL:
                mSeeAllAdapter.refreshAll();
                break;
            default:
                break;
        }
    }

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


    @Override
    public int getMode() {
        return mMode;
    }
}
