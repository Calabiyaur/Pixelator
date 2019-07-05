package com.calabi.pixelator.files.io;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

import javax.imageio.ImageIO;

import com.calabi.pixelator.files.Extension;
import com.calabi.pixelator.files.PixelFile;
import com.calabi.pixelator.util.FileUtil;

public abstract class PixelFileWriter {

    public abstract void write(PixelFile pixelFile) throws IOException;

    public abstract void writeConfig(PixelFile pixelFile) throws IOException;

    final void saveImage(Image image, File file) throws IOException {
        BufferedImage bImage = SwingFXUtils.fromFXImage(image, null);
        if (!ImageIO.write(bImage, Extension.PNG.name(), file)) {
            throw new IOException();
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
            if (file.getName().contains(".properties")) {
                return file;
            }
        }
        return null;
    }

}
