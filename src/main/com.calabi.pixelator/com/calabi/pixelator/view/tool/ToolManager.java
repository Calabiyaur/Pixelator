package com.calabi.pixelator.view.tool;

import java.lang.reflect.InvocationTargetException;
import java.util.IdentityHashMap;
import java.util.Map;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import com.calabi.pixelator.util.StringUtil;
import com.calabi.pixelator.view.editor.ImageEditor;
import com.calabi.pixelator.view.editor.ImageWindow;

public class ToolManager {

    private static ObjectProperty<ImageWindow> imageWindow = new SimpleObjectProperty<>();
    private static Map<Tools, Tool> fromEnumMap = new IdentityHashMap<>();
    private static Map<Tool, Tools> toEnumMap = new IdentityHashMap<>();

    private ToolManager() {
    }

    public static Tool getTool(Tools tool) {
        return fromEnumMap.computeIfAbsent(tool, t -> {
            Tool result;
            try {
                String classPath = Tool.class.getPackage().getName();
                String className = StringUtil.toCamelCap(t.name());
                Class<?> clazz = Class.forName(classPath + "." + className);
                result = (Tool) clazz.getMethod("getMe").invoke(null);
            } catch (ClassNotFoundException
                    | IllegalAccessException
                    | InvocationTargetException
                    | NoSuchMethodException e) {
                throw new IllegalStateException("No tool found for '" + t.name() + "'!");
            }
            toEnumMap.put(result, t);
            return result;
        });
    }

    public static Tools fromTool(Tool tool) {
        return toEnumMap.computeIfAbsent(tool, t -> {
            String simpleName = t.getClass().getSimpleName();
            String caps = StringUtil.toCaps(simpleName);
            Tools result = Tools.valueOf(caps);
            fromEnumMap.put(result, t);
            return result;
        });
    }

    public static ObjectProperty<ImageWindow> imageWindowProperty() {
        return imageWindow;
    }

    public static ImageWindow getImageWindow() {
        return imageWindow.get();
    }

    public static ImageEditor getEditor() {
        return getImageWindow() == null ? null : getImageWindow().getEditor();
    }
}
