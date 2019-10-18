package com.calabi.pixelator.files;

import java.io.File;
import java.util.Properties;

import javafx.scene.image.Image;
import javafx.util.Builder;

public class PixelFileBuilder implements Builder<PixelFile> {

    private Category category;
    private File file;
    private Image image;
    private Image preview;
    private Properties properties = new Properties();

    public PixelFileBuilder() {
    }

    public PixelFileBuilder(Image image) {
        this.image = image;
    }

    public PixelFileBuilder(File file, Image image) {
        this.file = file;
        this.image = image;
    }

    public PixelFileBuilder category(Category category) {
        this.category = category;
        return this;
    }

    public void file(File file) {
        this.file = file;
    }

    public void image(Image image) {
        this.image = image;
    }

    public void preview(Image preview) {
        this.preview = preview;
    }

    public void properties(Properties properties) {
        this.properties = properties;
    }

    public PixelFile build() {
        PixelFile pixelFile;
        if (Category.IMAGE.equals(category)) {
            pixelFile = new ImageFile(file, image);
        } else {
            pixelFile = new PaletteFile(file, image);
            ((PaletteFile) pixelFile).setPreview(preview);
        }
        pixelFile.getProperties().putAll(properties);
        return pixelFile;
    }

}
