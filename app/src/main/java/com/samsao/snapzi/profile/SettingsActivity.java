package com.samsao.snapzi.profile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.samsao.snapzi.social.SocialNetworkActivity;

/**
 * @author jingsilu
 * @since 2015-05-05
 */
public class SettingsActivity extends SocialNetworkActivity implements SettingsFragment.Listener {
    private SettingsFragment mSettingsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (mSettingsFragment == null) {
            mSettingsFragment = SettingsFragment.newInstance();
            getFragmentManager().beginTransaction().replace(android.R.id.content, mSettingsFragment).commit();
        }
    }




    public static void start(Context context) {
        Intent intent = new Intent(context, SettingsActivity.class);
        context.startActivity(intent);
    }
}
