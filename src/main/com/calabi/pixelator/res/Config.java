package com.calabi.pixelator.res;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Properties;
import java.util.prefs.Preferences;

import com.calabi.pixelator.files.FileException;
import com.calabi.pixelator.files.PixelFile;
import com.calabi.pixelator.logging.Logger;
import com.calabi.pixelator.meta.Direction;
import com.calabi.pixelator.start.ExceptionHandler;

public enum Config {

    // Global config
    ALPHA_ONLY(ConfigMode.GLOBAL, ConfigType.BOOLEAN, false),
    BULGE(ConfigMode.GLOBAL, ConfigType.INT, 0),
    COLOR(ConfigMode.GLOBAL, ConfigType.STRING),
    CROSSHAIR_COLOR(ConfigMode.GLOBAL, ConfigType.STRING, "#00000080"),
    FILL_SHAPE(ConfigMode.GLOBAL, ConfigType.BOOLEAN, false),
    FULLSCREEN(ConfigMode.GLOBAL, ConfigType.BOOLEAN, false),
    GRID_COLOR(ConfigMode.GLOBAL, ConfigType.STRING, "#00000080"),
    GRID_CONFIG(ConfigMode.GLOBAL, ConfigType.OBJECT, GridConfig.class, GridConfig.getDefault()),
    IMAGE_BACKGROUND_COLOR(ConfigMode.GLOBAL, ConfigType.STRING, "#DDDDDD"),
    IMAGE_BORDER_COLOR(ConfigMode.GLOBAL, ConfigType.STRING, "#00000000"),
    IMAGE_DIRECTORY(ConfigMode.GLOBAL, ConfigType.STRING, ""),
    NEW_IMAGE_HEIGHT(ConfigMode.GLOBAL, ConfigType.INT, 32),
    NEW_IMAGE_WIDTH(ConfigMode.GLOBAL, ConfigType.INT, 32),
    PALETTE_DIRECTORY(ConfigMode.GLOBAL, ConfigType.STRING, ""),
    PALETTE_MAX_COLORS(ConfigMode.GLOBAL, ConfigType.INT, 128),
    REPLACE(ConfigMode.GLOBAL, ConfigType.BOOLEAN, false),
    RESIZE_BIAS(ConfigMode.GLOBAL, ConfigType.STRING, Direction.NONE.name()),
    RESIZE_KEEP_RATIO(ConfigMode.GLOBAL, ConfigType.BOOLEAN, true),
    ROTATE_DEGREES(ConfigMode.GLOBAL, ConfigType.INT, 0),
    SCREEN_HEIGHT(ConfigMode.GLOBAL, ConfigType.DOUBLE, 400d),
    SCREEN_WIDTH(ConfigMode.GLOBAL, ConfigType.DOUBLE, 600d),
    SCREEN_X(ConfigMode.GLOBAL, ConfigType.DOUBLE, 0d),
    SCREEN_Y(ConfigMode.GLOBAL, ConfigType.DOUBLE, 0d),
    STRETCH_KEEP_RATIO(ConfigMode.GLOBAL, ConfigType.BOOLEAN, true),
    THEME(ConfigMode.GLOBAL, ConfigType.STRING, Theme.BRIGHT.name()),
    THICKNESS(ConfigMode.GLOBAL, ConfigType.INT, 1),
    TOLERANCE(ConfigMode.GLOBAL, ConfigType.INT, 0),
    TOOL(ConfigMode.GLOBAL, ConfigType.INT, 0),

    // Local (image) config
    FRAME_INDEX(ConfigMode.IMAGE, ConfigType.INT, 0),
    GRID_SELECTION(ConfigMode.IMAGE, ConfigType.OBJECT, GridSelectionConfig.class, ""),
    IMAGE_H_SCROLL(ConfigMode.IMAGE, ConfigType.DOUBLE),
    IMAGE_HEIGHT(ConfigMode.IMAGE, ConfigType.DOUBLE),
    IMAGE_V_SCROLL(ConfigMode.IMAGE, ConfigType.DOUBLE),
    IMAGE_WIDTH(ConfigMode.IMAGE, ConfigType.DOUBLE),
    IMAGE_X(ConfigMode.IMAGE, ConfigType.DOUBLE),
    IMAGE_Y(ConfigMode.IMAGE, ConfigType.DOUBLE),
    IMAGE_ZOOM_LEVEL(ConfigMode.IMAGE, ConfigType.DOUBLE);

