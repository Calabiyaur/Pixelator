package com.calabi.pixelator.view.tool;

import java.lang.reflect.InvocationTargetException;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import com.calabi.pixelator.view.editor.ImageEditor;
import com.calabi.pixelator.view.editor.ImageWindow;

public class ToolManager {

    private static ObjectProperty<ImageWindow> imageWindow = new SimpleObjectProperty<>();

    private ToolManager() {
    }

    public static Tool getTool(main.pixelator.view.tool.Tools tool) {
        Tool result;
        try {
            String classPath = Tool.class.getPackage().getName();
            String className = main.pixelator.util.StringUtil.toCamelCap(tool.name());
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
