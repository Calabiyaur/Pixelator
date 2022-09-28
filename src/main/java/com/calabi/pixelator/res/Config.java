package com.calabi.pixelator.res;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.prefs.Preferences;

import com.calabi.pixelator.file.PixelFile;
import com.calabi.pixelator.log.Logger;
import com.calabi.pixelator.main.ExceptionHandler;
import com.calabi.pixelator.util.meta.Direction;

public enum Config {

    // Global config
    FULLSCREEN(ConfigMode.GLOBAL, ConfigType.BOOLEAN, false),
    PALETTE_MAX_COLORS(ConfigMode.GLOBAL, ConfigType.INT, 128),
    SCREEN_HEIGHT(ConfigMode.GLOBAL, ConfigType.DOUBLE, 400d),
    SCREEN_WIDTH(ConfigMode.GLOBAL, ConfigType.DOUBLE, 600d),
    SCREEN_X(ConfigMode.GLOBAL, ConfigType.DOUBLE, 0d),
    SCREEN_Y(ConfigMode.GLOBAL, ConfigType.DOUBLE, 0d),
    THEME(ConfigMode.GLOBAL, ConfigType.STRING, Theme.BRIGHT.name()),

    // Project config
    ALPHA_ONLY(ConfigMode.GLOBAL, ConfigType.BOOLEAN, false),
    BULGE(ConfigMode.GLOBAL, ConfigType.INT, 0),
    COLOR(ConfigMode.GLOBAL, ConfigType.STRING),
    CROSSHAIR_COLOR(ConfigMode.GLOBAL, ConfigType.STRING, "#00000080"),
    FILL_SHAPE(ConfigMode.GLOBAL, ConfigType.BOOLEAN, false),
    GRID_COLOR(ConfigMode.GLOBAL, ConfigType.STRING, "#00000080"),
    GRID_CONFIG(ConfigMode.GLOBAL, ConfigType.OBJECT, GridConfig.class, GridConfig.getDefault()),
    IMAGE_BACKGROUND_COLOR(ConfigMode.GLOBAL, ConfigType.STRING, "#DDDDDD"),
    IMAGE_BORDER_COLOR(ConfigMode.GLOBAL, ConfigType.STRING, "#00000000"),
    IMAGE_DIRECTORY(ConfigMode.GLOBAL, ConfigType.STRING, ""),
    NEW_IMAGE_HEIGHT(ConfigMode.GLOBAL, ConfigType.INT, 32),
    NEW_IMAGE_WIDTH(ConfigMode.GLOBAL, ConfigType.INT, 32),
    PALETTE_DIRECTORY(ConfigMode.GLOBAL, ConfigType.STRING, ""),
    REPLACE(ConfigMode.GLOBAL, ConfigType.BOOLEAN, false),
    RESIZE_BIAS(ConfigMode.GLOBAL, ConfigType.STRING, Direction.NONE.name()),
    RESIZE_KEEP_RATIO(ConfigMode.GLOBAL, ConfigType.BOOLEAN, true),
    ROTATE_DEGREES(ConfigMode.GLOBAL, ConfigType.INT, 0),
    STRETCH_KEEP_RATIO(ConfigMode.GLOBAL, ConfigType.BOOLEAN, true),
    THICKNESS(ConfigMode.GLOBAL, ConfigType.INT, 1),
    TOLERANCE(ConfigMode.GLOBAL, ConfigType.INT, 0),
    TOOL(ConfigMode.GLOBAL, ConfigType.INT, 0),

    // Image config
    FRAME_INDEX(ConfigMode.IMAGE, ConfigType.INT, 0),
    GRID_SELECTION(ConfigMode.IMAGE, ConfigType.OBJECT, GridSelectionConfig.class, ""),
    IMAGE_H_SCROLL(ConfigMode.IMAGE, ConfigType.DOUBLE),
    IMAGE_HEIGHT(ConfigMode.IMAGE, ConfigType.DOUBLE),
    IMAGE_V_SCROLL(ConfigMode.IMAGE, ConfigType.DOUBLE),
    IMAGE_WIDTH(ConfigMode.IMAGE, ConfigType.DOUBLE),
    IMAGE_X(ConfigMode.IMAGE, ConfigType.DOUBLE),
    IMAGE_Y(ConfigMode.IMAGE, ConfigType.DOUBLE),
    IMAGE_ZOOM_LEVEL(ConfigMode.IMAGE, ConfigType.DOUBLE);

    private final ConfigMode mode;
    private final ConfigType type;
    private final Class<? extends ConfigObject> c;
    private Object def;

    Config(ConfigMode mode, ConfigType type) {
        this(mode, type, null);
    }

