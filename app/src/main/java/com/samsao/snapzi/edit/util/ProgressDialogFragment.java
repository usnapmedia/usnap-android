package com.samsao.snapzi.edit.util;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.LightingColorFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.samsao.snapzi.R;
import com.samsao.snapzi.SnapziApplication;

/**
 * @author jingsilu
 * @since 2015-04-23
 */
public class ProgressDialogFragment extends DialogFragment {
    public final static String PROGRESS_DIALOG_FRAGMENT_TAG = "com.samsao.snapzi.edit.util.ProgressDialogFragment.PROGRESS_DIALOG_FRAGMENT_TAG";
    private String mText;
    private Listener mListener;

    public static ProgressDialogFragment newInstance(Listener listener) {
        ProgressDialogFragment progressDialogFragment = new ProgressDialogFragment();
        progressDialogFragment.setListener(listener);
        progressDialogFragment.setText(SnapziApplication.getContext().getString(R.string.loading));
        return progressDialogFragment;
    }

    public static ProgressDialogFragment newInstance(Listener listener, String text) {
        ProgressDialogFragment progressDialogFragment = new ProgressDialogFragment();
        progressDialogFragment.setListener(listener);
        progressDialogFragment.setText(text);
        return progressDialogFragment;
    }

    public static ProgressDialogFragment newInstance(Listener listener, int resId) {
        ProgressDialogFragment progressDialogFragment = new ProgressDialogFragment();
        progressDialogFragment.setListener(listener);
        progressDialogFragment.setText(SnapziApplication.getContext().getString(resId));
        return progressDialogFragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View view = inflater.inflate(R.layout.dialog_fragment_progress, null);
        TextView textView = (TextView) view.findViewById(R.id.dialog_fragment_progress_text);
        ((ProgressBar) view.findViewById(R.id.dialog_fragment_progress_progress)).getIndeterminateDrawable().setColorFilter(new LightingColorFilter(0xFF000000, getResources().getColor(R.color.primary)));
        if (!TextUtils.isEmpty(mText)) {
            textView.setText(mText);
        }
        builder.setView(view);
        return builder.create();
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        mListener.onProgressDialogCancel();
    }

    public void setListener(Listener listener) {
        mListener = listener;
    }

    public void setText(String text) {
        mText = text;
    }

    public interface Listener {
        void onProgressDialogCancel();
    }
}
