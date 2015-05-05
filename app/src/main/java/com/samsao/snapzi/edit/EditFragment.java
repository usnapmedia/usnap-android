package com.samsao.snapzi.edit;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.samsao.snapzi.R;
import com.samsao.snapzi.api.ApiService;
import com.samsao.snapzi.api.entity.FeedImageList;
import com.samsao.snapzi.camera.CameraHelper;
import com.samsao.snapzi.edit.tools.Tool;
import com.samsao.snapzi.edit.tools.ToolDraw;
import com.samsao.snapzi.edit.util.TextAnnotationEditText;
import com.samsao.snapzi.live_feed.LiveFeedAdapter;
import com.samsao.snapzi.social.ShareActivity;
import com.samsao.snapzi.util.PhotoUtil;
import com.samsao.snapzi.util.SaveImageCallback;
import com.samsao.snapzi.util.VideoUtil;
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


public class EditFragment extends Fragment {

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
    private int mCampaignId;

    private ToolDraw mToolDraw;
    private final ViewTreeObserver.OnGlobalLayoutListener mDrawAnnotationGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            // Controls were initialize, stop listening for their creation
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                mDrawAnnotationContainer.getViewTreeObserver().removeOnGlobalLayoutListener(mDrawAnnotationGlobalLayoutListener);
            } else {
                //noinspection deprecation
                mDrawAnnotationContainer.getViewTreeObserver().removeGlobalOnLayoutListener(mDrawAnnotationGlobalLayoutListener);
            }

            if (mToolDraw != null) {
                mToolDraw.setToolFragment(EditFragment.this);
            }
        }
    };

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
        mLiveFeedAdapter = new LiveFeedAdapter(getActivity());
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
            mVideoContainer.setVisibility(View.GONE);
        }

        // set the fragment for tools
        for (Tool tool : mListener.getTools()) {
            if (tool.getClass().equals(ToolDraw.class)) {
                mToolDraw = (ToolDraw) tool;
            }
            tool.setToolFragment(this);
        }

        // initializes menus
        initToolsMenu();
        initLiveFeed();

        // disable the touch listener on annotations layers
        disableTextAnnotationContainerTouchEvent();
        mDrawAnnotationContainer.setOnTouchListener(null);

        // Adjust draw annotation container to screen size
        mDrawAnnotationContainer.getViewTreeObserver().addOnGlobalLayoutListener(mDrawAnnotationGlobalLayoutListener);

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
                    outRect.left = (int) getResources().getDimension(R.dimen.elements_double_horizontal_margin);
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
        mLiveFeedLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        mLiveFeedRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                if (parent.getChildAdapterPosition(view) != 0) {
                    outRect.left = (int) getResources().getDimension(R.dimen.elements_quarter_horizontal_margin);
                } else {
                    super.getItemOffsets(outRect, view, parent, state);
                }
            }
        });
        mLiveFeedRecyclerView.setHasFixedSize(true);
        mLiveFeedRecyclerView.setLayoutManager(mLiveFeedLayoutManager);
        mLiveFeedRecyclerView.setAdapter(mLiveFeedAdapter);
    }

    public void getFeedImage() {
        mApiService.getLiveFeed(mCampaignId,new Callback<FeedImageList>() {
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
            mListener.setSupportActionBar(mToolbar);
        }
        mListener.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mListener.getSupportActionBar().setDisplayShowTitleEnabled(false);
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
            mCampaignId = mListener.getCampaignId();
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
            mImageContainer.setVisibility(View.GONE);
            mVideoContainer.addView(mVideoPreview);
            mVideoContainer.setVisibility(View.VISIBLE);
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
     * @param showHome
     */
    public void showEditOptionsMenu(boolean showDone, boolean showClear, boolean showUndo, boolean showHome) {
        mListener.showEditMenu(showDone, showClear, showUndo, showHome);
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
        // Merge layers
        ArrayList<Bitmap> bitmapLayers = new ArrayList<Bitmap>();
        float mediaWidth, mediaHeight;

        // TODO add loading screen

        // Get media size in pixel (adjusted to the media container's aspect ratio)
        if (mListener.getEditMode().equals(EditActivity.IMAGE_MODE)) {
            // Source image bitmap
            Bitmap imageBitmap = ((BitmapDrawable) mImageContainer.getDrawable()).getBitmap();
            bitmapLayers.add(imageBitmap);
            mediaWidth = (float) imageBitmap.getWidth();
            mediaHeight = (float) imageBitmap.getHeight();
        } else {
            int videoWidth, videoHeight;
            float videoAspectRatio, videoContainerAspectRatio;

            if (VideoUtil.isVideoPortraitOriented(mListener.getMediaPath())) {
                videoWidth = VideoUtil.getVideoHeight(mListener.getMediaPath());
                videoHeight = VideoUtil.getVideoWidth(mListener.getMediaPath());
            } else {
                videoWidth = VideoUtil.getVideoWidth(mListener.getMediaPath());
                videoHeight = VideoUtil.getVideoHeight(mListener.getMediaPath());
            }
            videoAspectRatio = (float) videoWidth / (float) videoHeight;
            videoContainerAspectRatio = (float) mVideoContainer.getWidth() / (float) mVideoContainer.getHeight();

            // calculate video final size
            if (videoAspectRatio < videoContainerAspectRatio) {
                mediaWidth = videoWidth;
                mediaHeight = (int) ((float) videoHeight / videoContainerAspectRatio);
            } else {
                mediaWidth = (int) ((float) videoWidth / videoContainerAspectRatio);
                mediaHeight = videoHeight;
            }
        }

        // Draw annotation bitmap
        Bitmap drawAnnotationBitmap = mDrawAnnotationContainer.obtainBitmap();
        if (drawAnnotationBitmap != null) {
            float drawAnnotationScaleFactor = Math.max(
                    mediaWidth / (float) drawAnnotationBitmap.getWidth(),
                    mediaHeight / (float) drawAnnotationBitmap.getHeight());
            drawAnnotationBitmap = PhotoUtil.scaleBitmap(drawAnnotationBitmap, drawAnnotationScaleFactor, drawAnnotationScaleFactor);
            bitmapLayers.add(drawAnnotationBitmap);
        }

        // Text annotation bitmap
        Bitmap textAnnotationBitmap = Bitmap.createBitmap(mTextAnnotationContainer.getWidth(), mTextAnnotationContainer.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(textAnnotationBitmap);
        mTextAnnotationContainer.draw(canvas);
        if (textAnnotationBitmap != null) {
            float textAnnotationScaleFactor = Math.max(
                    mediaWidth / (float) textAnnotationBitmap.getWidth(),
                    mediaHeight / (float) textAnnotationBitmap.getHeight());
            textAnnotationBitmap = PhotoUtil.scaleBitmap(textAnnotationBitmap, textAnnotationScaleFactor, textAnnotationScaleFactor);
            bitmapLayers.add(textAnnotationBitmap);
        }

        // Combine images
        Bitmap finalImage = PhotoUtil.combineBitmapsIntoOne(bitmapLayers);

        // Set image destination path
        String imageDestinationPath;
        if (mListener.getEditMode().equals(EditActivity.IMAGE_MODE)) {
            imageDestinationPath = mListener.getMediaPath();
        } else {
            imageDestinationPath = CameraHelper.getDefaultImageFilePath();
        }

        PhotoUtil.saveImage(finalImage, imageDestinationPath, new SaveImageCallback() {
            @Override
            public void onSuccess(String imageDestinationPath) {
                Intent intent = new Intent(getActivity(), ShareActivity.class);

                intent.putExtra(ShareActivity.EXTRA_IMAGE_PATH, imageDestinationPath); // Keep image in both cases
                intent.putExtra(ShareActivity.EXTRA_CAMPAIGN_ID, mCampaignId);
                if (mListener.getEditMode().equals(EditActivity.IMAGE_MODE)) {
                    intent.putExtra(ShareActivity.EXTRA_MEDIA_TYPE, ShareActivity.TYPE_IMAGE);
                } else {
                    intent.putExtra(ShareActivity.EXTRA_MEDIA_TYPE, ShareActivity.TYPE_VIDEO);
                    intent.putExtra(ShareActivity.EXTRA_VIDEO_PATH, mListener.getMediaPath());
                }

                // TODO stop loading screen

                startActivity(intent);
            }

            @Override
            public void onFailure() {
                // TODO stop loading screen on error
            }
        });
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

    public interface Listener {
        int getCampaignId();
        String getEditMode();
        void saveBitmap(Bitmap bitmap);
        void resetMenu();
        void showEditMenu(boolean showDone, boolean showClear, boolean showUndo, boolean showHome);
        ArrayList<Tool> getTools();
        void setTools(ArrayList<Tool> tools);
        Tool getCurrentTool();
        void setCurrentTool(Tool currentTool);
        String getMediaPath();
        ActionBar getSupportActionBar();
        void setSupportActionBar(Toolbar toolbar);
    }
}
