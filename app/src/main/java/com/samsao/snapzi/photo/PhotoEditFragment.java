package com.samsao.snapzi.photo;


import android.app.Activity;
import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.samsao.snapzi.R;
import com.samsao.snapzi.photo.tools.Tool;
import com.samsao.snapzi.photo.tools.ToolDraw;
import com.samsao.snapzi.photo.tools.ToolFilters;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.squareup.picasso.Transformation;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import me.panavtec.drawableview.DrawableView;

/**
 * A simple {@link Fragment} subclass.
 */
public class PhotoEditFragment extends Fragment {

    @InjectView(R.id.fragment_photo_edit_image)
    public ImageView mImage;

    @InjectView(R.id.fragment_cafe_list_recyclerview)
    public RecyclerView mRecyclerView;

    @InjectView(R.id.fragment_photo_edit_text_annotation_container)
    public FrameLayout mTextAnnotationContainer;

    @InjectView(R.id.fragment_photo_edit_draw_annotation_container)
    public DrawableView mDrawAnnotationContainer;

    @InjectView(R.id.fragment_photo_tool_container)
    public FrameLayout mToolContainer;

    private MenuItemAdapter mMenuItemAdapter;
    private LinearLayoutManager mLayoutManager;
    private Listener mListener;

    // TODO move them to activity
    private ArrayList<Tool> mTools;
    private Tool mCurrentTool;

//    private MaterialDialog mColorPickerDialog;
//    private ColorPicker mColorPicker;

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

        // TODO pass the right tools to instanciate
        mTools = new ArrayList<>();
        mTools.add(new ToolFilters().setToolFragment(this));
        // special case for draw tool since we need to get the canvas height and width
        final ToolDraw toolDraw = new ToolDraw();
        toolDraw.setToolFragment(this);
        mTools.add(toolDraw);
        mMenuItemAdapter = new MenuItemAdapter(getMenuItemsForTools());

        mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                outRect.set((int)getResources().getDimension(R.dimen.elements_horizontal_margin), 0, (int)getResources().getDimension(R.dimen.elements_horizontal_margin), 0);
                super.getItemOffsets(outRect, view, parent, state);
            }
        });
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mMenuItemAdapter);

        // load the image
        Picasso.with(getActivity()).load(mListener.getImageUri()).noPlaceholder().into(mImage, new Callback() {
            @Override
            public void onSuccess() {
                Bitmap bitmap = ((BitmapDrawable)mImage.getDrawable()).getBitmap();
                toolDraw.setCanvasHeight(bitmap.getHeight()).setCanvasWidth(bitmap.getWidth());
            }

            @Override
            public void onError() {

            }
        });

        // TODO check for keyboard dismiss also
//        mTextAnnotation.setTextIsSelectable(false);
//        mTextAnnotation.setOnEditorActionListener(
//                new EditText.OnEditorActionListener() {
//                    @Override
//                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//                        if (actionId == EditorInfo.IME_ACTION_DONE) {
//                            KeyboardUtil.hideKeyboard(getActivity());
//                            if (!TextUtils.isEmpty(mTextAnnotation.getText())) {
//                                mTextAnnotation.setFocusableInTouchMode(false);
//                                mTextAnnotation.clearFocus();
//                                mTextAnnotation.setOnTouchListener(new TextAnnotationTouchListener(mTextAnnotation));
//                            } else {
//                                mTextAnnotation.clearFocus();
//                                mTextAnnotation.setOnTouchListener(null);
//                            }
//                            return true;
//                        }
//                        return false;
//                    }
//                });


        mDrawAnnotationContainer.setOnTouchListener(null);
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

//    public View getAddTextAnnotationView() {
//        View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_photo_edit_brigthness, mContainer, false);
//        // set the touch events listeners
//        SeekBar seekBar = (SeekBar) view.findViewById(R.id.fragment_photo_edit_brightness_seekbar);
//        seekBar.setVisibility(View.GONE);
//        Button doneButton = (Button) view.findViewById(R.id.fragment_photo_edit_brightness_done_btn);
//        doneButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                replaceContainer(getControlsView());
//                if (TextUtils.isEmpty(mTextAnnotation.getText())) {
//                    mTextAnnotation.setVisibility(View.GONE);
//                } else {
//                    mTextAnnotation.setFocusableInTouchMode(false);
//                    mTextAnnotation.setOnTouchListener(null);
//                }
//            }
//        });
//        return view;
//    }

//    public View getAddDrawAnnotationView() {
//        View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_photo_edit_draw, mContainer, false);
//        // set the touch events listeners
//        Button undoButton = (Button) view.findViewById(R.id.fragment_photo_edit_draw_undo_btn);
//        undoButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mDrawAnnotation.undo();
//            }
//        });
//        Button clearButton = (Button) view.findViewById(R.id.fragment_photo_edit_draw_clear_btn);
//        clearButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mDrawAnnotation.clear();
//            }
//        });
//        Button colorButton = (Button) view.findViewById(R.id.fragment_photo_edit_draw_color_btn);
//        colorButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                getColorPickerDialog().show();
//            }
//        });
//        Button doneButton = (Button) view.findViewById(R.id.fragment_photo_edit_draw_done_btn);
//        doneButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                replaceContainer(getControlsView());
//                mDrawAnnotation.setOnTouchListener(null);
//            }
//        });
//        return view;
//    }
//

