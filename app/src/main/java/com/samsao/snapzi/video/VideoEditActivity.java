package com.samsao.snapzi.video;


import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.samsao.snapzi.photo.PhotoEditFragment;


public class VideoEditActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            PhotoEditFragment fragment = PhotoEditFragment.newInstance();
            getFragmentManager().beginTransaction().replace(android.R.id.content, fragment).commit();
        }
    }
}
