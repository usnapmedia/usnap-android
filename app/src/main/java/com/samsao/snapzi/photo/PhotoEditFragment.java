package com.samsao.snapzi.photo;


import android.app.Activity;
import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.larswerkman.holocolorpicker.ColorPicker;
import com.samsao.snapzi.R;
import com.samsao.snapzi.util.KeyboardUtil;
import com.squareup.picasso.Picasso;

import butterknife.ButterKnife;
import butterknife.InjectView;
import jp.wasabeef.picasso.transformations.gpu.BrightnessFilterTransformation;
import jp.wasabeef.picasso.transformations.gpu.ContrastFilterTransformation;
import me.panavtec.drawableview.DrawableView;
import me.panavtec.drawableview.DrawableViewConfig;

/**
 * A simple {@link Fragment} subclass.
 */
public class PhotoEditFragment extends Fragment {

    @InjectView(R.id.fragment_photo_edit_image)
    public ImageView mImage;

    @InjectView(R.id.fragment_photo_edit_container)
    public ViewGroup mContainer;

    @InjectView(R.id.fragment_photo_edit_annotations_container)
    public ViewGroup mAnnotationsContainer;

    @InjectView(R.id.fragment_photo_edit_text_annotation)
    public EditText mTextAnnotation;

    @InjectView(R.id.fragment_photo_edit_draw_annotation)
    public DrawableView mDrawAnnotation;

    private Listener mListener;
    private MaterialDialog mColorPickerDialog;
    private DrawableViewConfig mDrawableViewConfig;
    private ColorPicker mColorPicker;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment PhotoEditFragment.
     */
    public static PhotoEditFragment newInstance() {
        PhotoEditFragment fragment = new PhotoEditFragment();
        return fragment;
    }

