package com.calabi.pixelator.res;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.prefs.Preferences;

import com.calabi.pixelator.files.PixelFile;
import com.calabi.pixelator.logging.Logger;
import com.calabi.pixelator.meta.Direction;
import com.calabi.pixelator.start.ExceptionHandler;

public enum Config {

    // Global config
    BULGE(ConfigMode.GLOBAL, ConfigType.INT, 0),
    COLOR(ConfigMode.GLOBAL, ConfigType.STRING),
    CONFIG_CONFIG(ConfigMode.GLOBAL, ConfigType.OBJECT, ConfigConfig.class, ConfigConfig.getDefault()),
    FULLSCREEN(ConfigMode.GLOBAL, ConfigType.BOOLEAN, false),
    GRID_CONFIG(ConfigMode.GLOBAL, ConfigType.OBJECT, GridConfig.class, GridConfig.getDefault()),
    HEIGHT(ConfigMode.GLOBAL, ConfigType.DOUBLE, 400d),
    IMAGE_DIRECTORY(ConfigMode.GLOBAL, ConfigType.STRING, ""),
    NEW_IMAGE_HEIGHT(ConfigMode.GLOBAL, ConfigType.INT, 32),
    NEW_IMAGE_WIDTH(ConfigMode.GLOBAL, ConfigType.INT, 32),
    PALETTE_DIRECTORY(ConfigMode.GLOBAL, ConfigType.STRING, ""),
    PALETTE_MAX_COLORS(ConfigMode.GLOBAL, ConfigType.INT, 128),
    RESIZE_BIAS(ConfigMode.GLOBAL, ConfigType.STRING, Direction.NONE.name()),
    RESIZE_KEEP_RATIO(ConfigMode.GLOBAL, ConfigType.BOOLEAN, true),
    STRETCH_KEEP_RATIO(ConfigMode.GLOBAL, ConfigType.BOOLEAN, true),
    WIDTH(ConfigMode.GLOBAL, ConfigType.DOUBLE, 600d),

    // Local config
    IMAGE_H_SCROLL(ConfigMode.IMAGE, ConfigType.DOUBLE),
    IMAGE_HEIGHT(ConfigMode.IMAGE, ConfigType.DOUBLE),
    IMAGE_V_SCROLL(ConfigMode.IMAGE, ConfigType.DOUBLE),
    IMAGE_WIDTH(ConfigMode.IMAGE, ConfigType.DOUBLE),
    IMAGE_X(ConfigMode.IMAGE, ConfigType.DOUBLE),
    IMAGE_Y(ConfigMode.IMAGE, ConfigType.DOUBLE),
    IMAGE_ZOOM_LEVEL(ConfigMode.IMAGE, ConfigType.DOUBLE),

    // User defined
    ALPHA_ONLY(ConfigMode.USER_DEFINED, ConfigType.BOOLEAN, false),
    FILL_SHAPE(ConfigMode.USER_DEFINED, ConfigType.BOOLEAN, false),
    IMAGE_BACKGROUND_COLOR(ConfigMode.USER_DEFINED, ConfigType.STRING, "#DDDDDD"),
    IMAGE_BORDER_COLOR(ConfigMode.USER_DEFINED, ConfigType.STRING, "#00000000"),
    REPLACE(ConfigMode.USER_DEFINED, ConfigType.BOOLEAN, false),
    THICKNESS(ConfigMode.USER_DEFINED, ConfigType.INT, 1);

    private final ConfigMode configMode;
    private final ConfigType configType;
    private Class<? extends ConfigObject> c;
    private Object def;

    Config(ConfigMode configMode, ConfigType configType) {
        this(configMode, configType, null);
    }

    Config(ConfigMode configMode, ConfigType configType, Object def) {
        this.configMode = configMode;
        this.configType = configType;
        this.def = def;
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
        } else if (isUserDefinedAsLocal()) {
            return def;
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
        } else if (isUserDefinedAsLocal()) {
            return;
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

    public boolean getBoolean(PixelFile file) {
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
        if (ConfigMode.GLOBAL.equals(configMode) || !configType.equals(this.configType)) {
            Logger.log(name() + "(" + configType.name() + ")");
            throw new UnsupportedOperationException();
        } else if (ConfigMode.USER_DEFINED.equals(configMode) && isUserDefinedAsGlobal()) {
            return get(configType, def);
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
        if (ConfigMode.GLOBAL.equals(configMode) || !configType.equals(this.configType)) {
            throw new UnsupportedOperationException();
        } else if (isUserDefinedAsGlobal()) {
            put(configType, value);
            return;
        }
        file.getProperties().put(name(), String.valueOf(value));
    }

    public boolean isUserDefinedAsGlobal() {
        if (!ConfigMode.USER_DEFINED.equals(configMode)) {
            return false;
        }
        ConfigConfig configConfig = Config.CONFIG_CONFIG.getObject();
        return !configConfig.getLocalConfigs().contains(this);
    }

    public boolean isUserDefinedAsLocal() {
        if (!ConfigMode.USER_DEFINED.equals(configMode)) {
            return false;
        }
        ConfigConfig configConfig = Config.CONFIG_CONFIG.getObject();
        return configConfig.getLocalConfigs().contains(this);
    }

    public void setUserDefinedAs(boolean local) {
        if (!ConfigMode.USER_DEFINED.equals(configMode)) {
            throw new IllegalStateException();
        }
        ConfigConfig configConfig = Config.CONFIG_CONFIG.getObject();
        if (local) {
            configConfig.getLocalConfigs().add(this);
        } else {
            configConfig.getLocalConfigs().remove(this);
        }
    }

}
