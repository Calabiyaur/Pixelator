package main.java.files.io;

import java.io.File;
import java.io.IOException;

import javafx.scene.image.Image;

import main.java.files.ImageFile;
import main.java.files.PixelFile;

public final class BasicImageReader extends PixelFileReader {

    @Override
    public PixelFile read(File file) throws IOException {
        Image image = new Image(file.toURI().toURL().toString());
        return new ImageFile(file, image);
    }

}