//    public MaterialDialog getColorPickerDialog() {
//        if (mColorPickerDialog == null) {
//            mColorPickerDialog = new MaterialDialog.Builder(getActivity())
//                    .customView(R.layout.dialog_color_picker, false)
//                    .positiveText(android.R.string.ok)
//                    .negativeText(android.R.string.cancel)
//                    .callback(new MaterialDialog.ButtonCallback() {
//                        @Override
//                        public void onPositive(MaterialDialog dialog) {
//                            mDrawableViewConfig.setStrokeColor(mColorPicker.getColor());
//                        }
//                    })
//                    .build();
//            View view = mColorPickerDialog.getCustomView();
//            mColorPicker = (ColorPicker) view.findViewById(R.id.picker);
//            // TODO set the right start color
//            mColorPicker.setOldCenterColor(mColorPicker.getColor());
//        }
//        return mColorPickerDialog;
//    }

    /**
     * Refreshes the image without any transformation
     */
    public void refreshImage() {
        refreshImage(null);
    }

    /**
     * Refreshes the image
     * @param transformation
     */
    public void refreshImage(Transformation transformation) {
        Picasso.with(getActivity()).invalidate(mListener.getImageUri());
        RequestCreator requestCreator = Picasso.with(getActivity()).load(mListener.getImageUri()).noPlaceholder();
        if (transformation != null) {
            requestCreator = requestCreator.transform(transformation);
        }
        requestCreator.into(mImage);
    }

//    public void fitImageToScreen() {
//        if (mImage != null) {
//            int width = ((View) mImage.getParent()).getWidth();
//            int height = ((View) mImage.getParent()).getHeight();
//
//            BitmapDrawable bitmap = (BitmapDrawable) mImage.getDrawable();
//            float bitmapWidth = bitmap.getBitmap().getWidth();
//            float bitmapHeight = bitmap.getBitmap().getHeight();
//
//            float wRatio = width / bitmapWidth;
//            float hRatio = height / bitmapHeight;
//
//            float ratioMultiplier;
//            if (hRatio < wRatio) {
//                ratioMultiplier = hRatio;
//            } else {
//                ratioMultiplier = wRatio;
//            }
//
//            int newBitmapWidth = (int) (bitmapWidth * ratioMultiplier);
//            int newBitmapHeight = (int) (bitmapHeight * ratioMultiplier);
//
//            mImage.setLayoutParams(new FrameLayout.LayoutParams(newBitmapWidth, newBitmapHeight));
//        }
//    }

    public void setMenuItems(ArrayList<MenuItem> items) {
        mMenuItemAdapter.setData(items);
    }

    /**
     * Reset menu to the initial state
     */
    public void resetMenu() {
        mMenuItemAdapter.setData(getMenuItemsForTools());
        mToolContainer.setVisibility(View.GONE);
    }

    /**
     * Get the menu items associated with current tools
     * @return
     */
    private ArrayList<MenuItem> getMenuItemsForTools() {
        ArrayList<MenuItem> menuItems = new ArrayList<>();
        for (Tool tool : mTools) {
            menuItems.add(tool.getMenuItem());
        }
        return menuItems;
    }

    /**
     * Replaces the tool container view
     * @param resId
     */
    public View replaceToolContainer(int resId) {
        mToolContainer.removeAllViews();
        View view = getActivity().getLayoutInflater().inflate(resId, mToolContainer, true);
        mToolContainer.setVisibility(View.VISIBLE);
        return view;
    }

    public void setCurrentTool(Tool currentTool, boolean enableClear, boolean enableUndo) throws UnsupportedOperationException {
        if (currentTool == null) {
            throw new UnsupportedOperationException("Use resetCurrentTool to remove the current tool");
        }
        mCurrentTool.unselect();
        mCurrentTool = currentTool;
        mListener.showEditMenu(enableClear, enableUndo);
    }

    /**
     * Reset current tool
     */
    public void resetCurrentTool() {
        mCurrentTool = null;
        resetMenu();
        mListener.resetMenu();
    }

    /**
     * Returns the DrawAnnotationContainer
     * @return
     */
    public DrawableView getDrawAnnotationContainer() {
        return mDrawAnnotationContainer;
    }

    /**
     * When options item NEXT is selected
     */
    public void onOptionsNextSelected() {
        // TODO
        Toast.makeText(getActivity(), "TODO: go to share activity", Toast.LENGTH_SHORT).show();
    }

    /**
     * When options item DONE is selected
     */
    public void onOptionsDoneSelected() {
        resetCurrentTool();
    }

    /**
     * When options item CLEAR is selected
     */
    public void onOptionsClearSelected() {
        if (mCurrentTool != null) {
            mCurrentTool.onOptionsClearSelected();
        }
    }

    /**
     * When options item UNDO is selected
     */
    public void onOptionsUndoSelected() {
        if (mCurrentTool != null) {
            mCurrentTool.onOptionsUndoSelected();
        }
    }

    public interface Listener {
        Uri getImageUri();
        void saveBitmap(Bitmap bitmap);
        void resetMenu();
        void showEditMenu(boolean showClear, boolean showUndo);
    }
}
