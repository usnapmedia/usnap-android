package com.samsao.snapzi.social;

import android.os.Bundle;
import android.view.MenuItem;

public class ShareActivity extends SocialNetworkActivity {

    private ShareFragment mShareFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            // Create a new Fragment to be placed in the activity layout
            mShareFragment = ShareFragment.newInstance();

            // Add the fragment
            getFragmentManager().beginTransaction()
                    .replace(android.R.id.content, mShareFragment, SocialNetworkFragment.SOCIAL_NETWORK_TAG).commit();
        }
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
