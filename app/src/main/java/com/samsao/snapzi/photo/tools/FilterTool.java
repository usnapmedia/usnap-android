package com.samsao.snapzi.photo.tools;

import android.os.Parcel;
import android.view.View;

import com.samsao.snapzi.R;
import com.samsao.snapzi.photo.MenuContainer;
import com.samsao.snapzi.photo.MenuItem;

import java.util.ArrayList;

/**
 * @author jfcartier
 * @since 15-04-06
 */
public class FilterTool extends Tool {

    public FilterTool(MenuContainer menuContainer) {
        super(menuContainer);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }

    @Override
    public MenuItem getMenuItem() {
        return new MenuItem() {
            @Override
            public String getName() {
                return getString(R.string.tool_filters_name);
            }

            @Override
            public int getImageResource() {
                return 0;
            }

            @Override
            public View.OnClickListener getOnClickListener() {
                return new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // TODO
                        ArrayList<MenuItem> newItems = new ArrayList<>();
                        newItems.add(new MenuItem() {
                            @Override
                            public String getName() {
                                return getString(R.string.tool_filters_filter_brightness_name);
                            }

                            @Override
                            public int getImageResource() {
                                return 0;
                            }

                            @Override
                            public View.OnClickListener getOnClickListener() {
                                return null;
                            }
                        });

                        newItems.add(new MenuItem() {
                            @Override
                            public String getName() {
                                return getString(R.string.tool_filters_filter_contrast_name);
                            }

                            @Override
                            public int getImageResource() {
                                return 0;
                            }

                            @Override
                            public View.OnClickListener getOnClickListener() {
                                return null;
                            }
                        });

                        mMenuContainer.setMenuItems(newItems);
                    }
                };
            }
        };
    }
}
