package com.samsao.snapzi.edit;

import android.os.Parcelable;
import android.view.Menu;
import android.view.MenuInflater;

/**
 * @author jfcartier
 * @since 15-04-07
 */
public abstract class MenuState implements Parcelable {
    public abstract void onCreateOptionsMenu(MenuInflater menuInflater, Menu menu);
}
