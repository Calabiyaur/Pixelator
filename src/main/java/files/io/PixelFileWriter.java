package main.java.files.io;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Properties;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

import javax.imageio.ImageIO;

import main.java.files.Extension;
import main.java.files.PixelFile;

public abstract class PixelFileWriter {

    public abstract void write(PixelFile pixelFile) throws IOException;

    public abstract void writeConfig(PixelFile pixelFile) throws IOException;

    final void saveImage(Image image, File file) throws IOException {
        BufferedImage bImage = SwingFXUtils.fromFXImage(image, null);
        if (!ImageIO.write(bImage, Extension.PNG.name(), file)) {
            throw new IOException();
        }
    }

    File findConfig(File directory) {
        Properties properties = new Properties();
        for (File file : directory.listFiles()) {
            if (file.getName().contains(".properties")) {
                return file;
            }
        }
        return null;
    }

}
