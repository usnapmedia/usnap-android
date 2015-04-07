package com.samsao.snapzi.photo.tools;

import com.samsao.snapzi.photo.MenuContainer;

import java.util.ArrayList;

/**
 * @author jfcartier
 * @since 15-04-07
 */
public class ToolsFactory {

    public static final int TOOL_FILTERS = 0;
    public static final int TOOL_BRIGHTNESS = 1;

    public static Tool getTool(int type, MenuContainer menuContainer) {
        Tool tool = null;
        switch (type) {
            case TOOL_FILTERS:
                ToolFilters toolFilters = new ToolFilters();
                toolFilters.setMenuContainer(menuContainer);
                ArrayList<Tool> tools = new ArrayList<>();
                tools.add(ToolsFactory.getTool(TOOL_BRIGHTNESS, menuContainer));
                toolFilters.setTools(tools);
                tool = toolFilters;
                break;
            case TOOL_BRIGHTNESS:
                tool = new ToolBrightness().setMenuContainer(menuContainer);
                break;
        }
        return tool;
    }
}
