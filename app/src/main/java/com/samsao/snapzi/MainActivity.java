package com.samsao.snapzi;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.samsao.snapzi.authentication.LoginActivity;
import com.samsao.snapzi.camera.SelectMediaActivity;
import com.samsao.snapzi.util.PreferenceManager;
import com.samsao.snapzi.util.UserManager;


public class MainActivity extends ActionBarActivity {

    // TODO inject me
    private UserManager mUserManager = new UserManager(new PreferenceManager());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (mUserManager.isLogged()) {
            startActivity(new Intent(this, SelectMediaActivity.class));
        } else {
            startActivity(new Intent(this, LoginActivity.class));
        }
    }
}
