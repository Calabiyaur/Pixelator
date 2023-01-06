package com.calabi.pixelator.project;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

import com.calabi.pixelator.config.BuildInfo;
import com.calabi.pixelator.config.Config;
import com.calabi.pixelator.file.PaletteFile;
import com.calabi.pixelator.file.PixelFile;
import com.calabi.pixelator.main.ExceptionHandler;
import com.calabi.pixelator.main.Pixelator;
import com.calabi.pixelator.view.ColorView;
import com.calabi.pixelator.view.ToolView;
import com.calabi.pixelator.view.editor.IWC;

public final class Project {

    private static final String FOLDER_NAME = ".pix/";
    private static final String PROPERTIES_FILENAME = ".project";

    private static Project INSTANCE;

    private final File location;
    private final File propertiesFile;
    private final Properties properties;

    private OpenedImagesConfig openedImages;
    private OpenedPalettesConfig openedPalettes;

    public Project(File location) {
        this.location = location;
        this.propertiesFile = new File(location + "/" + FOLDER_NAME + PROPERTIES_FILENAME);
        this.properties = loadConfig();
    }

    public static Project get() {
        return INSTANCE;
    }

    public static void setSilently(Project project) {
        INSTANCE = project;
    }

    public static void set(Project project) {
        INSTANCE = project;

        if (project != null) {

            project.load();
            project.saveConfig();

            Pixelator.getPrimaryStage().setTitle(Pixelator.TITLE + " " + BuildInfo.getVersion() + " - " + project.getName());

        } else {
            Pixelator.getPrimaryStage().setTitle(Pixelator.TITLE + " " + BuildInfo.getVersion());
        }
    }

    public static boolean active() {
        return INSTANCE != null;
    }

    public String getConfig(String key) {
        return properties.getProperty(key);
    }

    public void putConfig(String key, String value) {
        properties.setProperty(key, value);
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

    private void load() {
        ToolView.get().reload();

        openedImages = Config.OPENED_IMAGES.getObject();
        for (PixelFile file : openedImages.getFiles()) {
            IWC.get().addImage(file);
        }

        openedPalettes = Config.OPENED_PALETTES.getObject();
        for (PaletteFile file : openedPalettes.getFiles()) {
            ColorView.getPaletteSelection().addPalette(file);
        }

        RecentProjectsConfig recentProjects = Config.RECENT_PROJECTS.getObject();
        recentProjects.getFiles().remove(location);
        recentProjects.getFiles().add(0, location);
        Config.RECENT_PROJECTS.putObject(recentProjects);
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

    public void addOpenedImage(PixelFile file) {
        openedImages.getFiles().add(file);
        Config.OPENED_IMAGES.putObject(openedImages);
    }

    public void removeOpenedImage(PixelFile file) {
        openedImages.getFiles().remove(file);
        Config.OPENED_IMAGES.putObject(openedImages);
    }

    public void addOpenedPalette(PaletteFile file) {
        openedPalettes.getFiles().add(file);
        Config.OPENED_PALETTES.putObject(openedPalettes);
    }

    public void removeOpenedPalette(PaletteFile file) {
        openedPalettes.getFiles().remove(file);
        Config.OPENED_PALETTES.putObject(openedPalettes);
    }

}
