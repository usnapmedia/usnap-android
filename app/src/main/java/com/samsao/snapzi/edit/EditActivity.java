package com.samsao.snapzi.edit;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
import icepick.Icepick;
import icepick.Icicle;


public class EditActivity extends AppCompatActivity implements EditFragment.Listener {

    /**
     * Constants
     */
    private final String LOG_TAG = getClass().getSimpleName();
    public static final String EXTRA_EDIT_MODE = "com.samsao.snapzi.edit.EditActivity.EXTRA_EDIT_MODE";
    public static final String EXTRA_MEDIA_PATH = "com.samsao.snapzi.edit.EditActivity.EXTRA_MEDIA_PATH";
    public static final String IMAGE_MODE = "com.samsao.snapzi.edit.EditActivity.IMAGE_MODE";
    public static final String VIDEO_MODE = "com.samsao.snapzi.edit.EditActivity.VIDEO_MODE";

    public static final String EXTRA_CAMPAIGN_ID = "com.samsao.snapzi.edit.EditActivity.EXTRA_CAMPAIGN_ID";

    private EditFragment mEditFragment;

    @Icicle
    public int mCampaignId;
    @Icicle
    public String mEditMode;
    @Icicle
    public MenuState mMenuState;
    @Icicle
    public ArrayList<Tool> mTools;
    @Icicle
    public Tool mCurrentTool;
    @Icicle
    public String mMediaPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        ButterKnife.inject(this);

        mMenuState = new MenuStateView().setActivity(this);
        Intent intent = getIntent();
        if (intent != null) {
            mEditMode = intent.getStringExtra(EXTRA_EDIT_MODE);
            mMediaPath = intent.getStringExtra(EXTRA_MEDIA_PATH);
            mCampaignId = intent.getIntExtra(EXTRA_CAMPAIGN_ID, 0);
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
        } else {
            if (VideoUtil.isVideoPortraitOriented(mMediaPath)) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
            } else {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
            }
        }

        if (savedInstanceState == null) {
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

            mEditFragment = EditFragment.newInstance();
            getFragmentManager().beginTransaction().replace(R.id.activity_edit_content, mEditFragment, EditFragment.FRAGMENT_TAG).commit();
        } else {
            mEditFragment = (EditFragment) getFragmentManager().findFragmentByTag(EditFragment.FRAGMENT_TAG);
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

    /**
     * Reset menu
     */
    @Override
    public void resetMenu() {
        mMenuState = new MenuStateView().setActivity(this);
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
    public void showEditMenu(boolean showDone, boolean showClear, boolean showUndo, boolean showHome) {
        mMenuState = new MenuStateEdit().setShowDone(showDone).setShowClear(showClear).setShowUndo(showUndo).setShowHome(showHome).setActivity(this);
        invalidateOptionsMenu();
    }

    @Override
    public int getCampaignId() {
        return mCampaignId;
    }

    @Override
    public String getEditMode() {
        return mEditMode;
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
