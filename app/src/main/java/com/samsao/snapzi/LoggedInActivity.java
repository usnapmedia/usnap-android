package com.samsao.snapzi;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.samsao.snapzi.authentication.AuthenticationActivity;
import com.samsao.snapzi.util.PreferenceManager;
import com.samsao.snapzi.util.UserManager;

/**
 * Parent Activity when the user needs to be logged in to access it.
 * If the user is not logged in, the AuthenticationActivity is launched.
 *
 * @author jfcartier
 * @since 15-06-05
 */
public abstract class LoggedInActivity extends AppCompatActivity {

    // TODO inject me
    protected PreferenceManager mPreferenceManager = new PreferenceManager();
    protected UserManager mUserManager = new UserManager(mPreferenceManager);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!mUserManager.isLogged()) {
            AuthenticationActivity.startForResult(this);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case AuthenticationActivity.REQ_CODE:
                if (resultCode != RESULT_OK) {
                    finish();
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }
}
