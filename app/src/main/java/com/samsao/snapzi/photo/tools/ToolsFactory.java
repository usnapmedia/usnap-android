package com.samsao.snapzi.photo.tools;

import com.samsao.snapzi.photo.MenuContainer;

/**
 * @author jfcartier
 * @since 15-04-07
 */
public class ToolsFactory {

    public static final int TOOL_FILTERS = 0;
    public static final int TOOL_OPTION_BRIGHTNESS = 0;

    public static Tool getTool(int type, MenuContainer menuContainer) {
        Tool tool = null;
        switch (type) {
            case TOOL_FILTERS:
                ToolFilters toolFilters = new ToolFilters();
                toolFilters.setMenuContainer(menuContainer);
                toolFilters.addOption(ToolsFactory.getToolOption(TOOL_OPTION_BRIGHTNESS, toolFilters));
                tool = toolFilters;
                break;
        }
        return tool;
    }

    public static ToolOption getToolOption(int type, Tool tool) {
        ToolOption toolOption = null;
        switch (type) {
            case TOOL_OPTION_BRIGHTNESS:
                toolOption = new ToolBrightness().setTool(tool);
                break;
        }
        return toolOption;
    }
}
