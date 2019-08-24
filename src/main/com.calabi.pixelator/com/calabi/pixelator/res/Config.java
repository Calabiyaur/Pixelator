package com.calabi.pixelator.res;

import java.util.prefs.Preferences;

import com.calabi.pixelator.meta.Direction;

public enum Config {

    BULGE(ConfigType.INT, 0),
    COLOR(ConfigType.STRING, null),
    FILL_SHAPE(ConfigType.BOOLEAN, false),
    FULLSCREEN(ConfigType.BOOLEAN, false),
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

    private ConfigType configType;
    private Object def;

    Config(ConfigType configType, Object def) {
        this.configType = configType;
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
            default:
                throw new UnsupportedOperationException();
        }
    }

}
