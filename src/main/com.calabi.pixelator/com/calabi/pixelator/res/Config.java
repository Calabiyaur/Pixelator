package com.calabi.pixelator.res;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.prefs.Preferences;

import com.calabi.pixelator.files.PixelFile;
import com.calabi.pixelator.logging.Logger;
import com.calabi.pixelator.meta.Direction;
import com.calabi.pixelator.start.ExceptionHandler;

public enum Config {

    BULGE(true, ConfigType.INT, 0),
    COLOR(true, ConfigType.STRING),
    FILL_SHAPE(true, ConfigType.BOOLEAN, false),
    FULLSCREEN(true, ConfigType.BOOLEAN, false),
    GRID_CONFIG(true, ConfigType.OBJECT, GridConfig.class, GridConfig.getDefault()),
    HEIGHT(true, ConfigType.DOUBLE, 400d),
    IMAGE_BACKGROUND_COLOR(true, ConfigType.STRING, "#DDDDDD"),
    IMAGE_DIRECTORY(true, ConfigType.STRING, ""),
    IMAGE_H_SCROLL(false, ConfigType.DOUBLE),
    IMAGE_HEIGHT(false, ConfigType.DOUBLE),
    IMAGE_V_SCROLL(false, ConfigType.DOUBLE),
    IMAGE_WIDTH(false, ConfigType.DOUBLE),
    IMAGE_X(false, ConfigType.DOUBLE),
    IMAGE_Y(false, ConfigType.DOUBLE),
    IMAGE_ZOOM_LEVEL(false, ConfigType.DOUBLE),
    NEW_IMAGE_HEIGHT(true, ConfigType.INT, 32),
    NEW_IMAGE_WIDTH(true, ConfigType.INT, 32),
    PALETTE_DIRECTORY(true, ConfigType.STRING, ""),
    PALETTE_MAX_COLORS(true, ConfigType.INT, 128),
    REPLACE(true, ConfigType.BOOLEAN, false),
    RESIZE_BIAS(true, ConfigType.STRING, Direction.NONE.name()),
    RESIZE_KEEP_RATIO(true, ConfigType.BOOLEAN, true),
    STRETCH_KEEP_RATIO(true, ConfigType.BOOLEAN, true),
    THICKNESS(true, ConfigType.INT, 1),
    WIDTH(true, ConfigType.DOUBLE, 600d);

    private final boolean global;
    private final ConfigType configType;
    private Class<? extends ConfigObject> c;
    private Object def;

    Config(boolean global, ConfigType configType) {
        this(global, configType, null);
    }

    Config(boolean global, ConfigType configType, Object def) {
        this.global = global;
        this.configType = configType;
        this.def = def;
    }

    Config(boolean global, ConfigType configType, Class<? extends ConfigObject> c, Object def) {
        this.global = global;
        this.configType = configType;
        this.c = c;
        this.def = def;
    }

    public void setDef(Object def) {
        this.def = def;
    }

    public String toString() {
        return name();
    }

    public boolean getBoolean() {
        return (boolean) get(ConfigType.BOOLEAN, def);
    }

    public double getDouble() {
        return (double) get(ConfigType.DOUBLE, def);
    }

    public int getInt() {
        return (int) get(ConfigType.INT, def);
    }

    public String getString() {
        return (String) get(ConfigType.STRING, def);
    }

    public <T extends ConfigObject> T getObject() {
        String string = (String) get(ConfigType.OBJECT, def);
        if (string != null) {
            try {
                Constructor<T> constructor = (Constructor<T>) c.getConstructor();
                T instance = constructor.newInstance();
                instance.build(string);
                return instance;
            } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
                ExceptionHandler.handle(e);
            }
        }
        return (T) def;
    }

    private Object get(ConfigType configType, Object def) {
        if (!global || !configType.equals(this.configType)) {
            throw new UnsupportedOperationException();
        }
        switch(configType) {
            case BOOLEAN:
                return Preferences.userRoot().getBoolean(name(), (boolean) def);
            case DOUBLE:
                return Preferences.userRoot().getDouble(name(), (double) def);
            case INT:
                return Preferences.userRoot().getInt(name(), (int) def);
            case STRING:
                return Preferences.userRoot().get(name(), (String) def);
            case OBJECT:
                return Preferences.userRoot().get(name(), null);
            default:
                throw new UnsupportedOperationException();
        }
    }

    public void putBoolean(boolean value) {
        put(ConfigType.BOOLEAN, value);
    }

    public void putDouble(double value) {
        put(ConfigType.DOUBLE, value);
    }

    public void putInt(int value) {
        put(ConfigType.INT, value);
    }

    public void putString(String value) {
        put(ConfigType.STRING, value);
    }

    public <T> void putObject(ConfigObject value) {
        put(ConfigType.OBJECT, value);
    }

    private void put(ConfigType configType, Object value) {
        if (!global || !configType.equals(this.configType)) {
            throw new UnsupportedOperationException();
        }
        switch(configType) {
            case BOOLEAN:
                Preferences.userRoot().putBoolean(name(), (boolean) value);
                break;
            case DOUBLE:
                Preferences.userRoot().putDouble(name(), (double) value);
                break;
            case INT:
                Preferences.userRoot().putInt(name(), (int) value);
                break;
            case STRING:
                Preferences.userRoot().put(name(), (String) value);
                break;
            case OBJECT:
                Preferences.userRoot().put(name(), ((ConfigObject) value).toConfig());
                break;
            default:
                throw new UnsupportedOperationException();
        }
    }

    public boolean getBoolean(PixelFile file, boolean def) {
        return (boolean) get(file, ConfigType.BOOLEAN, def);
    }

    public double getDouble(PixelFile file, double def) {
        return (double) get(file, ConfigType.DOUBLE, def);
    }

    public int getInt(PixelFile file, int def) {
        return (int) get(file, ConfigType.INT, def);
    }

    public String getString(PixelFile file, String def) {
        return (String) get(file, ConfigType.STRING, def);
    }

    private Object get(PixelFile file, ConfigType configType, Object def) {
        if (global || !configType.equals(this.configType)) {
            Logger.log(name() + "(" + configType.name() + ")");
            throw new UnsupportedOperationException();
        }
        String stringValue = file.getProperties().getProperty(name());
        if (stringValue == null) {
            return def;
        }
        switch(configType) {
            case BOOLEAN:
                return Boolean.valueOf(stringValue);
            case DOUBLE:
                return Double.valueOf(stringValue);
            case INT:
                return Integer.valueOf(stringValue);
            case STRING:
            case OBJECT:
                return stringValue;
            default:
                throw new UnsupportedOperationException();
        }
    }

    public void putBoolean(PixelFile file, boolean value) {
        put(file, ConfigType.BOOLEAN, value);
    }

    public void putDouble(PixelFile file, double value) {
        put(file, ConfigType.DOUBLE, value);
    }

    public void putInt(PixelFile file, int value) {
        put(file, ConfigType.INT, value);
    }

    public void putString(PixelFile file, String value) {
        put(file, ConfigType.STRING, value);
    }

    private void put(PixelFile file, ConfigType configType, Object value) {
        if (global || !configType.equals(this.configType)) {
            throw new UnsupportedOperationException();
        }
        file.getProperties().put(name(), String.valueOf(value));
    }

}
