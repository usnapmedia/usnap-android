package com.samsao.snapzi.photo;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;

import com.samsao.snapzi.R;
import com.samsao.snapzi.photo.tools.Tool;
import com.samsao.snapzi.util.PhotoUtil;
import com.samsao.snapzi.util.SaveImageCallback;
import com.soundcloud.android.crop.Crop;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import icepick.Icepick;
import icepick.Icicle;

public class EditActivity extends ActionBarActivity implements EditFragment.Listener {
    public static final String EXTRA_URI = "com.samsao.snapzi.photo.EditActivity.EXTRA_URI";

    @InjectView(R.id.activity_edit_toolbar)
    public Toolbar mToolbar;

    private EditFragment mEditFragment;

    @Icicle
    public Uri mImageUri;
    @Icicle
    public MenuState mMenuState;
    @Icicle
    public ArrayList<Tool> mTools;
    @Icicle
    public Tool mCurrentTool;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        ButterKnife.inject(this);
        setupToolbar();

        mMenuState = new MenuStateView();
        Intent intent = getIntent();
        if (intent != null) {
            mImageUri = intent.getParcelableExtra(EXTRA_URI);
        }
        // restore saved state
        Icepick.restoreInstanceState(this, savedInstanceState);

        if (savedInstanceState == null) {
            mEditFragment = EditFragment.newInstance();
            getFragmentManager().beginTransaction().replace(R.id.activity_edit_content, mEditFragment).commit();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this, outState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode) {
            case Crop.REQUEST_CROP:
                if (resultCode == Activity.RESULT_OK && null != data) {
                    if (mEditFragment != null) {
                        mEditFragment.refreshImage();
                    }
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        mMenuState.onCreateOptionsMenu(getMenuInflater(), menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        switch (item.getItemId()) {
            case R.id.activity_edit_next:
                if (mEditFragment != null) {
                    mEditFragment.onOptionsNextSelected();
                }
                return true;
            case R.id.activity_edit_clear:
                if (mEditFragment != null) {
                    mEditFragment.onOptionsClearSelected();
                }
                return true;
            case R.id.activity_edit_undo:
                if (mEditFragment != null) {
                    mEditFragment.onOptionsUndoSelected();
                }
                return true;
            case R.id.activity_edit_done:
                if (mEditFragment != null) {
                    mEditFragment.onOptionsDoneSelected();
                }
                return true;
            case android.R.id.home:
                if (mEditFragment != null) {
                    mEditFragment.onOptionsHomeSelected();
                } else {
                    finish();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        if (mEditFragment != null) {
            mEditFragment.onOptionsHomeSelected();
        } else {
            finish();
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
     * Reset menu
     */
    @Override
    public void resetMenu() {
        mMenuState = new MenuStateView();
        getSupportActionBar().invalidateOptionsMenu();
    }

    /**
     * Show the edit menu
     * @param showDone
     * @param showClear
     * @param showUndo
     */
    @Override
    public void showEditMenu(boolean showDone, boolean showClear, boolean showUndo) {
        mMenuState = new MenuStateEdit().setShowDone(showDone).setShowClear(showClear).setShowUndo(showUndo);
        invalidateOptionsMenu();
    }

    @Override
    public Uri getImageUri() {
        return mImageUri;
    }

    public Toolbar getToolbar() {
        return mToolbar;
    }

    public ArrayList<Tool> getTools() {
        return mTools;
    }

    public void setTools(ArrayList<Tool> tools) {
        mTools = tools;
    }

    public Tool getCurrentTool() {
        return mCurrentTool;
    }

    public void setCurrentTool(Tool currentTool) {
        mCurrentTool = currentTool;
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