    private final ConfigMode configMode;
    private final ConfigType configType;
    private final Class<? extends ConfigObject> c;
    private Object def;

    Config(ConfigMode configMode, ConfigType configType) {
        this(configMode, configType, null);
    }

    Config(ConfigMode configMode, ConfigType configType, Object def) {
        this(configMode, configType, null, def);
    }

    Config(ConfigMode configMode, ConfigType configType, Class<? extends ConfigObject> c, Object def) {
        this.configMode = configMode;
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
        if (ConfigMode.IMAGE.equals(configMode) || !configType.equals(this.configType)) {
            throw new UnsupportedOperationException();
        }
        return switch(configType) {
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

    private void put(ConfigType configType, Object value) {
        if (ConfigMode.IMAGE.equals(configMode) || !configType.equals(this.configType)) {
            throw new UnsupportedOperationException();
        }
        switch(configType) {
            case BOOLEAN -> Preferences.userRoot().putBoolean(name(), (boolean) value);
            case DOUBLE -> Preferences.userRoot().putDouble(name(), (double) value);
            case INT -> Preferences.userRoot().putInt(name(), (int) value);
            case STRING -> Preferences.userRoot().put(name(), (String) value);
            case OBJECT -> Preferences.userRoot().put(name(), ((ConfigObject) value).toConfig());
        }
    }

    public boolean getBoolean(PixelFile file, boolean def) {
        return (boolean) get(file, ConfigType.BOOLEAN, def);
    }

    public boolean getBoolean(PixelFile file) {
        return (boolean) get(file, ConfigType.BOOLEAN, def);
    }

    public double getDouble(PixelFile file, double def) {
        return (double) get(file, ConfigType.DOUBLE, def);
    }

    public int getInt(PixelFile file) {
        return (int) get(file, ConfigType.INT, def);
    }

    public String getString(PixelFile file, String def) {
        return (String) get(file, ConfigType.STRING, def);
    }

    public <T extends ConfigObject> T getObject(PixelFile file) {
        String string = (String) get(file, ConfigType.OBJECT, def);
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

    private Object get(PixelFile file, ConfigType configType, Object def) {
        if (ConfigMode.GLOBAL.equals(configMode) || !configType.equals(this.configType)) {
            Logger.log(name() + "(" + configType.name() + ")");
            throw new UnsupportedOperationException();
        }
        String stringValue = file.getProperties().getProperty(name());
        if (stringValue == null) {
            stringValue = getLocalConfig(file.getName(), name());
        }
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
        if (ConfigMode.GLOBAL.equals(configMode) || !configType.equals(this.configType)) {
            throw new UnsupportedOperationException();
        } else if (file != null) {
            file.getProperties().put(name(), String.valueOf(value));
            putLocalConfig(file.getName(), name(), String.valueOf(value));
        }
    }

    private String getLocalConfig(String hash, String key) {
        String home = System.getProperty("user.home");
        String s = File.separator;
        File dir = new File(home + s + "AppData" + s + "Local" + s + "Pixelator" + s + "config");
        if (!dir.exists()) {
            return null;
        }

        File file = new File(dir.getPath() + s + hash);
        if (!file.exists() || !file.isFile()) {
            return null;
        }

        Properties properties = new Properties();
        try (InputStream inputStream = new FileInputStream(file)) {
            properties.load(inputStream);
        } catch (IOException e) {
            throw new FileException(e);
        }
        return properties.getProperty(key);
    }

    private void putLocalConfig(String hash, String key, String value) {
        String home = System.getProperty("user.home");
        String s = File.separator;
        File dir = new File(home + s + "AppData" + s + "Local" + s + "Pixelator" + s + "config");
        if (!dir.exists() && !dir.mkdirs()) {
            throw new FileException("Failed to create config directory");
        }

        File file = new File(dir.getPath() + s + hash);
        try {
            boolean preExisting;
            if ((preExisting = !file.exists()) && !file.createNewFile()) {
                throw new FileException("Failed to create local config");
            }

            Properties properties = new Properties();
            if (!preExisting) {
                try (InputStream inputStream = new FileInputStream(file)) {
                    properties.load(inputStream);
                }
            }
            properties.put(key, value);
            try (FileOutputStream outputStream = new FileOutputStream(file)) {
                properties.store(outputStream, "");
            }

        } catch (IOException e) {
            throw new FileException(e);
        }
    }

}