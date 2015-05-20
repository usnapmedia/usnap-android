package com.samsao.snapzi.profile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.samsao.snapzi.authentication.AuthenticationActivity;
import com.samsao.snapzi.util.PreferenceManager;
import com.samsao.snapzi.util.UserManager;


/**
 * @author vlegault
 * @since 15-04-30
 */
public class ProfileActivity extends AppCompatActivity implements ProfileProvider {

    /**
     * Constants
     */
    PreferenceManager mPreferenceManager = new PreferenceManager();
    UserManager mUserManager = new UserManager(mPreferenceManager);
    private ProfileFragment mProfileFragment;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!mUserManager.isLogged()) {
            AuthenticationActivity.start(this);
        }

        if (mProfileFragment == null) {
            mProfileFragment = ProfileFragment.newInstance();
            getFragmentManager().beginTransaction().replace(android.R.id.content, mProfileFragment).commit();
        }
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
