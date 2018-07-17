package main.java.files;

import java.io.File;
import java.util.Properties;

import javafx.scene.image.Image;
import javafx.util.Builder;

public class PixelFileBuilder implements Builder<PixelFile> {

    private Category category;
    private File file;
    private Image image;
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

    public void setCategory(Category category) {
        this.category = category;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    public PixelFile build() {
        PixelFile pixelFile;
        if (Category.IMAGE.equals(category)) {
            pixelFile = new ImageFile(file, image);
        } else {
            pixelFile = new PaletteFile(file, image);
        }
        pixelFile.getProperties().putAll(properties);
        return pixelFile;
    }

}
