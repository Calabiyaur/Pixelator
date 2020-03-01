package com.calabi.pixelator.files;

import java.io.File;
import java.util.Properties;

import javafx.scene.image.Image;
import javafx.util.Builder;

import com.calabi.pixelator.control.image.WritableImage;

public class PixelFileBuilder implements Builder<PixelFile> {

    private Category category;
    private File file;
    private WritableImage image;
    private Image preview;
    private Properties properties = new Properties();

    public PixelFileBuilder() {
    }

    public PixelFileBuilder category(Category category) {
        if (category == Category.IMAGE && image.isAnimated()) {
            category = Category.ANIMATION;
        }
        this.category = category;
        return this;
    }

    public PixelFileBuilder file(File file) {
        this.file = file;
        return this;
    }

    public PixelFileBuilder image(WritableImage image) {
        this.image = image;
        return this;
    }

    public PixelFileBuilder preview(Image preview) {
        this.preview = preview;
        return this;
    }

    public PixelFileBuilder properties(Properties properties) {
        this.properties = properties;
        return this;
    }

    public PixelFile build() {
        PixelFile pixelFile;
        if (!Category.PALETTE.equals(category)) {
            pixelFile = new ImageFile(file, image, category);
        } else {
            pixelFile = new PaletteFile(file, image);
            ((PaletteFile) pixelFile).setPreview(preview);
        }
        pixelFile.getProperties().putAll(properties);
        return pixelFile;
    }

}
