package com.samsao.snapzi.social;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.samsao.snapzi.R;

import icepick.Icepick;
import icepick.Icicle;

public class ShareActivity extends SocialNetworkActivity implements ShareFragment.Listener {

    /**
     * Constants
     */
    private final String LOG_TAG = getClass().getSimpleName();
    public static final String EXTRA_MEDIA_TYPE = "com.samsao.snapzi.social.SocialNetworkActivity.EXTRA_MEDIA_TYPE";
    public static final String EXTRA_IMAGE_PATH = "com.samsao.snapzi.social.SocialNetworkActivity.EXTRA_IMAGE_PATH";
    public static final String EXTRA_VIDEO_PATH = "com.samsao.snapzi.social.SocialNetworkActivity.EXTRA_VIDEO_PATH";
    public static final String EXTRA_COMMENT_TEXT = "com.samsao.snapzi.social.SocialNetworkActivity.EXTRA_COMMENT_TEXT";
    public static final String TYPE_IMAGE = "com.samsao.snapzi.social.SocialNetworkActivity.TYPE_IMAGE";
    public static final String TYPE_VIDEO = "com.samsao.snapzi.social.SocialNetworkActivity.TYPE_VIDEO";
    public static final String EXTRA_CAMPAIGN_ID = "com.samsao.snapzi.social.ShareActivity.EXTRA_CAMPAIGN_ID";

    @Icicle
    public String mMediaType;
    @Icicle
    public String mImagePath;
    @Icicle
    public String mVideoPath;
    @Icicle
    public String mCommentText;

    private ShareFragment mShareFragment;

    private int mCampaignId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        if (intent != null) {
            mMediaType = intent.getStringExtra(EXTRA_MEDIA_TYPE);
            mImagePath = intent.getStringExtra(EXTRA_IMAGE_PATH);
            mCampaignId = intent.getIntExtra(EXTRA_CAMPAIGN_ID, 0);
            if (mMediaType.equals(TYPE_VIDEO)) {
                mVideoPath = intent.getStringExtra(EXTRA_VIDEO_PATH);
            }
        }
        // restore saved state
        if (savedInstanceState != null) {
            Icepick.restoreInstanceState(this, savedInstanceState);
            mMediaType = savedInstanceState.getString(EXTRA_MEDIA_TYPE);
            mImagePath = savedInstanceState.getString(EXTRA_IMAGE_PATH);
            mVideoPath = savedInstanceState.getString(EXTRA_VIDEO_PATH);
            mCommentText = savedInstanceState.getString(EXTRA_COMMENT_TEXT);

            mShareFragment = (ShareFragment) getFragmentManager().findFragmentByTag(ShareFragment.SHARE_FRAGMENT_TAG);
        }

        if (mMediaType == null || !(mMediaType.equals(TYPE_IMAGE) || mMediaType.equals(TYPE_VIDEO))) {
            Log.e(LOG_TAG, "Unrecognized share mode was provided, closing ShareActivity");
            Toast.makeText(this,
                    getResources().getString(R.string.error_unknown),
                    Toast.LENGTH_LONG).show();
            finish();
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
        outState.putString(EXTRA_MEDIA_TYPE, mMediaType);
        outState.putString(EXTRA_IMAGE_PATH, mImagePath);
        outState.putString(EXTRA_VIDEO_PATH, mVideoPath);
        outState.putString(EXTRA_COMMENT_TEXT, mCommentText);
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
    public int getCampaignId() {
        return mCampaignId;
    }

    @Override
    public String getMediaType() {
        return mMediaType;
    }

    @Override
    public String getImagePath() {
        return mImagePath;
    }

    @Override
    public String getVideoPath() {
        return mVideoPath;
    }

    public void setCommentText(String commentText) {
        mCommentText = commentText;
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mMediaType = savedInstanceState.getString(EXTRA_MEDIA_TYPE);
        mImagePath = savedInstanceState.getString(EXTRA_IMAGE_PATH);
        mVideoPath = savedInstanceState.getString(EXTRA_VIDEO_PATH);
        mCommentText = savedInstanceState.getString(EXTRA_COMMENT_TEXT);
    }
}
