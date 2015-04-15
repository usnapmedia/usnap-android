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
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.samsao.snapzi.R;
import com.samsao.snapzi.camera.CameraHelper;
import com.samsao.snapzi.photo.tools.Tool;
import com.samsao.snapzi.photo.tools.ToolCrop;
import com.samsao.snapzi.photo.tools.ToolDraw;
import com.samsao.snapzi.photo.tools.ToolFilters;
import com.samsao.snapzi.photo.tools.ToolText;
import com.samsao.snapzi.photo.util.TextAnnotationEditText;
import com.samsao.snapzi.video.VideoPreview;
import com.soundcloud.android.crop.Crop;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.squareup.picasso.Transformation;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.Optional;
import me.panavtec.drawableview.DrawableView;

/**
 * A simple {@link Fragment} subclass.
 */
public class EditFragment extends Fragment {

    private final int ANIMATION_DURATION = 300;

    @InjectView(R.id.fragment_edit_image_container)
    public ImageView mImageContainer;

    @InjectView(R.id.fragment_edit_video_container)
    public FrameLayout mVideoContainer;
    private VideoPreview mVideoPreview;

    @InjectView(R.id.fragment_cafe_list_recyclerview)
    public RecyclerView mRecyclerView;

    @InjectView(R.id.fragment_edit_text_annotation_container)
    public FrameLayout mTextAnnotationContainer;

    @InjectView(R.id.fragment_edit_draw_annotation_container)
    public DrawableView mDrawAnnotationContainer;

    @InjectView(R.id.fragment_tool_container)
    public FrameLayout mToolContainer;

    @InjectView(R.id.fragment_edit_text_annotation_container_text)
    @Optional
    public TextAnnotationEditText mTextAnnotation;

    private MenuItemAdapter mMenuItemAdapter;
    private LinearLayoutManager mLayoutManager;
    private Listener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment EditFragment.
     */
    public static EditFragment newInstance() {
        EditFragment fragment = new EditFragment();
        return fragment;
    }

