package com.samsao.snapzi.photo;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.*;
import android.view.MenuItem;

import com.samsao.snapzi.R;
import com.samsao.snapzi.util.PhotoUtil;
import com.samsao.snapzi.util.SaveImageCallback;
import com.soundcloud.android.crop.Crop;

import butterknife.ButterKnife;
import butterknife.InjectView;
import icepick.Icepick;
import icepick.Icicle;

public class PhotoEditActivity extends ActionBarActivity implements PhotoEditFragment.Listener {
    public static final String EXTRA_URI = "com.samsao.snapzi.photo.PhotoEditActivity.EXTRA_URI";
    // contrast varies from 0 to 4.0, but progress bar from 0 to MAX -> initial contrast is 10 (1.0) and max is 40
    private final int INITIAL_CONTRAST = 10;

    @InjectView(R.id.activity_photo_edit_toolbar)
    public Toolbar mToolbar;

    private Menu mMenu;
    private PhotoEditFragment mPhotoEditFragment;

    @Icicle
    public int mContrast;
    @Icicle
    public Uri mImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_edit);
        ButterKnife.inject(this);
        setupToolbar();

        Intent intent = getIntent();
        if (intent != null) {
            mImageUri = intent.getParcelableExtra(EXTRA_URI);
        }
        mContrast = INITIAL_CONTRAST;
        // restore saved state
        Icepick.restoreInstanceState(this, savedInstanceState);

        if (savedInstanceState == null) {
            mPhotoEditFragment = PhotoEditFragment.newInstance();
            getFragmentManager().beginTransaction().replace(R.id.activity_photo_edit_content, mPhotoEditFragment).commit();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this, outState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // When an image as been cropped
        if (requestCode == Crop.REQUEST_CROP
                && resultCode == Activity.RESULT_OK
                && null != data) {
            if (mPhotoEditFragment != null) {
                mPhotoEditFragment.refreshImage();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        mMenu = menu;
        getMenuInflater().inflate(R.menu.activity_photo_edit, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        switch (item.getItemId()) {
            case R.id.activity_photo_edit_next:
                if (mPhotoEditFragment != null) {
                    mPhotoEditFragment.onOptionsNextSelected();
                }
                return true;
            case R.id.activity_photo_edit_clear:
                if (mPhotoEditFragment != null) {
                    mPhotoEditFragment.onOptionsClearSelected();
                }
                return true;
            case R.id.activity_photo_edit_undo:
                if (mPhotoEditFragment != null) {
                    mPhotoEditFragment.onOptionsUndoSelected();
                }
                return true;
            case R.id.activity_photo_edit_done:
                if (mPhotoEditFragment != null) {
                    mPhotoEditFragment.onOptionsDoneSelected();
                }
                return true;
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void setupToolbar() {
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    /**
     * Show the edit menu
     * @param showClear
     * @param showUndo
     */
    public void showEditMenu(boolean showClear, boolean showUndo) {
        getMenuInflater().inflate(R.menu.activity_photo_edit_edit, mMenu);
        if (!showClear) {
            MenuItem item = mMenu.findItem(R.id.activity_photo_edit_clear);
            if (item != null) {
                item.setVisible(false);
            }
        }
        if (!showUndo) {
            MenuItem item = mMenu.findItem(R.id.activity_photo_edit_undo);
            if (item != null) {
                item.setVisible(false);
            }
        }
        getSupportActionBar().invalidateOptionsMenu();
    }

    @Override
    public Uri getImageUri() {
        return mImageUri;
    }

    /**
     * Save the image to disk
     *
     * @param bitmap
     */
    public void saveBitmap(Bitmap bitmap) {
        PhotoUtil.saveImage(bitmap, new SaveImageCallback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onFailure() {

            }
        });
    }
}
