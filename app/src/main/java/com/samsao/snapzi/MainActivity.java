package com.samsao.snapzi;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.samsao.snapzi.camera.SelectMediaActivity;


public class MainActivity extends ActionBarActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SelectMediaActivity.start(this);
        finish();
    }
}
