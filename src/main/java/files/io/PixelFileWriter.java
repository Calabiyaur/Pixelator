package main.java.files.io;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

import javax.imageio.ImageIO;

import main.java.files.Extension;
import main.java.files.PixelFile;

public abstract class PixelFileWriter {

    public abstract void write(PixelFile pixelFile) throws IOException;

    void saveImage(Image image, File file) throws IOException {
        BufferedImage bImage = SwingFXUtils.fromFXImage(image, null);
        if (!ImageIO.write(bImage, Extension.PNG.name(), file)) {
            throw new IOException();
        }
    }

    boolean deleteRecursive(File file) {
        boolean success = true;
        if (file.isDirectory()) {
            for (File child : file.listFiles()) {
                success = success && deleteRecursive(child);
            }
        }
        return success && file.delete();
    }

}
