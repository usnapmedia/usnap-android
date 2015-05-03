package com.samsao.snapzi.social;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import com.samsao.snapzi.R;
import com.samsao.snapzi.util.StringUtil;

/**
 * @author jingsilu
 * @since 2015-04-29
 */
public class ShareLoginDialogFragment extends DialogFragment{
    public final static String PROMPT_LOGIN_DIALOG_FRAGMENT_TAG = "com.samsao.snapzi.social.ShareLoginDialogFragment.PROMPT_LOGIN_DIALOG_FRAGMENT_TAG";
    private ShareDialogListener mShareDialogListener;

    public static ShareLoginDialogFragment newInstance(ShareDialogListener listener) {
        ShareLoginDialogFragment shareLoginDialogFragment = new ShareLoginDialogFragment();
        shareLoginDialogFragment.setShareDialogListener(listener);
        return shareLoginDialogFragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setMessage(StringUtil.getAppFontString(R.string.action_prompting_login_text))
                .setPositiveButton(StringUtil.getAppFontString(R.string.action_login), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mShareDialogListener.onLoginButtonClick(ShareLoginDialogFragment.this);
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mShareDialogListener.onCancelButtonClick(ShareLoginDialogFragment.this);
                    }
                });
        return builder.create();
    }

    public void setShareDialogListener(ShareDialogListener listener) {
        mShareDialogListener = listener;
    }

    public interface ShareDialogListener {
        public void onLoginButtonClick(DialogFragment dialog);
        public void onCancelButtonClick(DialogFragment dialog);
    }
}
