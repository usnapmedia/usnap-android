package com.samsao.snapzi.authentication;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.samsao.snapzi.R;


public class LoginActivity extends ActionBarActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (savedInstanceState == null) {
            // Create a new Fragment to be placed in the activity layout
            LoginFragment loginFragment = LoginFragment.newInstance();

            // Add the fragment to the 'fragment_container' FrameLayout
            getFragmentManager().beginTransaction()
                    .replace(R.id.activity_login_fragment_container, loginFragment).commit();
        }
    }
}
