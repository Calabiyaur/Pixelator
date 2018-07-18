package main.java.view.tool;

import java.lang.reflect.InvocationTargetException;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import main.java.util.StringUtil;
import main.java.view.editor.ImageEditor;
import main.java.view.editor.ImageWindow;

public class ToolManager {

    private static ObjectProperty<ImageWindow> imageWindow = new SimpleObjectProperty<>();

    private ToolManager() {
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
