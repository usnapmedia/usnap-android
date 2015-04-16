package com.samsao.snapzi.edit;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.widget.Toast;

import com.samsao.snapzi.R;
import com.samsao.snapzi.edit.tools.Tool;
import com.samsao.snapzi.edit.tools.ToolCrop;
import com.samsao.snapzi.edit.tools.ToolDraw;
import com.samsao.snapzi.edit.tools.ToolFilters;
import com.samsao.snapzi.edit.tools.ToolText;
import com.samsao.snapzi.util.PhotoUtil;
import com.samsao.snapzi.util.SaveImageCallback;
import com.samsao.snapzi.util.VideoUtil;
import com.soundcloud.android.crop.Crop;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import icepick.Icepick;
import icepick.Icicle;


public class EditActivity extends ActionBarActivity implements EditFragment.Listener {

    /**
     * Constants
     */
    private final String LOG_TAG = getClass().getSimpleName();
    public static final String EXTRA_EDIT_MODE = "EditActivity.EXTRA_EDIT_MODE";
    public static final String EXTRA_MEDIA_PATH = "EditActivity.EXTRA_MEDIA_PATH";
    public static final String IMAGE_MODE = "EditActivity.IMAGE_MODE";
    public static final String VIDEO_MODE = "EditActivity.VIDEO_MODE";


    @InjectView(R.id.activity_edit_toolbar)
    public Toolbar mToolbar;

    private EditFragment mEditFragment;

    @Icicle
    public String mEditMode;
    @Icicle
    public MenuState mMenuState;
    @Icicle
    public ArrayList<Tool> mTools;
    @Icicle
    public Tool mCurrentTool;
    @Icicle
    private String mMediaPath;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        ButterKnife.inject(this);
        setupToolbar();

        mMenuState = new MenuStateView();
        Intent intent = getIntent();
        if (intent != null) {
            mEditMode = intent.getStringExtra(EXTRA_EDIT_MODE);
            mMediaPath = intent.getStringExtra(EXTRA_MEDIA_PATH);
        }

        // restore saved state
        if (savedInstanceState != null) {
            Icepick.restoreInstanceState(this, savedInstanceState);
        }

        if (mEditMode == null || !(mEditMode.equals(IMAGE_MODE) || mEditMode.equals(VIDEO_MODE))) {
            Log.e(LOG_TAG, "Unrecognized edit mode was provided, closing EditActivity");
            Toast.makeText(this,
                    getResources().getString(R.string.error_unknown),
                    Toast.LENGTH_LONG).show();
            finish();
        }

        if (mEditMode.equals(IMAGE_MODE)) {
            // Lock screen in image orientation
            if (PhotoUtil.isImagePortraitOriented(mMediaPath)) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
            } else {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
            }
        }

        if (savedInstanceState == null) {
            mEditFragment = EditFragment.newInstance();
            getFragmentManager().beginTransaction().replace(R.id.activity_edit_content, mEditFragment).commit();

            // initialize tools
            // TODO put the available tools in a config file that can change
            // depending the product flavor
            mTools = new ArrayList<>();

            // Add common tools for both modes
            mTools.add(new ToolText());
            mTools.add(new ToolDraw());

            // TODO have a tools list for picture and one for video
            if (getEditMode().equals(EditActivity.IMAGE_MODE)) {
                // Add specific tool for edit image mode
                mTools.add(new ToolCrop());
                mTools.add(new ToolFilters());
            }
        } else {
            if (mCurrentTool != null) {
                // current tool has to be selected if restoring from a saved instance
                mCurrentTool.select();
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this, outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        for (Tool tool : mTools) {
            tool.destroy();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
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
     *
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
    public String getEditMode() {
        return mEditMode;
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

    @Override
    public String getMediaPath() {
        return mMediaPath;
    }

    /**
     * Save the image to disk
     *
     * @param bitmap
     */
    public void saveBitmap(Bitmap bitmap) {
        PhotoUtil.saveImage(bitmap, mMediaPath, new SaveImageCallback() {
            @Override
            public void onSuccess(String destFilePath) {

            }

            @Override
            public void onFailure() {

            }
        });
    }
}
