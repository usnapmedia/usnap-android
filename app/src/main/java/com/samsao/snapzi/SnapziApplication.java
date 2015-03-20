package com.samsao.snapzi;

import android.app.Application;
import android.content.Context;

import com.adobe.creativesdk.foundation.AdobeCSDKFoundation;
import com.adobe.creativesdk.foundation.auth.IAdobeAuthClientCredentials;
import com.aviary.android.feather.sdk.IAviaryClientCredentials;
import com.crashlytics.android.Crashlytics;
import com.sromku.simple.fb.Permission;
import com.sromku.simple.fb.SimpleFacebook;
import com.sromku.simple.fb.SimpleFacebookConfiguration;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;

import io.fabric.sdk.android.Fabric;

/**
 * @author jfcartier
 * @since 15-03-12
 */
public class SnapziApplication extends Application implements IAdobeAuthClientCredentials,
        IAviaryClientCredentials {

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
        };
        SimpleFacebookConfiguration configuration = new SimpleFacebookConfiguration.Builder()
                .setAppId(getResources().getString(R.string.facebook_app_id))
                .setNamespace(getResources().getString(R.string.facebook_app_namespace))
                .setPermissions(permissions)
                .build();
        SimpleFacebook.setConfiguration(configuration);

        // enable Adobe creative SDK
        AdobeCSDKFoundation.initializeCSDKFoundation(getApplicationContext());
    }

    /**
     * This method returns the application context
     *
     * @return application context
     */
    public static Context getContext() {
        return mContext;
    }

    @Override
    public String getClientID() {
        return getString(R.string.adobe_client_id);
    }

    @Override
    public String getClientSecret() {
        return getString(R.string.adobe_client_secret);
    }

    @Override
    public String getBillingKey() {
        return "";
    }
}
