package com.samsao.snapzi;

import android.app.Application;
import android.content.Context;

import com.crashlytics.android.Crashlytics;
import com.sromku.simple.fb.Permission;
import com.sromku.simple.fb.SimpleFacebook;
import com.sromku.simple.fb.SimpleFacebookConfiguration;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;

import net.danlew.android.joda.JodaTimeAndroid;

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

        TwitterAuthConfig authConfig = new TwitterAuthConfig(getString(R.string.twitter_api_key), getString(R.string.twitter_api_secret));
        // start crashlytics
        if (!BuildConfig.DEBUG) {
            Fabric.with(this, new Crashlytics(), new Twitter(authConfig));
        } else {
            Fabric.with(this, new Twitter(authConfig));
        }

        // initialize the application context
        mContext = getApplicationContext();

        // initialize facebook configuration
        Permission[] permissions = new Permission[] {
                Permission.PUBLIC_PROFILE,
                Permission.PUBLISH_ACTION
        };

        SimpleFacebookConfiguration configuration = new SimpleFacebookConfiguration.Builder()
                .setAppId(getResources().getString(R.string.facebook_app_id))
                .setNamespace(getResources().getString(R.string.facebook_app_namespace))
                .setPermissions(permissions)
                .setAskForAllPermissionsAtOnce(true)
                .build();
        SimpleFacebook.setConfiguration(configuration);

        // initialize jodatime
        JodaTimeAndroid.init(this);
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
