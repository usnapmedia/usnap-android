package com.samsao.snapzi.util;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;


/**
 * Created by vlegault on 15-04-08.
 */
public class WindowUtil {

    private final static String LOG_TAG = WindowUtil.class.getSimpleName();


    /**
     * Unlocks passed activity screen orientation.
     * @param activity
     */
    public static void unlockScreenOrientation(Activity activity) {
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
    }

    /**
     * Locks passed activity screen orientation.
     * @param activity
     */
    public static void lockScreenOrientation(Activity activity) {
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();

        switch (activity.getResources().getConfiguration().orientation) {
            case Configuration.ORIENTATION_PORTRAIT:
                if (rotation == android.view.Surface.ROTATION_90 || rotation == android.view.Surface.ROTATION_180) {
                    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
                } else {
                    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                }
                break;

            case Configuration.ORIENTATION_LANDSCAPE:
                if (rotation == android.view.Surface.ROTATION_0 || rotation == android.view.Surface.ROTATION_90) {
                    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                } else {
                    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
                }
                break;
        }
    }
}
