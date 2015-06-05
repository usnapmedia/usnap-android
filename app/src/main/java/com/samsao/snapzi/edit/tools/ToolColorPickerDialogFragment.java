package com.samsao.snapzi.edit.tools;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.larswerkman.holocolorpicker.ColorPicker;
import com.samsao.snapzi.R;
import com.samsao.snapzi.util.StringUtil;

/**
 * @author jingsilu
 * @since 2015-05-12
 */
public class ToolColorPickerDialogFragment extends DialogFragment {
    public static final String ToolColorPickerDialogFragment_TAG = "com.samsao.snapzi.edit.tools.ToolColorPickerDialogFragment_TAG";
    private Listener mListener;
    private ColorPicker mColorPicker;
    private int mColor;
    private ToolOptionColorPicker.ToolCallback mToolCallback;

    public static ToolColorPickerDialogFragment newInstance(Listener listener) {
        ToolColorPickerDialogFragment mToolColorPickerDialogFragment = new ToolColorPickerDialogFragment();
        mToolColorPickerDialogFragment.setListener(listener);
        return mToolColorPickerDialogFragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View view = inflater.inflate(R.layout.dialog_color_picker, null);
        mColorPicker = (ColorPicker) view.findViewById(R.id.picker);
        mColorPicker.setOldCenterColor(mColor);
        builder.setView(view);
        builder.setPositiveButton(StringUtil.getAppFontString(android.R.string.ok), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                mColor = mColorPicker.getColor();
                mToolCallback = mListener.getToolCallback();
                mToolCallback.onColorSelected(mColor);
            }
        });
        builder.setNegativeButton(StringUtil.getAppFontString(android.R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        return builder.create();
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        super.show(manager, tag);
    }

    public void setListener(Listener listener) {
        mListener = listener;
    }

    public interface Listener {
        ToolOptionColorPicker.ToolCallback getToolCallback();
        ColorPicker getColorPicker();
        void setColor(int color);
    }
}
