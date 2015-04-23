package com.samsao.snapzi.edit;

import android.os.Parcel;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;

import com.hannesdorfmann.parcelableplease.annotation.ParcelablePlease;
import com.samsao.snapzi.R;

import java.lang.ref.WeakReference;


/**
 * @author jfcartier
 * @since 15-04-07
 */
@ParcelablePlease
public class MenuStateView extends MenuState {

    @Override
    public void onCreateOptionsMenu(MenuInflater menuInflater, Menu menu) {
        menuInflater.inflate(R.menu.activity_edit, menu);
        // show home menu item
        if (mActivity != null && mActivity.get() != null) {
            mActivity.get().getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public MenuStateView setActivity(ActionBarActivity activity) {
        mActivity = new WeakReference<>(activity);
        return this;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        MenuStateViewParcelablePlease.writeToParcel(this, dest, flags);
    }

    public static final Creator<MenuStateView> CREATOR = new Creator<MenuStateView>() {
        public MenuStateView createFromParcel(Parcel source) {
            MenuStateView target = new MenuStateView();
            MenuStateViewParcelablePlease.readFromParcel(target, source);
            return target;
        }

        public MenuStateView[] newArray(int size) {
            return new MenuStateView[size];
        }
    };
}
