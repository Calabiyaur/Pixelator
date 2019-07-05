package com.calabi.pixelator.files.io;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javafx.embed.swing.SwingFXUtils;

import javax.imageio.ImageIO;

import com.calabi.pixelator.files.PixelFile;

public final class BasicImageWriter extends PixelFileWriter {

    @Override
    public void write(PixelFile pixelFile) throws IOException {
        BufferedImage bImage = SwingFXUtils.fromFXImage(pixelFile.getImage(), null);
        if (!ImageIO.write(bImage, pixelFile.getExtension().name(), pixelFile.getFile())) {
            throw new IOException();
        }
    }

    @Override
    public void writeConfig(PixelFile pixelFile) {
        // Basic images do not have a config file.
    }
}
