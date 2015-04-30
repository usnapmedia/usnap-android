package com.samsao.snapzi.fan_page;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import com.samsao.snapzi.R;
import com.squareup.picasso.Picasso;

import butterknife.ButterKnife;
import butterknife.InjectView;
import icepick.Icepick;
import icepick.Icicle;

/**
 * @author jingsilu
 * @since 2015-04-30
 */
public class PhotoDetailActivity extends ActionBarActivity {
    public final static String EXTRA_PHOTO_PATH = "com.samsao.snapzi.fan_page.PhotoDetailActivity.EXTRA_PHOTO_PATH";
    public final static String EXTRA_PHOTO_TEXT = "com.samsao.snapzi.fan_page.PhotoDetailActivity.EXTRA_PHOTO_TEXT";
    public final static String EXTRA_PHOTO_USERNAME = "com.samsao.snapzi.fan_page.PhotoDetailActivity.EXTRA_PHOTO_USERNAME";
    @InjectView(R.id.activity_photo_detail_first_letter_id)
    public TextView mFirstLetterTextView;
    @InjectView(R.id.activity_photo_detail_user_name_id)
    public TextView mUserNameTextView;
    @InjectView(R.id.activity_photo_detail_time_id)
    public TextView mTimeTextView;
    @InjectView(R.id.activity_photo_detail_image_view_id)
    public ImageView mImageView;
    @InjectView(R.id.activity_photo_detail_description_id)
    public TextView mDescriptionTextView;
    @InjectView(R.id.activity_photo_detail_toolbar)
    public Toolbar mToolbar;

    @Icicle
    public String mPhotoPath;
    @Icicle
    public String mText;
    @Icicle
    public String mUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_detail);
        ButterKnife.inject(this);
        setupToolbar();

        Intent intent = getIntent();
        if (intent != null) {
            mPhotoPath = intent.getStringExtra(EXTRA_PHOTO_PATH);
            mText = intent.getStringExtra(EXTRA_PHOTO_TEXT);
            //mUsername = intent.getStringExtra(EXTRA_PHOTO_USERNAME);
            System.out.println("PhotoDetailActivity: "+mPhotoPath);
        }

        // restore saved state
        if (savedInstanceState != null) {
            Icepick.restoreInstanceState(this, savedInstanceState);
        }

        Picasso.with(this).load(mPhotoPath).into(mImageView);
        //mUserNameTextView.setText(mUsername);
        mDescriptionTextView.setText(mText);
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this, outState);
    }

    private void setupToolbar() {
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }
}
