package com.samsao.snapzi.camera;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public class SelectMediaActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SelectMediaFragment selectMediaFragment = new SelectMediaFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(android.R.id.content, selectMediaFragment).commit();
    }
}