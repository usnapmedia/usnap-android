package com.samsao.snapzi.preferences;

import android.os.Bundle;

import com.samsao.snapzi.R;
import com.samsao.snapzi.social.SocialNetworkActivity;

public class PreferencesActivity extends SocialNetworkActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);

        if (savedInstanceState == null) {
            // Create a new Fragment to be placed in the activity layout
            PreferencesFragment preferencesFragment = PreferencesFragment.newInstance();

            // Add the fragment to the 'fragment_container' FrameLayout
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.activity_preferences_fragment_container, preferencesFragment).commit();
        }
    }
}
