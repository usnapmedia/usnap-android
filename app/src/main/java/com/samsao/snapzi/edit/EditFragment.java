package com.samsao.snapzi.edit;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
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
import android.widget.LinearLayout;

import com.samsao.snapzi.R;
import com.samsao.snapzi.api.ApiService;
import com.samsao.snapzi.api.entity.FeedImageList;
import com.samsao.snapzi.edit.tools.Tool;
import com.samsao.snapzi.edit.util.TextAnnotationEditText;
import com.samsao.snapzi.fan_page.FanPageActivity;
import com.samsao.snapzi.live_feed.LiveFeedAdapter;
import com.samsao.snapzi.social.ShareActivity;
import com.soundcloud.android.crop.Crop;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.squareup.picasso.Transformation;

import java.io.File;
import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import me.panavtec.drawableview.DrawableView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;


public class EditFragment extends Fragment implements LiveFeedAdapter.Listener {

    public static final String FRAGMENT_TAG = "com.samsao.snapzi.edit.EditFragment";
    private final int ANIMATION_DURATION = 300;

    @InjectView(R.id.fragment_edit_toolbar_livefeed_container)
    public LinearLayout mToolBarAndLiveFeedContainer;

    @InjectView(R.id.fragment_edit_image_container)
    public ImageView mImageContainer;

    @InjectView(R.id.fragment_edit_video_container)
    public FrameLayout mVideoContainer;
    private VideoPreview mVideoPreview;

    @InjectView(R.id.fragment_edit_tools_menu_recyclerview)
    public RecyclerView mMenuRecyclerView;

    @InjectView(R.id.fragment_edit_text_annotation_container)
    public FrameLayout mTextAnnotationContainer;

    @InjectView(R.id.fragment_edit_draw_annotation_container)
    public DrawableView mDrawAnnotationContainer;

    @InjectView(R.id.fragment_edit_tool_container)
    public FrameLayout mToolContainer;

    @InjectView(R.id.fragment_edit_text_annotation_container_text)
    public TextAnnotationEditText mTextAnnotation;

    @InjectView(R.id.fragment_edit_toolbar)
    public Toolbar mToolbar;

    @InjectView(R.id.fragment_edit_livefeed_recycler_view)
    public RecyclerView mLiveFeedRecyclerView;

    private LiveFeedAdapter mLiveFeedAdapter;
    private MenuItemAdapter mMenuItemAdapter;
    private LinearLayoutManager mMenuLayoutManager;
    private LinearLayoutManager mLiveFeedLayoutManager;
    private Listener mListener;

    /**
     * TODO inject me
     */
    private ApiService mApiService = new ApiService();

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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLiveFeedAdapter = new LiveFeedAdapter(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit, container, false);
        ButterKnife.inject(this, view);

        setupToolbar();

        // load the image
        if (mListener.getEditMode().equals(EditActivity.IMAGE_MODE)) {
            Uri imageUri = Uri.fromFile(new File(mListener.getMediaPath()));
            Picasso.with(getActivity()).load(imageUri)
                    .noPlaceholder()
                    .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                    .into(mImageContainer);
            mImageContainer.setVisibility(View.VISIBLE);
        }

        // set the fragment for tools
        for (Tool tool : mListener.getTools()) {
            tool.setToolFragment(this);
        }

        // initializes menus
        initToolsMenu();
        initLiveFeed();

        // disable the touch listener on annotations layers
        disableTextAnnotationContainerTouchEvent();
        mDrawAnnotationContainer.setOnTouchListener(null);

