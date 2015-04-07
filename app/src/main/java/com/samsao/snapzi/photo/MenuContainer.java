package com.samsao.snapzi.photo;

import java.util.ArrayList;

/**
 * @author jfcartier
 * @since 15-04-06
 */
public interface MenuContainer {
    public void setMenuItems(ArrayList<MenuItem> items);
    public void resetMenu();
}
