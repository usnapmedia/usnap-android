package com.samsao.snapzi.util;

import android.content.Context;
import android.net.Uri;


/**
 * Created by vlegault on 15-04-07.
 */
public class MediaUtil {

    private final static String LOG_TAG = MediaUtil.class.getSimpleName();

    public static enum MediaType {
        Image,
        Video,
        Unsupported
    }


    public static MediaType getMediaTypeFromUri(Context context, Uri mediaUri) {
        String mediaType = context.getContentResolver().getType(mediaUri);

        if (mediaType.startsWith("image")) {
            return MediaType.Image;
        } else if (mediaType.startsWith("video")) {
            return MediaType.Video;
        } else {
            return MediaType.Unsupported;
        }
    }
}