    public PhotoEditFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_photo_edit, container, false);
        ButterKnife.inject(this, view);

        // TODO check for keyboard dismiss also
        mTextAnnotation.setTextIsSelectable(false);
        mTextAnnotation.setOnEditorActionListener(
                new EditText.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if (actionId == EditorInfo.IME_ACTION_DONE) {
                            KeyboardUtil.hideKeyboard(getActivity());
                            if (!TextUtils.isEmpty(mTextAnnotation.getText())) {
                                mTextAnnotation.setFocusableInTouchMode(false);
                                mTextAnnotation.clearFocus();
                                mTextAnnotation.setOnTouchListener(new TextAnnotationTouchListener(mTextAnnotation));
                            } else {
                                mTextAnnotation.clearFocus();
                                mTextAnnotation.setOnTouchListener(null);
                            }
                            return true;
                        }
                        return false;
                    }
                });


        // init draw annotation
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        mDrawableViewConfig = new DrawableViewConfig();
        mDrawableViewConfig.setStrokeColor(getResources().getColor(android.R.color.holo_red_light));
        mDrawableViewConfig.setStrokeWidth(20.0f);
        mDrawableViewConfig.setMinZoom(1.0f);
        mDrawableViewConfig.setMaxZoom(3.0f);
        mDrawableViewConfig.setCanvasHeight(size.y);
        mDrawableViewConfig.setCanvasWidth(size.x);
        mDrawAnnotation.setConfig(mDrawableViewConfig);
        mDrawAnnotation.setOnTouchListener(null);

        // set the view background
        Picasso.with(getActivity()).invalidate(mListener.getImageUri());
        Picasso.with(getActivity()).load(mListener.getImageUri())
                .noPlaceholder()
                .into(mImage);
        replaceContainer(getControlsView());
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mListener = (Listener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement PhotoEditFragment.Listener");
        }
    }

    public View getControlsView() {
        View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_photo_edit_controls, mContainer, false);
        // set the touch events listeners
        Button brightnessButton = (Button) view.findViewById(R.id.fragment_photo_edit_controls_brightness_btn);
        brightnessButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceContainer(getBrightnessEditView());
            }
        });
        Button contrastButton = (Button) view.findViewById(R.id.fragment_photo_edit_controls_contrast_btn);
        contrastButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceContainer(getContrastEditView());
            }
        });
        Button textButton = (Button) view.findViewById(R.id.fragment_photo_edit_controls_text_btn);
        textButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceContainer(getAddTextAnnotationView());
                mTextAnnotation.setVisibility(View.VISIBLE);
                mTextAnnotation.setFocusableInTouchMode(true);
                mTextAnnotation.requestFocus();
                KeyboardUtil.showKeyboard(getActivity(), mTextAnnotation);
            }
        });
        Button drawButton = (Button) view.findViewById(R.id.fragment_photo_edit_controls_draw_btn);
        drawButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceContainer(getAddDrawAnnotationView());
                mDrawAnnotation.setOnTouchListener(mDrawAnnotation);
            }
        });
        return view;
    }

    public View getBrightnessEditView() {
        View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_photo_edit_brigthness, mContainer, false);
        // set the touch events listeners
        SeekBar seekBar = (SeekBar) view.findViewById(R.id.fragment_photo_edit_brightness_seekbar);
        seekBar.setMax(20);
        seekBar.setProgress(mListener.getBrightness());
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, final int progress, boolean fromUser) {
                Picasso.with(getActivity()).load(mListener.getImageUri())
                        .noPlaceholder()
                        .transform(new BrightnessFilterTransformation(getActivity(), (progress - 10) / 10.0f))
                        .into(mImage);
                mListener.setBrightness(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        Button doneButton = (Button) view.findViewById(R.id.fragment_photo_edit_brightness_done_btn);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceContainer(getControlsView());
                mListener.saveBitmap(((BitmapDrawable) mImage.getDrawable()).getBitmap());
            }
        });
        return view;
    }


    public View getContrastEditView() {
        View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_photo_edit_brigthness, mContainer, false);
        // set the touch events listeners
        SeekBar seekBar = (SeekBar) view.findViewById(R.id.fragment_photo_edit_brightness_seekbar);
        seekBar.setMax(40);
        seekBar.setProgress(mListener.getContrast());
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, final int progress, boolean fromUser) {
                Picasso.with(getActivity()).load(mListener.getImageUri())
                        .noPlaceholder()
                        .transform(new ContrastFilterTransformation(getActivity(), progress / 10.0f))
                        .into(mImage);
                mListener.setContrast(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        Button doneButton = (Button) view.findViewById(R.id.fragment_photo_edit_brightness_done_btn);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceContainer(getControlsView());
                mListener.saveBitmap(((BitmapDrawable) mImage.getDrawable()).getBitmap());
            }
        });
        return view;
    }

    public View getAddTextAnnotationView() {
        View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_photo_edit_brigthness, mContainer, false);
        // set the touch events listeners
        SeekBar seekBar = (SeekBar) view.findViewById(R.id.fragment_photo_edit_brightness_seekbar);
        seekBar.setVisibility(View.GONE);
        Button doneButton = (Button) view.findViewById(R.id.fragment_photo_edit_brightness_done_btn);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceContainer(getControlsView());
                if (TextUtils.isEmpty(mTextAnnotation.getText())) {
                    mTextAnnotation.setVisibility(View.GONE);
                } else {
                    mTextAnnotation.setFocusableInTouchMode(false);
                    mTextAnnotation.setOnTouchListener(null);
                }
            }
        });
        return view;
    }

    public View getAddDrawAnnotationView() {
        View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_photo_edit_draw, mContainer, false);
        // set the touch events listeners
        Button undoButton = (Button) view.findViewById(R.id.fragment_photo_edit_draw_undo_btn);
        undoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawAnnotation.undo();
            }
        });
        Button clearButton = (Button) view.findViewById(R.id.fragment_photo_edit_draw_clear_btn);
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawAnnotation.clear();
            }
        });
        Button colorButton = (Button) view.findViewById(R.id.fragment_photo_edit_draw_color_btn);
        colorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getColorPickerDialog().show();
            }
        });
        Button doneButton = (Button) view.findViewById(R.id.fragment_photo_edit_draw_done_btn);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceContainer(getControlsView());
                mDrawAnnotation.setOnTouchListener(null);
            }
        });
        return view;
    }

    public void replaceContainer(View view) {
        mContainer.removeAllViews();
        mContainer.addView(view);
    }

    public MaterialDialog getColorPickerDialog() {
        if (mColorPickerDialog == null) {
            mColorPickerDialog = new MaterialDialog.Builder(getActivity())
                    .customView(R.layout.dialog_color_picker, false)
                    .positiveText(android.R.string.ok)
                    .negativeText(android.R.string.cancel)
                    .callback(new MaterialDialog.ButtonCallback() {
                        @Override
                        public void onPositive(MaterialDialog dialog) {
                            mDrawableViewConfig.setStrokeColor(mColorPicker.getColor());
                        }
                    })
                    .build();
            View view = mColorPickerDialog.getCustomView();
            mColorPicker = (ColorPicker) view.findViewById(R.id.picker);
            // TODO set the right start color
            mColorPicker.setOldCenterColor(mColorPicker.getColor());
        }
        return mColorPickerDialog;
    }

    public interface Listener {
        public Uri getImageUri();

        public int getBrightness();

        public void setBrightness(int brightness);

        public int getContrast();

        public void setContrast(int contrast);

        public void saveBitmap(Bitmap bitmap);
    }
}
