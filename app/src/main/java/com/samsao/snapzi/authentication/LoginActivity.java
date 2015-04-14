package com.samsao.snapzi.authentication;

import android.os.Bundle;

import com.samsao.snapzi.authentication.view.LoginFragment;
import com.samsao.snapzi.social.SocialNetworkActivity;
import com.samsao.snapzi.social.SocialNetworkFragment;


public class LoginActivity extends SocialNetworkActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            // Create a new Fragment to be placed in the activity layout
            LoginFragment loginFragment = LoginFragment.newInstance();

            // Add the fragment to the 'fragment_container' FrameLayout
            getFragmentManager().beginTransaction()
                    .replace(android.R.id.content, loginFragment, SocialNetworkFragment.SOCIAL_NETWORK_TAG).commit();
        }
    }
}
