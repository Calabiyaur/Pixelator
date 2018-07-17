package main.java.files.io;

import java.io.File;
import java.io.IOException;

import javafx.scene.image.Image;

import main.java.files.PixelFileBuilder;

public final class BasicImageReader extends PixelFileReader {

    @Override
    public PixelFileBuilder read(File file) throws IOException {
        Image image = new Image(file.toURI().toURL().toString());
        return new PixelFileBuilder(file, image);
    }

}
