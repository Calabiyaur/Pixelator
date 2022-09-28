package com.calabi.pixelator.res;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

import com.calabi.pixelator.main.ExceptionHandler;
import com.calabi.pixelator.main.Pixelator;

public final class Project {

    private static final String FOLDER_NAME = ".pix/";
    private static final String PROPERTIES_FILENAME = ".project";

    private static Project INSTANCE;

    private final File location;
    private final File propertiesFile;
    private final Properties properties;

    public Project(File location) {
        this.location = location;
        this.propertiesFile = new File(location + "/" + FOLDER_NAME + PROPERTIES_FILENAME);
        this.properties = loadConfig();
    }

    public static Project get() {
        return INSTANCE;
    }

    public static void set(Project project) {
        INSTANCE = project;
        INSTANCE.saveConfig();

        Pixelator.getPrimaryStage().setTitle(Pixelator.TITLE + " " + BuildInfo.getVersion() + " - " + project.getName());
    }

    public static boolean active() {
        return INSTANCE != null;
    }

    public Object getConfig(String key, ConfigType type) {
        String stringValue = properties.getProperty(key);
        if (stringValue == null) {
            return null;
        }

        return switch(type) {
            case BOOLEAN -> Boolean.valueOf(stringValue);
            case DOUBLE -> Double.valueOf(stringValue);
            case INT -> Integer.valueOf(stringValue);
            case STRING, OBJECT -> stringValue;
        };
    }

    public void putConfig(String key, ConfigType type, Object value) {
        properties.setProperty(key,
                switch(type) {
                    case BOOLEAN -> Boolean.toString((Boolean) value);
                    case DOUBLE -> Double.toString((Double) value);
                    case INT -> Integer.toString((Integer) value);
                    case STRING -> (String) value;
                    case OBJECT -> ((ConfigObject) value).toConfig();
                }
        );
        saveConfig();
    }

    public Properties readProperties(String filename) {
        File file = getFile(filename);

        return readProperties(file);
    }

    private Properties readProperties(File file) {
        Properties properties = new Properties();
        if (file.exists()) {
            try {
                properties.load(Files.newInputStream(file.toPath()));
            } catch (IOException e) {
                ExceptionHandler.handle(e);
            }
        }

        return properties;
    }

    public void writeProperties(String filename, Properties properties) {
        File file = getFile(filename);

        writeProperties(file, properties);
    }

    private void writeProperties(File file, Properties properties) {
        try {
            if (!file.exists()) {
                if (!file.getParentFile().exists() && !file.getParentFile().mkdirs()) {
                    throw new IOException("Failed to create directory: '" + file.getParentFile().getAbsolutePath() + "'");
                }
                if (!file.createNewFile()) {
                    throw new IOException("Failed to create file: '" + file.getAbsolutePath() + "'");
                }
            }

            properties.store(Files.newOutputStream(file.toPath()), "");

        } catch (IOException e) {
            ExceptionHandler.handle(e);
        }
    }

    public File getFile(String filename) {
        return new File(location, FOLDER_NAME + location.toPath().relativize(Path.of(filename)));
    }

    private Properties loadConfig() {
        return readProperties(propertiesFile);
    }

    private void saveConfig() {
        writeProperties(propertiesFile, properties);
    }

    public File getLocation() {
        return location;
    }

    public String getName() {
        return location.getAbsolutePath();
    }

}
