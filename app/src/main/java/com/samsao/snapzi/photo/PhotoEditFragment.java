package com.samsao.snapzi.photo;


import android.app.Activity;
import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;

import com.samsao.snapzi.R;
import com.squareup.picasso.Picasso;

import butterknife.ButterKnife;
import butterknife.InjectView;
import jp.wasabeef.picasso.transformations.gpu.BrightnessFilterTransformation;
import jp.wasabeef.picasso.transformations.gpu.ContrastFilterTransformation;

/**
 * A simple {@link Fragment} subclass.
 */
public class PhotoEditFragment extends Fragment {

    @InjectView(R.id.fragment_photo_edit_image)
    public ImageView mImage;

    @InjectView(R.id.fragment_photo_edit_container)
    public ViewGroup mContainer;

    private Listener mListener;

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

    public void replaceContainer(View view) {
        mContainer.removeAllViews();
        mContainer.addView(view);
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
