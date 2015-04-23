package com.samsao.snapzi.camera;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import com.samsao.snapzi.R;
import com.samsao.snapzi.util.PhotoUtil;

/**
 * @author jingsilu
 * @since 2015-04-23
 */
public class SavingImageProgressDialogFragment extends DialogFragment{
    private Context mContext;
    private SelectMediaFragment mSelectMediaFragment;

    public static SavingImageProgressDialogFragment newInstance(Context context, SelectMediaFragment selectMediaFragment) {
        SavingImageProgressDialogFragment mSavingImageProgressDialogFragment = new SavingImageProgressDialogFragment();
        mSavingImageProgressDialogFragment.setContext(context);
        mSavingImageProgressDialogFragment.setSelectMediaFragment(selectMediaFragment);
        return mSavingImageProgressDialogFragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Dialog dialog = new Dialog(mContext);
        dialog.setTitle(R.string.action_processing_image_title);
        dialog.setContentView(R.layout.dialog_processing_image);
        return dialog;
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        PhotoUtil.cancelSaveImage();
        // Restart camera preview
        if (mSelectMediaFragment != null) {
            mSelectMediaFragment.initializeCamera();
        }
    }

    public void setContext(Context context) {
        mContext = context;
    }

    private void setSelectMediaFragment(SelectMediaFragment selectMediaFragment) {
        mSelectMediaFragment = selectMediaFragment;
    }

    @Override
    public void show(FragmentManager manager, String tag) {
            super.show(manager, tag);
    }
}
