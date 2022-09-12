package com.calabi.pixelator.files;

import java.io.File;
import java.util.Properties;

import javafx.util.Builder;

import com.calabi.pixelator.ui.image.WritableImage;
import com.calabi.pixelator.util.Check;

public class PixelFileBuilder implements Builder<PixelFile> {

    private Category category;
    private File file;
    private WritableImage image;
    private WritableImage preview;
    private Properties properties;
    private Metadata metadata;

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
        this.metadata = Metadata.read(file);
        return this;
    }

    public PixelFileBuilder image(WritableImage image) {
        this.image = image;
        return this;
    }

    public PixelFileBuilder preview(WritableImage preview) {
        this.preview = preview;
        return this;
    }

    public PixelFileBuilder properties(Properties properties) {
        this.properties = properties;
        return this;
    }

    public PixelFile build() {
        Check.notNull(category);
        Check.notNull(file);
        Check.notNull(image);

        PixelFile pixelFile;
        if (!Category.PALETTE.equals(category)) {
            pixelFile = new ImageFile(file, image, category);
        } else {
            pixelFile = new PaletteFile(file, image);
            ((PaletteFile) pixelFile).setPreview(preview);
        }
        if (properties != null) {
            pixelFile.getProperties().putAll(properties);
        }
        if (metadata != null) {
            pixelFile.setMetaData(metadata);
        }

        image.setFile(pixelFile);

        return pixelFile;
    }

}
