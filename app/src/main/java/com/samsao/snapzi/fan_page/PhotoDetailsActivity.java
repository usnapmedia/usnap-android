package com.samsao.snapzi.fan_page;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;

import icepick.Icepick;
import icepick.Icicle;

/**
 * @author jingsilu
 * @since 2015-04-30
 */
public class PhotoDetailsActivity extends ActionBarActivity implements PhotoDetailsFragment.Listener {
    public final static String EXTRA_PHOTO_PATH = "com.samsao.snapzi.fan_page.PhotoDetailActivity.EXTRA_PHOTO_PATH";
    public final static String EXTRA_PHOTO_TEXT = "com.samsao.snapzi.fan_page.PhotoDetailActivity.EXTRA_PHOTO_TEXT";
    public final static String EXTRA_PHOTO_USERNAME = "com.samsao.snapzi.fan_page.PhotoDetailActivity.EXTRA_PHOTO_USERNAME";

    @Icicle
    public String mPhotoPath;
    @Icicle
    public String mText;
    @Icicle
    public String mUsername;

    private PhotoDetailsFragment mPhotoDetailsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            Intent intent = getIntent();
            if (intent != null) {
                mPhotoPath = intent.getStringExtra(EXTRA_PHOTO_PATH);
                mText = intent.getStringExtra(EXTRA_PHOTO_TEXT);
                mUsername = intent.getStringExtra(EXTRA_PHOTO_USERNAME);
            }
            mPhotoDetailsFragment = PhotoDetailsFragment.newInstance();
            getFragmentManager().beginTransaction().replace(android.R.id.content, mPhotoDetailsFragment, PhotoDetailsFragment.PHOTO_DETAILS_FRAGMENT_TAG).commit();
        } else {
            Icepick.restoreInstanceState(this, savedInstanceState);
            mPhotoDetailsFragment = (PhotoDetailsFragment) getFragmentManager().findFragmentByTag(PhotoDetailsFragment.PHOTO_DETAILS_FRAGMENT_TAG);
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
                if (mPhotoDetailsFragment != null) {
                    mPhotoDetailsFragment.onOptionsItemSelected(item);
                } else {
                    finish();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public String getPhotoPath() {
        return mPhotoPath;
    }

    public String getText() {
        return mText;
    }

    public String getUsername() {
        return mUsername;
    }
}
