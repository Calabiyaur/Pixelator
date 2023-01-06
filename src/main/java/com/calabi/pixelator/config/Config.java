package com.calabi.pixelator.config;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.prefs.Preferences;

import com.calabi.pixelator.file.PixelFile;
import com.calabi.pixelator.log.Logger;
import com.calabi.pixelator.project.OpenedImagesConfig;
import com.calabi.pixelator.project.OpenedPalettesConfig;
import com.calabi.pixelator.project.Project;
import com.calabi.pixelator.project.RecentProjectsConfig;
import com.calabi.pixelator.util.meta.Direction;

public enum Config {

    // Global config
    FULLSCREEN(ConfigMode.GLOBAL, ConfigType.BOOLEAN, false),
    PALETTE_MAX_COLORS(ConfigMode.GLOBAL, ConfigType.INT, 128),
    PROJECT_DIRECTORY(ConfigMode.GLOBAL, ConfigType.STRING, ""),
    RECENT_PROJECTS(ConfigMode.GLOBAL, ConfigType.OBJECT, RecentProjectsConfig.class, new RecentProjectsConfig()),
    SCREEN_HEIGHT(ConfigMode.GLOBAL, ConfigType.DOUBLE, 400d),
    SCREEN_WIDTH(ConfigMode.GLOBAL, ConfigType.DOUBLE, 600d),
    SCREEN_X(ConfigMode.GLOBAL, ConfigType.DOUBLE, 0d),
    SCREEN_Y(ConfigMode.GLOBAL, ConfigType.DOUBLE, 0d),
    THEME(ConfigMode.GLOBAL, ConfigType.STRING, Theme.BRIGHT.name()),

    // Project config
    ALL_FRAMES(ConfigMode.PROJECT, ConfigType.BOOLEAN, true),
    ALPHA_ONLY(ConfigMode.PROJECT, ConfigType.BOOLEAN, false),
    BULGE(ConfigMode.PROJECT, ConfigType.INT, 0),
    COLOR(ConfigMode.PROJECT, ConfigType.STRING),
    CROSSHAIR_COLOR(ConfigMode.PROJECT, ConfigType.STRING, "#00000080"),
    DEFAULT_PREVIEW_ZOOM_LEVEL(ConfigMode.PROJECT, ConfigType.DOUBLE, 1),
    FILL_SHAPE(ConfigMode.PROJECT, ConfigType.BOOLEAN, false),
    GRID_COLOR(ConfigMode.PROJECT, ConfigType.STRING, "#00000080"),
    GRID_CONFIG(ConfigMode.PROJECT, ConfigType.OBJECT, GridConfig.class, GridConfig.getDefault()),
    IMAGE_BACKGROUND_COLOR(ConfigMode.PROJECT, ConfigType.STRING, "#DDDDDD"),
    IMAGE_BORDER_COLOR(ConfigMode.PROJECT, ConfigType.STRING, "#00000000"),
    IMAGE_DIRECTORY(ConfigMode.PROJECT, ConfigType.STRING, ""),
    NEW_IMAGE_HEIGHT(ConfigMode.PROJECT, ConfigType.INT, 32),
    NEW_IMAGE_WIDTH(ConfigMode.PROJECT, ConfigType.INT, 32),
    OPENED_IMAGES(ConfigMode.PROJECT, ConfigType.OBJECT, OpenedImagesConfig.class, new OpenedImagesConfig()),
    OPENED_PALETTES(ConfigMode.PROJECT, ConfigType.OBJECT, OpenedPalettesConfig.class, new OpenedPalettesConfig()),
    PALETTE_DIRECTORY(ConfigMode.PROJECT, ConfigType.STRING, ""),
    REPLACE(ConfigMode.PROJECT, ConfigType.BOOLEAN, false),
    RESIZE_BIAS(ConfigMode.PROJECT, ConfigType.STRING, Direction.NONE.name()),
    RESIZE_KEEP_RATIO(ConfigMode.PROJECT, ConfigType.BOOLEAN, true),
    ROTATE_DEGREES(ConfigMode.PROJECT, ConfigType.INT, 0),
    STRETCH_KEEP_RATIO(ConfigMode.PROJECT, ConfigType.BOOLEAN, true),
    THICKNESS(ConfigMode.PROJECT, ConfigType.INT, 1),
    TOLERANCE(ConfigMode.PROJECT, ConfigType.INT, 0),
    TOOL(ConfigMode.PROJECT, ConfigType.INT, 0),