        // select the current tool if there's one
        if (mListener.getCurrentTool() != null) {
            // current tool has to be selected if restoring from a saved instance
            mListener.getCurrentTool().select(true); // force selection
        }
        return view;
    }

    /**
     * This method initializes the tools menu
     */
    public void initToolsMenu() {
        mMenuItemAdapter = new MenuItemAdapter(getMenuItemsForTools());
        mMenuLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        mMenuRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                if (parent.getChildAdapterPosition(view) != 0) {
                    outRect.left = (int) getResources().getDimension(R.dimen.elements_horizontal_margin);
                } else {
                    super.getItemOffsets(outRect, view, parent, state);
                }
            }
        });
        mMenuRecyclerView.setHasFixedSize(true);
        mMenuRecyclerView.setLayoutManager(mMenuLayoutManager);
        mMenuRecyclerView.setAdapter(mMenuItemAdapter);
    }

    /**
     * This method initializes the live feed
     */
    public void initLiveFeed() {
        mLiveFeedRecyclerView.setHasFixedSize(true);
        mLiveFeedLayoutManager = new LinearLayoutManager(getActivity());
        mLiveFeedLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mLiveFeedRecyclerView.setLayoutManager(mLiveFeedLayoutManager);
        mLiveFeedRecyclerView.setAdapter(mLiveFeedAdapter);
    }

    public void getFeedImage() {
        mApiService.getLiveFeed(new Callback<FeedImageList>() {
            @Override
            public void success(FeedImageList feedImageList, Response response) {
                mLiveFeedAdapter.setImageLiveFeed(feedImageList.getResponse());
            }

            @Override
            public void failure(RetrofitError error) {
                Timber.e("Error Fetching Images!");
            }
        });
    }

    /**
     * Setup the toolbar
     */
    public void setupToolbar() {
        if (mToolbar != null) {
            ((ActionBarActivity) getActivity()).setSupportActionBar(mToolbar);
        }
        ((ActionBarActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((ActionBarActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
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
                    + " must implement EditFragment.Listener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mListener.getEditMode().equals(EditActivity.VIDEO_MODE)) {
            // load the video
            if (mVideoPreview == null) {
                mVideoPreview = new VideoPreview(getActivity(), mListener.getMediaPath());
            }
            mVideoContainer.setVisibility(View.VISIBLE);
            mVideoContainer.addView(mVideoPreview);
        }
        getFeedImage();
    }

    @Override
    public void onPause() {
        super.onPause();

        if (mListener.getEditMode().equals(EditActivity.VIDEO_MODE)) {
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
        Uri imageUri = Uri.fromFile(new File(mListener.getMediaPath()));
        RequestCreator requestCreator = Picasso.with(getActivity())
                .load(imageUri)
                .noPlaceholder();
        if (transformation != null) {
            requestCreator = requestCreator.transform(transformation);
        }
        requestCreator
                .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                .into(mImageContainer);
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
        mTextAnnotation.setFocusableInTouchMode(false);
        mTextAnnotation.clearFocus();
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
        Uri imageUri = Uri.fromFile(new File(mListener.getMediaPath()));
        if (imageUri != null) {
            Intent intent = new Intent(getActivity(), ShareActivity.class);
            intent.putExtra(ShareActivity.EXTRA_URI, imageUri);
            startActivity(intent);
        } else {
            Timber.e("image uri is null");
        }
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
        Uri imageUri = Uri.fromFile(new File(mListener.getMediaPath()));
        new Crop(imageUri)
                .output(imageUri)
                .withAspect(mImageContainer.getWidth(), mImageContainer.getHeight())
                .start(getActivity());
    }

    /**
     * Hide the tools menu
     */
    public void hideMenu() {
        float transY = mMenuRecyclerView.getMeasuredHeight();
        if (mMenuRecyclerView.getTranslationY() != 0) {
            mMenuRecyclerView.animate().cancel();
            transY = -mMenuRecyclerView.getTranslationY();
        }
        mMenuRecyclerView.animate().translationYBy(transY)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .setDuration(ANIMATION_DURATION);
    }

    /**
     * Hide the toolbar and live feed
     */
    public void hideToolbarAndLiveFeed() {
        float transY = -mToolBarAndLiveFeedContainer.getMeasuredHeight();
        if (mToolBarAndLiveFeedContainer.getTranslationY() != 0) {
            mToolBarAndLiveFeedContainer.animate().cancel();
            transY = -mToolBarAndLiveFeedContainer.getTranslationY();
        }
        mToolBarAndLiveFeedContainer.animate().translationYBy(transY)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .setDuration(ANIMATION_DURATION);
    }

    /**
     * Show the tools menu
     */
    public void showMenu() {
        mMenuRecyclerView.animate().cancel();
        mMenuRecyclerView.animate().translationYBy(-mMenuRecyclerView.getTranslationY()).setDuration(ANIMATION_DURATION);
    }

    /**
     * Show the toolbar and live feed
     */
    public void showToolbarAndLiveFeed() {
        mToolBarAndLiveFeedContainer.animate().cancel();
        mToolBarAndLiveFeedContainer.animate().translationYBy(-mToolBarAndLiveFeedContainer.getTranslationY()).setDuration(ANIMATION_DURATION);
    }

    /**
     * Hide all the overlays, that is the menu and the toolbar
     */
    public void hideOverlays() {
        hideMenu();
        hideToolbarAndLiveFeed();
    }

    /**
     * Show all the overlays, that is the menu and the toolbar
     */
    public void showOverlays() {
        showMenu();
        showToolbarAndLiveFeed();
    }

    @Override
    public void onItemClick(View view, int position) {
        Intent intent = new Intent(getActivity(), FanPageActivity.class);
        startActivity(intent);
    }

    public interface Listener {
        String getEditMode();

        void saveBitmap(Bitmap bitmap);

        void resetMenu();

        void showEditMenu(boolean showDone, boolean showClear, boolean showUndo);

        ArrayList<Tool> getTools();

        void setTools(ArrayList<Tool> tools);

        Tool getCurrentTool();

        void setCurrentTool(Tool currentTool);

        String getMediaPath();
    }
}
