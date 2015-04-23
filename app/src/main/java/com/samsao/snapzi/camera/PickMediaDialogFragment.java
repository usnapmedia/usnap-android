package com.samsao.snapzi.camera;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.samsao.snapzi.R;

/**
 * @author jingsilu
 * @since 2015-04-22
 */
public class PickMediaDialogFragment extends DialogFragment {
    private Context mContext;
    private PickMediaDialogListener mPickMediaDialogListener;
    private Button mPickImageButton;
    private Button mPickVideoButton;

    public static PickMediaDialogFragment newInstance(PickMediaDialogListener listener, Context context) {
        PickMediaDialogFragment mPickMediaDialogFragment = new PickMediaDialogFragment();
        mPickMediaDialogFragment.setPickMediaDialogListener(listener);
        mPickMediaDialogFragment.setContext(context);
        return mPickMediaDialogFragment;
    }

    //android.R.style.AppTheme.DialogTheme
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Dialog dialog = new Dialog(mContext);

        dialog.setTitle(R.string.action_select_media_type_title);
        dialog.setContentView(R.layout.dialog_select_media_type);

        mPickImageButton = (Button) dialog.findViewById(R.id.dialog_select_media_type_pick_image);
        mPickImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPickMediaDialogListener.onPickImageClick(PickMediaDialogFragment.this);
            }
        });

        mPickVideoButton = (Button) dialog.findViewById(R.id.dialog_select_media_type_pick_video);
        mPickVideoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPickMediaDialogListener.onPickVideoClick(PickMediaDialogFragment.this);
            }
        });

        return dialog;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
    }

    public void setPickMediaDialogListener(PickMediaDialogListener mListener) {
        mPickMediaDialogListener = mListener;
    }

    public void setContext(Context context) {
        mContext = context;
    }

    public interface PickMediaDialogListener {
        public void onPickImageClick(DialogFragment dialog);
        public void onPickVideoClick(DialogFragment dialog);
    }
}
