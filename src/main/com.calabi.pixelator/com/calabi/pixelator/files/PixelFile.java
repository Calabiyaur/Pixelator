package com.calabi.pixelator.files;

import java.io.File;
import java.util.List;
import java.util.Properties;

import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;

import com.calabi.pixelator.util.Check;
import com.calabi.pixelator.util.FileUtil;

public abstract class PixelFile {

    private final Category category;
    private File file;
    private Image image; //TODO: Delete this and leave it up to the subclass to define how the data should be stored
    private StringProperty name = new SimpleStringProperty();
    private Extension extension;
    private Properties properties = new Properties();

    public PixelFile(Category category, File file, Image image) {
        Check.notNull(category);
        Check.notNull(image);

        this.category = category;
        setFile(file);
        this.image = image;
    }

    public final void setFile(File file) {
        this.file = file;
        this.name.set(updateName(file));
        this.extension = FileUtil.getExtension(file);
    }

    abstract String updateName(File file);

    public final Category getCategory() {
        return category;
    }

    public final File getFile() {
        return file;
    }

    public final Image getImage() {
        return image;
    }

    public final void setImage(Image image) {
        this.image = image;
    }

    public final boolean isNew() {
        return file == null;
    }

    public final String getName() {
        return name.get();
    }

    public ReadOnlyStringProperty nameProperty() {
        return name;
    }

    public final Extension getExtension() {
        return extension;
    }

    public final FileChooser.ExtensionFilter getUsedFilter(List<FileChooser.ExtensionFilter> filters) {
        if (extension == null) {
            return null;
        }
        for (FileChooser.ExtensionFilter filter : filters) {
            for (String extension : filter.getExtensions()) {
                if (extension.equals(this.extension.getSuffix())) {
                    return filter;
                }
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return String.format("'%s' (path: %s)", name, file.toString());
    }

    public Properties getProperties() {
        return properties;
    }

}