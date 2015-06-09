package com.samsao.snapzi.fan_page;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import com.samsao.snapzi.R;

/**
 * @author jfcartier
 * @since 15-06-08
 */
public class ReportImageDialogFragment extends DialogFragment {

    private Listener mListener;

    public static ReportImageDialogFragment newInstance(Listener listener) {
        ReportImageDialogFragment fragment = new ReportImageDialogFragment();
        fragment.setListener(listener);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.confirm_report_image)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (mListener != null) {
                            mListener.onReportImageConfirmation();
                        }
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }

    public void setListener(Listener listener) {
        mListener = listener;
    }

    public interface Listener {
        void onReportImageConfirmation();
    }
}
