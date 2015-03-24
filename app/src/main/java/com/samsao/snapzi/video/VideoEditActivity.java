package com.samsao.snapzi.video;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import icepick.Icepick;
import icepick.Icicle;


public class VideoEditActivity extends ActionBarActivity implements VideoEditFragment.Listener {

    /**
     * Constants
     */
    public static final String EXTRA_VIDEO_PATH = "com.samsao.snapzi.photo.VideoEditActivity.EXTRA_VIDEO_PATH";

    @Icicle
    public String mVideoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        if (intent != null) {
            mVideoPath = intent.getStringExtra(EXTRA_VIDEO_PATH);
        }

        // restore saved state
        Icepick.restoreInstanceState(this, savedInstanceState);

        if (savedInstanceState == null) {
            VideoEditFragment fragment = VideoEditFragment.newInstance();
            getFragmentManager().beginTransaction().replace(android.R.id.content, fragment).commit();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this, outState);
    }

    @Override
    public String getVideoPath() {
        return mVideoPath;
    }
}
