package com.samsao.snapzi.video;

import android.app.Activity;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.widget.FrameLayout;
import android.widget.VideoView;

import com.samsao.snapzi.camera.CameraHelper;


/**
 * @author vlegault
 * @since 15-03-24
 */
public class VideoPreview extends VideoView implements SurfaceHolder.Callback {

    /**
     * Constants
     */
    private final String LOG_TAG = getClass().getSimpleName();

    private SurfaceHolder mHolder;
    private LayoutMode mLayoutMode;
    private String mVideoPath;

    public static enum LayoutMode {
        FitParent,
        CenterCrop
    }

    public VideoPreview(Activity activity, LayoutMode layoutMode, String videoPath) {
        super(activity);

        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        mHolder = getHolder();
        mHolder.addCallback(this);

        // deprecated setting, but required on Android versions prior to 3.0
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
            }
        });
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        stopPlayback();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        MediaMetadataRetriever metaRetriever = new MediaMetadataRetriever();
        metaRetriever.setDataSource(mVideoPath);
        int videoWidth = Integer.valueOf(metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
        int videoHeight = Integer.valueOf(metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));
        metaRetriever.release();

        if (!adjustSurfaceLayoutSize(videoWidth, videoHeight, width, height)) {
            start();
        }
    }

    /**
     * Adjusts SurfaceView dimension to our layout available space.
     */
    private boolean adjustSurfaceLayoutSize(int targetedWidth, int targetedHeight,
                                            int availableWidth, int availableHeight) {
        float previewSizeWidth, previewSizeHeight;
        float heightScale, widthScale, previewSizeScale;

        if (CameraHelper.isPortrait(getContext())) {
            previewSizeWidth = targetedHeight;
            previewSizeHeight = targetedWidth;
        } else {
            previewSizeWidth = targetedWidth;
            previewSizeHeight = targetedHeight;
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
