package com.samsao.snapzi.edit;

import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;

import com.hannesdorfmann.parcelableplease.annotation.ParcelableNoThanks;

import java.lang.ref.WeakReference;

/**
 * @author jfcartier
 * @since 15-04-07
 */
public abstract class MenuState implements Parcelable {
    @ParcelableNoThanks
    protected WeakReference<AppCompatActivity> mActivity;

    public abstract void onCreateOptionsMenu(MenuInflater menuInflater, Menu menu);

    public MenuState setActivity(AppCompatActivity activity) {
        mActivity = new WeakReference<>(activity);
        return this;
    }
}