    Config(ConfigMode mode, ConfigType type, Object def) {
        this(mode, type, null, def);
    }

    Config(ConfigMode mode, ConfigType type, Class<? extends ConfigObject> c, Object def) {
        this.mode = mode;
        this.type = type;
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

    private Object get(ConfigType type, Object def) {
        Object result = null;
        if (ConfigMode.PROJECT.equals(mode) && Project.active()) {
            result = getProjectConfig(type);
        }
        if (result == null) {
            result = getGlobalConfig(type, def);
        }
        return result;
    }

    private Object getProjectConfig(ConfigType type) {
        if (ConfigMode.IMAGE.equals(mode) || !type.equals(this.type)) {
            throw new UnsupportedOperationException();
        }
        return Project.get().getConfig(name(), type);
    }

    private Object getGlobalConfig(ConfigType type, Object def) {
        if (ConfigMode.IMAGE.equals(mode) || !type.equals(this.type)) {
            throw new UnsupportedOperationException();
        }
        return switch(type) {
            case BOOLEAN -> Preferences.userRoot().getBoolean(name(), (boolean) def);
            case DOUBLE -> Preferences.userRoot().getDouble(name(), (double) def);
            case INT -> Preferences.userRoot().getInt(name(), (int) def);
            case STRING -> Preferences.userRoot().get(name(), (String) def);
            case OBJECT -> Preferences.userRoot().get(name(), null);
        };
    }

    public void putBoolean(boolean value) {
        //TODO: Could we not just say 'put(T value)' and then decide which type T is instance of?
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

    public void putObject(ConfigObject value) {
        put(ConfigType.OBJECT, value);
    }

    private void put(ConfigType type, Object value) {
        if (ConfigMode.PROJECT.equals(mode) && Project.active()) {
            putProjectConfig(type, value);
        } else {
            putGlobalConfig(type, value);
        }
    }

    private void putProjectConfig(ConfigType type, Object value) {
        if (ConfigMode.IMAGE.equals(mode) || !type.equals(this.type)) {
            throw new UnsupportedOperationException();
        }
        Project.get().putConfig(name(), type, value);
    }

    private void putGlobalConfig(ConfigType type, Object value) {
        if (ConfigMode.IMAGE.equals(mode) || !type.equals(this.type)) {
            throw new UnsupportedOperationException();
        }
        switch(type) {
            case BOOLEAN -> Preferences.userRoot().putBoolean(name(), (boolean) value);
            case DOUBLE -> Preferences.userRoot().putDouble(name(), (double) value);
            case INT -> Preferences.userRoot().putInt(name(), (int) value);
            case STRING -> Preferences.userRoot().put(name(), (String) value);
            case OBJECT -> Preferences.userRoot().put(name(), ((ConfigObject) value).toConfig());
        }
    }

    public boolean getBoolean(PixelFile file, boolean def) {
        return (boolean) getImageConfig(file, ConfigType.BOOLEAN, def);
    }

    public boolean getBoolean(PixelFile file) {
        return (boolean) getImageConfig(file, ConfigType.BOOLEAN, def);
    }

    public double getDouble(PixelFile file, double def) {
        return (double) getImageConfig(file, ConfigType.DOUBLE, def);
    }

    public int getInt(PixelFile file) {
        return (int) getImageConfig(file, ConfigType.INT, def);
    }

    public String getString(PixelFile file, String def) {
        return (String) getImageConfig(file, ConfigType.STRING, def);
    }

    public <T extends ConfigObject> T getObject(PixelFile file) {
        String string = (String) getImageConfig(file, ConfigType.OBJECT, def);
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

    private Object getImageConfig(PixelFile file, ConfigType configType, Object def) {
        if (!ConfigMode.IMAGE.equals(mode) || !configType.equals(this.type)) {
            Logger.log(name() + "(" + configType.name() + ")");
            throw new UnsupportedOperationException();
        }
        String stringValue = file.getProperties().getProperty(name());
        if (stringValue == null) {
            return def;
        }
        return switch(configType) {
            case BOOLEAN -> Boolean.valueOf(stringValue);
            case DOUBLE -> Double.valueOf(stringValue);
            case INT -> Integer.valueOf(stringValue);
            case STRING, OBJECT -> stringValue;
        };
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

    public void putObject(PixelFile file, ConfigObject value) {
        put(file, ConfigType.OBJECT, value);
    }

    private void put(PixelFile file, ConfigType configType, Object value) {
        if (ConfigMode.GLOBAL.equals(mode) || !configType.equals(this.type)) {
            throw new UnsupportedOperationException();
        }
        file.getProperties().put(name(), String.valueOf(value));
    }

}
