package com.calabi.pixelator.res;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.prefs.Preferences;

import com.calabi.pixelator.meta.Direction;
import com.calabi.pixelator.start.ExceptionHandler;

public enum Config {

    BULGE(ConfigType.INT, 0),
    COLOR(ConfigType.STRING, null),
    FILL_SHAPE(ConfigType.BOOLEAN, false),
    FULLSCREEN(ConfigType.BOOLEAN, false),
    GRID_CONFIG(ConfigType.OBJECT, GridConfig.class, GridConfig.getDefault()),
    HEIGHT(ConfigType.DOUBLE, 400d),
    IMAGE_DIRECTORY(ConfigType.STRING, ""),
    NEW_IMAGE_HEIGHT(ConfigType.INT, 32),
    NEW_IMAGE_WIDTH(ConfigType.INT, 32),
    PALETTE_DIRECTORY(ConfigType.STRING, ""),
    PALETTE_MAX_COLORS(ConfigType.INT, 128),
    REPLACE(ConfigType.BOOLEAN, false),
    RESIZE_BIAS(ConfigType.STRING, Direction.NONE.name()),
    RESIZE_KEEP_RATIO(ConfigType.BOOLEAN, true),
    STRETCH_KEEP_RATIO(ConfigType.BOOLEAN, true),
    THICKNESS(ConfigType.INT, 1),
    WIDTH(ConfigType.DOUBLE, 600d);

    //TODO: Add image config here (flexibility!)
    //HEIGHT,
    //H_SCROLL,
    //V_SCROLL,
    //WIDTH,
    //X,
    //Y,
    //ZOOM_LEVEL

    private ConfigType configType;
    private Class<? extends ConfigObject> c;
    private Object def;

    Config(ConfigType configType, Object def) {
        this.configType = configType;
        this.def = def;
    }

    Config(ConfigType configType, Class<? extends ConfigObject> c, Object def) {
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
        if (!configType.equals(this.configType)) {
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
        if (!configType.equals(this.configType)) {
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

}
