package com.calabi.pixelator.files.io;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;

import javax.imageio.ImageIO;

import com.calabi.pixelator.files.Extension;
import com.calabi.pixelator.files.FileConfig;
import com.calabi.pixelator.files.PixelFile;
import com.calabi.pixelator.util.FileUtil;

public abstract class PixelFileWriter {

    public abstract void write(PixelFile pixelFile) throws IOException;

    public abstract void writeConfig(PixelFile pixelFile) throws IOException;

    final void saveImage(Image image, File file) throws IOException {
        int width = (int) image.getWidth();
        int height = (int) image.getHeight();
        PixelReader reader = image.getPixelReader();
        BufferedImage bImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int argb = reader.getArgb(i, j);
                bImage.setRGB(i, j, argb);
            }
        }

        if (!ImageIO.write(bImage, Extension.PNG.name(), file)) {
            throw new IOException("Failed to write image: " + bImage.toString());
        }
    }

    File findImage(File directory) {
        for (File file : directory.listFiles()) {
            if (Extension.PNG.equals(FileUtil.getExtension(file))) {
                return file;
            }
        }
        return null;
    }

    File findConfig(File directory) {
        for (File file : directory.listFiles()) {
            if (FileConfig.NAME_PROPERTIES.equals(file.getName())) {
                return file;
            }
        }
        return null;
    }

}
