package main.java.res;

import java.util.prefs.Preferences;

import main.java.view.tool.Tools;

public enum Config {

    BLUE,
    FILL_SHAPE,
    FULLSCREEN,
    GREEN,
    HEIGHT,
    IMAGE_DIRECTORY,
    OPACITY,
    PALETTE_DIRECTORY,
    RED,
    REPLACE,
    WIDTH;

    public static Tools fromString(String string) {
        return Tools.valueOf(Tools.class, string);
    }

    public String toString() {
        return name();
    }

    public static void putDouble(Config key, double value) {
        Preferences.userRoot().putDouble(key.toString(), value);
    }

    public static double getDouble(Config key, double def) {
        return Preferences.userRoot().getDouble(key.toString(), def);
    }

    public static boolean getBoolean(Config key, boolean def) {
        return Preferences.userRoot().getBoolean(key.toString(), def);
    }

    public static void putBoolean(Config key, boolean value) {
        Preferences.userRoot().putBoolean(key.toString(), value);
    }

    public static String getString(Config key, String def) {
        return Preferences.userRoot().get(key.toString(), def);
    }

    public static void putString(Config key, String value) {
        Preferences.userRoot().put(key.toString(), value);
    }

}
