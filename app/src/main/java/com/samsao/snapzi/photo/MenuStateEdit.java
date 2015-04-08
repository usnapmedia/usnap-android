package com.samsao.snapzi.photo;

import android.os.Parcel;
import android.os.Parcelable;
import android.view.Menu;
import android.view.MenuInflater;

import com.hannesdorfmann.parcelableplease.annotation.ParcelablePlease;
import com.samsao.snapzi.R;

/**
 * @author jfcartier
 * @since 15-04-07
 */
@ParcelablePlease
public class MenuStateEdit extends MenuState implements Parcelable {
    public boolean mShowClear = false;
    public boolean mShowUndo = false;

    @Override
    public void onCreateOptionsMenu(MenuInflater menuInflater, Menu menu) {
        menuInflater.inflate(R.menu.activity_photo_edit_edit, menu);
        if (!mShowClear) {
            android.view.MenuItem item = menu.findItem(R.id.activity_photo_edit_clear);
            if (item != null) {
                item.setVisible(false);
            }
        }
        if (!mShowUndo) {
            android.view.MenuItem item = menu.findItem(R.id.activity_photo_edit_undo);
            if (item != null) {
                item.setVisible(false);
            }
        }
    }

    public MenuStateEdit setShowClear(boolean showClear) {
        mShowClear = showClear;
        return this;
    }

    public MenuStateEdit setShowUndo(boolean showUndo) {
        mShowUndo = showUndo;
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
