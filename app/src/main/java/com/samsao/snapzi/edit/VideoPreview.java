package com.samsao.snapzi.edit;

import android.app.Activity;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.VideoView;

import com.samsao.snapzi.camera.CameraHelper;

import java.util.HashMap;


/**
 * @author vlegault
 * @since 15-03-24
 */
public class VideoPreview extends VideoView implements SurfaceHolder.Callback {

    private SurfaceHolder mHolder;
    private LayoutMode mLayoutMode;
    private String mVideoPath;
    private MediaPlayer.OnPreparedListener mOnPreparedListener;

    public static enum LayoutMode {
        FitParent,
        CenterCrop
    }

    public VideoPreview(Activity activity, String videoPath) {
        super(activity);
        init(LayoutMode.CenterCrop, videoPath);
    }

    public VideoPreview(Activity activity, LayoutMode layoutMode, String videoPath) {
        super(activity);
        init(layoutMode, videoPath);
    }

    private void init(LayoutMode layoutMode, String videoPath) {
        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        mHolder = getHolder();
        mHolder.addCallback(this);

        // deprecated setting, but required on Android versions prior to 3.0
        //noinspection deprecation
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        mLayoutMode = layoutMode;
        mVideoPath = videoPath;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        setVideoPath(mVideoPath);
        setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
                if (mOnPreparedListener != null) {
                    mOnPreparedListener.onPrepared(mp);
                }
            }
        });
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        stopPlayback();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        resizeToFitParentView();
    }

    /**
     * Adds an additionnal MediaPlayer.OnPreparedListener
     * @param onPreparedListener
     */
    public void addOnPreparedListener(MediaPlayer.OnPreparedListener onPreparedListener) {
        mOnPreparedListener = onPreparedListener;
    }

    /**
     * Resize PhotoCamera view to fit it's parent container
     */
    public void resizeToFitParentView() {
        int parentViewWidth = ((View) getParent()).getWidth();
        int parentViewHeight = ((View) getParent()).getHeight();

        if (!adjustSurfaceLayoutSize(parentViewWidth, parentViewHeight)) {
            start();
        }
    }

    /**
     * Adjusts this VideoView dimension to our layout available space.
     *
     * @param availableWidth  available width of the parent container
     * @param availableHeight available heigth of the parent container
     */
    private boolean adjustSurfaceLayoutSize(int availableWidth, int availableHeight) {
        float previewSizeWidth, previewSizeHeight;
        float heightScale, widthScale, previewSizeScale;

        // Get video size
        MediaMetadataRetriever metaRetriever = new MediaMetadataRetriever();
        if (mVideoPath.startsWith("http")) {
            try {
                metaRetriever.setDataSource(mVideoPath, new HashMap<String, String>());
            } catch (Exception e) {
                return false;
            }
        } else {
            metaRetriever.setDataSource(mVideoPath);
        }
        int videoWidth = Integer.valueOf(metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
        int videoHeight = Integer.valueOf(metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));
        metaRetriever.release();

        if (CameraHelper.isPortrait(getContext())) {
            previewSizeWidth = videoHeight;
            previewSizeHeight = videoWidth;
        } else {
            previewSizeWidth = videoWidth;
            previewSizeHeight = videoHeight;
        }

        heightScale = availableHeight / previewSizeHeight;
        widthScale = availableWidth / previewSizeWidth;

        if (mLayoutMode == LayoutMode.FitParent) {
            // Select smaller factor, because the surface cannot be set to the size larger than display metrics.
            if (heightScale < widthScale) {
                previewSizeScale = heightScale;
            } else {
                previewSizeScale = widthScale;
            }
        } else {
            if (heightScale < widthScale) {
                previewSizeScale = widthScale;
            } else {
                previewSizeScale = heightScale;
            }
        }

        int layoutHeight = Math.round(previewSizeHeight * previewSizeScale);
        int layoutWidth = Math.round(previewSizeWidth * previewSizeScale);

        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.getLayoutParams();
        if ((layoutWidth != this.getWidth()) || (layoutHeight != this.getHeight())) {
            layoutParams.height = layoutHeight;
            layoutParams.width = layoutWidth;
            layoutParams.gravity = Gravity.CENTER;
            this.setLayoutParams(layoutParams);

            // A call to setLayoutParams will trigger another surfaceChanged invocation.
            // Set return value to true since the layout as been modified.
            return true;
        } else {
            // Set return value to false since no changes were made to the layout.
            return false;
        }
    }
}
