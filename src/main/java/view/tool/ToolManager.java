package main.java.view.tool;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import main.java.util.StringUtil;

public class ToolManager {

    private static final Map<Tools, Tool> toolMap = new HashMap<>();

    private ToolManager() {
    }

    public static void setTool(Tools enumTool, Tool tool) {
        toolMap.put(enumTool, tool);
    }

    public static Tool getTool(Tools tool) {
        Tool result;
        try {
            String classPath = Tool.class.getPackage().getName();
            String className = StringUtil.toCamelCap(tool.name());
            Class<?> clazz = Class.forName(classPath + "." + className);
            result = (Tool) clazz.getMethod("getMe").invoke(null);
        } catch (ClassNotFoundException
                | IllegalAccessException
                | InvocationTargetException
                | NoSuchMethodException e) {
            throw new IllegalStateException("No tool found for '" + tool.name() + "'!");
        }
        return result;
    }

}
