package com.samsao.snapzi.social;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.samsao.snapzi.fan_page.FanPageActivity;

import icepick.Icepick;
import icepick.Icicle;

public class ShareActivity extends SocialNetworkActivity implements ShareFragment.Listener {

    /**
     * Constants
     */
    private static final String EXTRA_IMAGE_PATH = "com.samsao.snapzi.social.SocialNetworkActivity.EXTRA_IMAGE_PATH";
    private static final String EXTRA_VIDEO_PATH = "com.samsao.snapzi.social.SocialNetworkActivity.EXTRA_VIDEO_PATH";
    private static final String EXTRA_CAMPAIGN_ID = "com.samsao.snapzi.social.ShareActivity.EXTRA_CAMPAIGN_ID";

    @Icicle
    public String mImagePath;
    @Icicle
    public String mVideoPath;
    @Icicle
    public Integer mCampaignId;

    private ShareFragment mShareFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        if (intent != null) {
            mImagePath = intent.getStringExtra(EXTRA_IMAGE_PATH);
            mCampaignId = intent.getIntExtra(EXTRA_CAMPAIGN_ID, FanPageActivity.NO_CAMPAIGN_ID);
            mVideoPath = intent.getStringExtra(EXTRA_VIDEO_PATH);
        }
        // restore saved state
        if (savedInstanceState != null) {
            Icepick.restoreInstanceState(this, savedInstanceState);
            mShareFragment = (ShareFragment) getFragmentManager().findFragmentByTag(ShareFragment.SHARE_FRAGMENT_TAG);
        }

        if (savedInstanceState == null) {
            mShareFragment = ShareFragment.newInstance();
            getFragmentManager().beginTransaction().replace(android.R.id.content, mShareFragment, ShareFragment.SHARE_FRAGMENT_TAG).commit();
        }
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this, outState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (mShareFragment != null) {
                    mShareFragment.onOptionsItemSelected(item);
                } else {
                    finish();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public Integer getCampaignId() {
        return mCampaignId;
    }

    @Override
    public String getImagePath() {
        return mImagePath;
    }

    @Override
    public String getVideoPath() {
        return mVideoPath;
    }

    /**
     * Helper method to start this activity
     *
     */
    public static void start(Context context, Integer mCampaignId, String imageDestinationPath, String videoPath) {
        Intent intent = new Intent(context, ShareActivity.class);
        intent.putExtra(ShareActivity.EXTRA_IMAGE_PATH, imageDestinationPath); // Keep image in both cases
        intent.putExtra(ShareActivity.EXTRA_CAMPAIGN_ID, mCampaignId);
        intent.putExtra(ShareActivity.EXTRA_VIDEO_PATH, videoPath);
        context.startActivity(intent);
    }
}
