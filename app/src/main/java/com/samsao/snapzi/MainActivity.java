package com.samsao.snapzi;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.samsao.snapzi.authentication.LoginActivity;
import com.samsao.snapzi.camera.SelectMediaActivity;
import com.samsao.snapzi.util.UserManager;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (UserManager.isLogged()) {
            startActivity(new Intent(this, SelectMediaActivity.class));
        } else {
            startActivity(new Intent(this, LoginActivity.class));
        }
    }
}
