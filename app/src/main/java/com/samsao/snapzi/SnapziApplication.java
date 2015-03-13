package com.samsao.snapzi;

import android.app.Application;
import android.content.Context;
import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;

/**
 * @author jfcartier
 * @since 15-03-12
 */
public class SnapziApplication extends Application {

    /**
     * Static context
     */
    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();

        // start crashlytics
        if (!BuildConfig.DEBUG) {
            Fabric.with(this, new Crashlytics());
        }

        // initialize the application context
        mContext = getApplicationContext();
    }

    /**
     * This method returns the application context
     *
     * @return application context
     */
    public static Context getContext() {
        return mContext;
    }
}
