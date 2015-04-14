package com.samsao.snapzi.social;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;

import icepick.Icepick;
import icepick.Icicle;

public class ShareActivity extends SocialNetworkActivity {
    public static final String EXTRA_URI = "com.samsao.snapzi.social.ShareActivity.EXTRA_URI";

    private ShareFragment mShareFragment;
    @Icicle
    public Uri mImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        if (intent != null) {
            mImageUri = intent.getParcelableExtra(EXTRA_URI);
        }
        // restore saved state
        Icepick.restoreInstanceState(this, savedInstanceState);

        if (savedInstanceState == null) {
            // Create a new Fragment to be placed in the activity layout
            mShareFragment = ShareFragment.newInstance(mImageUri);

            // Add the fragment
            getFragmentManager().beginTransaction()
                    .replace(android.R.id.content, mShareFragment, SocialNetworkFragment.SOCIAL_NETWORK_TAG).commit();
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

}