    // Image config
    FRAME_INDEX(ConfigMode.IMAGE, ConfigType.INT, 0),
    GRID_SELECTION(ConfigMode.IMAGE, ConfigType.OBJECT, GridSelectionConfig.class, new GridSelectionConfig(false, 1, 1)),
    IMAGE_H_SCROLL(ConfigMode.IMAGE, ConfigType.DOUBLE),
    IMAGE_HEIGHT(ConfigMode.IMAGE, ConfigType.DOUBLE),
    IMAGE_V_SCROLL(ConfigMode.IMAGE, ConfigType.DOUBLE),
    IMAGE_WIDTH(ConfigMode.IMAGE, ConfigType.DOUBLE),
    IMAGE_X(ConfigMode.IMAGE, ConfigType.DOUBLE),
    IMAGE_Y(ConfigMode.IMAGE, ConfigType.DOUBLE),
    IMAGE_ZOOM_LEVEL(ConfigMode.IMAGE, ConfigType.DOUBLE),
    PREVIEW_ZOOM_LEVEL(ConfigMode.IMAGE, ConfigType.DOUBLE);

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

    public static <T extends ConfigObject> T toObject(String string, Class<T> c) {
        try {
            Constructor<T> constructor = c.getConstructor();
            T instance = constructor.newInstance();
            instance.build(string);
            return instance;
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
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
        return (T) get(ConfigType.OBJECT, def);
    }

    private Object get(ConfigType type, Object def) {
        Object result = null;
        if (ConfigMode.PROJECT.equals(mode) && Project.active()) {
            result = getProjectConfig(type, def);
        }
        if (result == null) {
            result = getGlobalConfig(type, def);
        }
        return result != null ? result : def;
    }

    private Object getProjectConfig(ConfigType type, Object def) {
        if (ConfigMode.IMAGE.equals(mode) || !type.equals(this.type)) {
            throw new UnsupportedOperationException();
        }
        String stringValue = Project.get().getConfig(name());
        if (stringValue == null) {
            return def;
        }
        return switch(type) {
            case BOOLEAN -> Boolean.valueOf(stringValue);
            case DOUBLE -> Double.valueOf(stringValue);
            case INT -> Integer.valueOf(stringValue);
            case STRING -> stringValue;
            case OBJECT -> toObject(stringValue, c);
        };
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
            case OBJECT -> {
                String stringValue = Preferences.userRoot().get(name(), null);
                yield stringValue == null ? null : toObject(stringValue, c);
            }
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
        String stringValue = switch(type) {
            case BOOLEAN -> Boolean.toString((boolean) value);
            case DOUBLE -> Double.toString((double) value);
            case INT -> Integer.toString((int) value);
            case STRING -> (String) value;
            case OBJECT -> ((ConfigObject) value).toConfig();
        };
        Project.get().putConfig(name(), stringValue);
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
        return (T) getImageConfig(file, ConfigType.OBJECT, def);
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
            case STRING -> stringValue;
            case OBJECT -> toObject(stringValue, c);
        };
    }

    public void putBoolean(PixelFile file, boolean value) {
        putImageConfig(file, ConfigType.BOOLEAN, value);
    }

    public void putDouble(PixelFile file, double value) {
        putImageConfig(file, ConfigType.DOUBLE, value);
    }

    public void putInt(PixelFile file, int value) {
        putImageConfig(file, ConfigType.INT, value);
    }

    public void putString(PixelFile file, String value) {
        putImageConfig(file, ConfigType.STRING, value);
    }

    public void putObject(PixelFile file, ConfigObject value) {
        putImageConfig(file, ConfigType.OBJECT, value);
    }

    private void putImageConfig(PixelFile file, ConfigType configType, Object value) {
        if (ConfigMode.GLOBAL.equals(mode) || !configType.equals(this.type)) {
            throw new UnsupportedOperationException();
        }
        file.getProperties().put(name(), String.valueOf(value));
    }

}
