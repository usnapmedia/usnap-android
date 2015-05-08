package com.samsao.snapzi.edit;

/**
 * @author jfcartier
 * @since 15-04-06
 */
public interface MenuItem {
    public String getName();

    public int getImageResource();

    public void onSelected();

    public boolean isSelected();
}
