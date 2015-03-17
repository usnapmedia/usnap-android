package com.samsao.snapzi.social;

import android.app.Fragment;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;

/**
 * @author jfcartier
 * @since 15-03-13
 */
public class SocialNetworkActivity extends ActionBarActivity {

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /**
         * This is required only if you are using Google Plus, the issue is that there SDK
         * require Activity to launch Auth, so library can't receive onActivityResult in fragment
         */
        Fragment fragment = getFragmentManager().findFragmentByTag(SocialNetworkFragment.SOCIAL_NETWORK_TAG);
        if (fragment != null) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }

}