    public EditFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit, container, false);
        ButterKnife.inject(this, view);

        // TODO read the value from gradle to know what tools to instantiate
        ArrayList<Tool> tools = new ArrayList<>();

        // Add common tools for both modes
        tools.add(new ToolText().setToolFragment(this));
        tools.add(new ToolDraw().setToolFragment(this));

        if (mListener.isEditPictureMode()) {
            // Add specific tool for edit image mode
            tools.add(new ToolCrop().setToolFragment(this));
            tools.add(new ToolFilters().setToolFragment(this));

            // load the image
            Picasso.with(getActivity()).invalidate(mListener.getImageUri()); // clear cache to force refresh
            Picasso.with(getActivity())
                    .load(mListener.getImageUri())
                    .noPlaceholder()
                    .into(mImageContainer);
            mImageContainer.setVisibility(View.VISIBLE);
        }
        mListener.setTools(tools);

        mMenuItemAdapter = new MenuItemAdapter(getMenuItemsForTools());
        mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                if (parent.getChildAdapterPosition(view) != 0) {
                    outRect.left = (int) getResources().getDimension(R.dimen.elements_horizontal_margin);
                } else {
                    super.getItemOffsets(outRect, view, parent, state);
                }
            }
        });
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mMenuItemAdapter);

        // disable the touch listener on the draw view so it does not take draw events
        mDrawAnnotationContainer.setOnTouchListener(null);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
        for (Tool tool : mListener.getTools()) {
            tool.destroy();
        }
        mListener.getTools().clear();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mListener = (Listener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement EditFragment.Listener");
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (!mListener.isEditPictureMode()) {
            // load the video
            if (mVideoPreview == null) {
                mVideoPreview = new VideoPreview(getActivity(), mListener.getVideoPath());
            }
            mVideoContainer.setVisibility(View.VISIBLE);
            mVideoContainer.addView(mVideoPreview);

        }
    }

    @Override
    public void onPause() {
        super.onPause();

        if (!mListener.isEditPictureMode() && mVideoPreview != null) {
            mVideoContainer.removeView(mVideoPreview);
            mVideoPreview = null;
        }
    }

    /**
     * Refreshes the image without any transformation
     */
    public void refreshImage() {
        refreshImage(null);
    }

    /**
     * Refreshes the image
     *
     * @param transformation
     */
    public void refreshImage(Transformation transformation) {
        Picasso.with(getActivity()).invalidate(mListener.getImageUri());
        RequestCreator requestCreator = Picasso.with(getActivity()).load(mListener.getImageUri()).noPlaceholder();
        if (transformation != null) {
            requestCreator = requestCreator.transform(transformation);
        }
        requestCreator.into(mImageContainer);
    }

    public void setMenuItems(ArrayList<MenuItem> items) {
        mMenuItemAdapter.setData(items);
    }

    /**
     * Reset menu to the initial state
     */
    public void resetMenu() {
        mMenuItemAdapter.setData(getMenuItemsForTools());
    }

    /**
     * Get the menu items associated with current tools
     *
     * @return
     */
    private ArrayList<MenuItem> getMenuItemsForTools() {
        ArrayList<MenuItem> menuItems = new ArrayList<>();
        for (Tool tool : mListener.getTools()) {
            menuItems.add(tool.getMenuItem());
        }
        return menuItems;
    }

    /**
     * Replaces the tool container view
     *
     * @param resId
     */
    public View showToolContainer(int resId) {
        mToolContainer.removeAllViews();
        View view = getActivity().getLayoutInflater().inflate(resId, mToolContainer, true);
        mToolContainer.setVisibility(View.VISIBLE);
        return view;
    }

    /**
     * Hide tool container
     */
    public void hideToolContainer() {
        mToolContainer.removeAllViews();
        mToolContainer.setVisibility(View.GONE);
    }

    /**
     * Set the current tool
     *
     * @param currentTool
     * @throws UnsupportedOperationException
     */
    public void setCurrentTool(Tool currentTool) throws UnsupportedOperationException {
        if (currentTool == null) {
            throw new UnsupportedOperationException("Use resetCurrentTool to remove the current tool");
        }
        mListener.setCurrentTool(currentTool);
    }

    /**
     * This method shows the edit options menu
     *
     * @param showDone
     * @param showClear
     * @param showUndo
     */
    public void showEditOptionsMenu(boolean showDone, boolean showClear, boolean showUndo) {
        mListener.showEditMenu(showDone, showClear, showUndo);
    }

    /**
     * Reset current tool
     */
    public void resetCurrentTool() {
        Tool currentTool = mListener.getCurrentTool();
        if (currentTool != null) {
            currentTool.unselect();
        }
        mListener.setCurrentTool(null);
        resetMenu();
    }

    /**
     * This method resets the options menu
     */
    public void resetOptionsMenu() {
        mListener.resetMenu();
    }

    /**
     * Returns the text annotation EditText
     *
     * @return
     */
    public EditText getTextAnnotation() {
        return mTextAnnotation;
    }

    /**
     * Returns the DrawAnnotationContainer
     *
     * @return
     */
    public DrawableView getDrawAnnotationContainer() {
        return mDrawAnnotationContainer;
    }

    /**
     * Disable touch event on text annotation container
     */
    public void disableTextAnnotationContainerTouchEvent() {
        mTextAnnotationContainer.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });

        for (int i = 0; i < mTextAnnotationContainer.getChildCount(); ++i) {
            mTextAnnotationContainer.getChildAt(i).setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return false;
                }
            });
        }
    }

    /**
     * Enable touch event on text annotation container
     */
    public void enableTextAnnotationContainerTouchEvent() {
        mTextAnnotationContainer.setOnTouchListener(null);
        for (int i = 0; i < mTextAnnotationContainer.getChildCount(); ++i) {
            mTextAnnotationContainer.getChildAt(i).setOnTouchListener(null);
        }
    }

    /**
     * Get the text annotation container
     *
     * @return
     */
    public FrameLayout getTextAnnotationContainer() {
        return mTextAnnotationContainer;
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
        if (mListener.getCurrentTool() != null) {
            mListener.getCurrentTool().onOptionsDoneSelected();
        }
    }

    /**
     * When options item CLEAR is selected
     */
    public void onOptionsClearSelected() {
        if (mListener.getCurrentTool() != null) {
            mListener.getCurrentTool().onOptionsClearSelected();
        }
    }

    /**
     * When options item UNDO is selected
     */
    public void onOptionsUndoSelected() {
        if (mListener.getCurrentTool() != null) {
            mListener.getCurrentTool().onOptionsUndoSelected();
        }
    }

    /**
     * When options item HOME is selected
     */
    public void onOptionsHomeSelected() {
        if (mListener.getCurrentTool() != null) {
            mListener.getCurrentTool().onOptionsHomeSelected();
        } else {
            getActivity().finish();
        }
    }

    /**
     * Save the current image
     */
    public void saveImage() {
        mListener.saveBitmap(((BitmapDrawable) mImageContainer.getDrawable()).getBitmap());
    }

    /**
     * Notify the menu item adapter that data set changed
     */
    public void notifyMenuItemAdapterDataSetChanged() {
        mMenuItemAdapter.notifyDataSetChanged();
    }

    /**
     * Start cropping activity
     */
    public void startCropActivity() {
        new Crop(CameraHelper.getImageUri())
                .output(CameraHelper.getImageUri())
                .start(getActivity());
    }

    /**
     * Hide the tools menu
     */
    public void hideMenu() {
        float transY = mRecyclerView.getMeasuredHeight();
        if (mRecyclerView.getTranslationY() != 0) {
            mRecyclerView.animate().cancel();
            transY = -mRecyclerView.getTranslationY();
        }
        mRecyclerView.animate().translationYBy(transY)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .setDuration(ANIMATION_DURATION);
    }

    /**
     * Hide the toolbar
     */
    public void hideToolbar() {
        Toolbar toolbar = mListener.getToolbar();
        float transY = -toolbar.getMeasuredHeight();
        if (toolbar.getTranslationY() != 0) {
            toolbar.animate().cancel();
            transY = -toolbar.getTranslationY();
        }
        toolbar.animate().translationYBy(transY)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .setDuration(ANIMATION_DURATION);
    }

    /**
     * Show the tools menu
     */
    public void showMenu() {
        mRecyclerView.animate().cancel();
        mRecyclerView.animate().translationYBy(-mRecyclerView.getTranslationY()).setDuration(ANIMATION_DURATION);
    }

    /**
     * Show the toolbar
     */
    public void showToolbar() {
        Toolbar toolbar = mListener.getToolbar();
        toolbar.animate().cancel();
        toolbar.animate().translationYBy(-toolbar.getTranslationY()).setDuration(ANIMATION_DURATION);
    }

    /**
     * Hide all the overlays, that is the menu and the toolbar
     */
    public void hideOverlays() {
        hideMenu();
        hideToolbar();
    }

    /**
     * Show all the overlays, that is the menu and the toolbar
     */
    public void showOverlays() {
        showMenu();
        showToolbar();
    }

    public interface Listener {
        boolean isEditPictureMode();

        Uri getImageUri();

        void saveBitmap(Bitmap bitmap);

        void resetMenu();

        void showEditMenu(boolean showDone, boolean showClear, boolean showUndo);

        Toolbar getToolbar();

        ArrayList<Tool> getTools();

        void setTools(ArrayList<Tool> tools);

        Tool getCurrentTool();

        void setCurrentTool(Tool currentTool);

        String getVideoPath();
    }
}