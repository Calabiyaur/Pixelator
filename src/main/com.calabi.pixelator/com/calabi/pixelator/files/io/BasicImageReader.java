package com.calabi.pixelator.files.io;

import java.io.File;
import java.io.IOException;

import javafx.scene.image.Image;

import com.calabi.pixelator.files.PixelFileBuilder;

public final class BasicImageReader extends PixelFileReader {

    @Override
    public PixelFileBuilder read(File file) throws IOException {
        Image image = new Image(file.toURI().toURL().toString());
        return new PixelFileBuilder(file, image);
    }

}
