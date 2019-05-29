package main.java.files;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javafx.stage.FileChooser;

import main.java.res.Config;

public enum Category {

    IMAGE(
            Config.IMAGE_DIRECTORY,
            new Filter[] {
                    new Filter(Extension.GIF),
                    new Filter(Extension.JPEG, Extension.JPG),
                    new Filter(Extension.PNG),
                    new Filter(Extension.PAL),
                    new Filter(Extension.PALI),
                    new Filter(Extension.PIX)
            },
            new Filter[] {
                    new Filter("Image files",
                            Extension.GIF, Extension.JPG, Extension.JPEG, Extension.PNG, Extension.PIX),
                    new Filter("Palette files", Extension.PAL, Extension.PALI),
                    new Filter("All", Extension.values())
            }
    ),
    PALETTE(
            Config.PALETTE_DIRECTORY,
            new Filter[] {
                    new Filter(Extension.PAL),
                    new Filter(Extension.PALI),
            },
            new Filter[] {
                    new Filter("Palette files", Extension.PAL, Extension.PALI),
                    new Filter("All", Extension.values())
            }
    );

    private Config config;
    private List<FileChooser.ExtensionFilter> filtersToSave;
    private List<FileChooser.ExtensionFilter> filtersToOpen;

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
