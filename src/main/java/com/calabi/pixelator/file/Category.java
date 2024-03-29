package com.calabi.pixelator.file;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javafx.stage.FileChooser;

import com.calabi.pixelator.config.Config;

public enum Category {

    ANIMATION(
            Config.IMAGE_DIRECTORY,
            new Filter[] {
                    new Filter(Extension.GIF)
            },
            new Filter[] {} // Not needed
    ),
    IMAGE(
            Config.IMAGE_DIRECTORY,
            new Filter[] {
                    new Filter(Extension.PNG),
                    new Filter(Extension.JPEG, Extension.JPG),
                    new Filter(Extension.GIF),
            },
            new Filter[] {
                    new Filter("Image files", Extension.GIF, Extension.JPG, Extension.JPEG, Extension.PNG),
                    new Filter("All", Extension.values())
            }
    ),
    PALETTE(
            Config.PALETTE_DIRECTORY,
            new Filter[] {
                    new Filter(Extension.PNG),
                    new Filter(Extension.JPEG, Extension.JPG)
            },
            new Filter[] {
                    new Filter("Palette files", Extension.JPG, Extension.JPEG, Extension.PNG),
                    new Filter("All", Extension.values())
            }
    ),
    PROJECT(
            Config.PROJECT_DIRECTORY,
            new Filter[] {
                    new Filter(Extension.PROJECT),
            },
            new Filter[] {
                    new Filter("Project files", Extension.PROJECT)
            }
    );

    private final Config config;
    private final List<FileChooser.ExtensionFilter> filtersToSave;
    private final List<FileChooser.ExtensionFilter> filtersToOpen;

    Category(Config config, Filter[] filtersToSave, Filter[] filtersToOpen) {
        this.config = config;
        this.filtersToSave = createExtensionFilters(filtersToSave);
        this.filtersToOpen = createExtensionFilters(filtersToOpen);
    }

    public File getDirectory() {
        File file = new File(config.getString());
        if (file.isDirectory()) {
            return file;
        } else {
            return File.listRoots()[0];
        }
    }

    public void setDirectory(String directory) {
        config.putString(directory);
    }

    public List<FileChooser.ExtensionFilter> getExtensionFiltersToSave() {
        return filtersToSave;
    }

    public List<FileChooser.ExtensionFilter> getExtensionFiltersToOpen() {
        return filtersToOpen;
    }

    public List<String> getExtensions() {
        List<String> result = new ArrayList<>();
        for (FileChooser.ExtensionFilter extensionFilter : getExtensionFiltersToOpen()) {
            result.addAll(extensionFilter.getExtensions());
        }
        return result;
    }

    private List<FileChooser.ExtensionFilter> createExtensionFilters(Filter[] filters) {
        List<FileChooser.ExtensionFilter> result = new ArrayList<>();
        for (Filter filter : filters) {
            result.add(new FileChooser.ExtensionFilter(filter.getName(), filter.getExtensions()));
        }
        return result;
    }

}
