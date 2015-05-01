package com.samsao.snapzi.profile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;

import icepick.Icepick;


/**
 * @author vlegault
 * @since 15-04-30
 */
public class ProfileActivity extends ActionBarActivity implements ProfileProvider {

    /**
     * Constants
     */
    private final String LOG_TAG = getClass().getSimpleName();

    private ProfileFragment mProfileFragment;

    public Toolbar mToolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // restore saved state
        if (savedInstanceState != null) {
            Icepick.restoreInstanceState(this, savedInstanceState);
        }

        if (mProfileFragment == null) {
            mProfileFragment = ProfileFragment.newInstance();
            getFragmentManager().beginTransaction().replace(android.R.id.content, mProfileFragment).commit();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this, outState);

    }

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void setupToolbar(Toolbar toolbar) {
        if (toolbar != null) {
            mToolbar = toolbar;
            setSupportActionBar(mToolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
    }

    /**
     * Helper method to start this activity
     *
     * @param context
     */
    public static void start(Context context) {
        Intent intent = new Intent(context, ProfileActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }
}
