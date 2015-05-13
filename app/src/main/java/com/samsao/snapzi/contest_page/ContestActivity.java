package com.samsao.snapzi.contest_page;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.samsao.snapzi.R;
import com.samsao.snapzi.api.entity.Campaign;
import com.samsao.snapzi.camera.SelectMediaActivity;
import com.samsao.snapzi.profile.ProfileActivity;

import icepick.Icepick;
import icepick.Icicle;

/**
 * @author jingsilu
 * @since 2015-05-12
 */
public class ContestActivity extends AppCompatActivity implements ContestFragment.Listener {
    public static final String EXTRA_CAMPAIGN = "com.samsao.snapzi.contest_page.EXTRA_CAMPAIGN";

    @Icicle
    public Campaign mCampaign;
    private ContestFragment mContestFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            Intent intent = getIntent();
            if (intent != null) {
                mCampaign = intent.getParcelableExtra(EXTRA_CAMPAIGN);
            }
            mContestFragment = ContestFragment.newInstance();
            getFragmentManager().beginTransaction().replace(android.R.id.content, mContestFragment, ContestFragment.CONTEST_PAGE_FRAGMENT_TAG).commit();
        } else {
            Icepick.restoreInstanceState(this, savedInstanceState);
            mContestFragment = (ContestFragment) getFragmentManager().findFragmentByTag(ContestFragment.CONTEST_PAGE_FRAGMENT_TAG);
        }
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (mContestFragment != null) {
                    mContestFragment.onOptionsItemSelected(item);
                } else {
                    finish();
                }
                return true;
            case R.id.activity_fan_page_menu_profile:
                ProfileActivity.start(ContestActivity.this);
                return true;
            case R.id.activity_fan_page_menu_camera:
                SelectMediaActivity.start(this, null);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public Campaign getCampaign() {
        return mCampaign;
    }

    /**
     * Helper to start the activity
     *
     * @param campaign
     * @param context
     */
    public static void start(Campaign campaign, Context context) {
        Intent intent = new Intent(context, ContestActivity.class);
        intent.putExtra(ContestActivity.EXTRA_CAMPAIGN, campaign);
        context.startActivity(intent);
    }
}
