package com.samsao.snapzi.edit;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;

import com.hannesdorfmann.parcelableplease.annotation.ParcelablePlease;
import com.hannesdorfmann.parcelableplease.annotation.ParcelableThisPlease;
import com.samsao.snapzi.R;

import java.lang.ref.WeakReference;


/**
 * @author jfcartier
 * @since 15-04-07
 */
@ParcelablePlease(allFields = false)
public class MenuStateEdit extends MenuState implements Parcelable {

    @ParcelableThisPlease
    public boolean mShowHome = true;
    @ParcelableThisPlease
    public boolean mShowDone = false;
    @ParcelableThisPlease
    public boolean mShowClear = false;
    @ParcelableThisPlease
    public boolean mShowUndo = false;

    @Override
    public void onCreateOptionsMenu(MenuInflater menuInflater, Menu menu) {
        menuInflater.inflate(R.menu.activity_edit_edit, menu);
        if (!mShowDone) {
            android.view.MenuItem item = menu.findItem(R.id.activity_edit_done);
            if (item != null) {
                item.setVisible(false);
            }
        }
        if (!mShowClear) {
            android.view.MenuItem item = menu.findItem(R.id.activity_edit_clear);
            if (item != null) {
                item.setVisible(false);
            }
        }
        if (!mShowUndo) {
            android.view.MenuItem item = menu.findItem(R.id.activity_edit_undo);
            if (item != null) {
                item.setVisible(false);
            }
        }
        // hide home menu item
        if (mActivity != null && mActivity.get() != null) {
            mActivity.get().getSupportActionBar().setDisplayHomeAsUpEnabled(mShowHome);
        }
    }

    public MenuStateEdit setShowDone(boolean showDone) {
        mShowDone = showDone;
        return this;
    }

    public MenuStateEdit setShowClear(boolean showClear) {
        mShowClear = showClear;
        return this;
    }

    public MenuStateEdit setShowUndo(boolean showUndo) {
        mShowUndo = showUndo;
        return this;
    }

    public MenuStateEdit setShowHome(boolean showHome) {
        mShowHome = showHome;
        return this;
    }

    @Override
    public MenuStateEdit setActivity(AppCompatActivity activity) {
        mActivity = new WeakReference<>(activity);
        return this;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        MenuStateEditParcelablePlease.writeToParcel(this, dest, flags);
    }

    public static final Creator<MenuStateEdit> CREATOR = new Creator<MenuStateEdit>() {
        public MenuStateEdit createFromParcel(Parcel source) {
            MenuStateEdit target = new MenuStateEdit();
            MenuStateEditParcelablePlease.readFromParcel(target, source);
            return target;
        }

        public MenuStateEdit[] newArray(int size) {
            return new MenuStateEdit[size];
        }
    };
}
