package main.java.files.io;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javafx.embed.swing.SwingFXUtils;

import javax.imageio.ImageIO;

import main.java.files.PixelFile;

public class BasicImageWriter extends PixelFileWriter {

    public void write(PixelFile pixelFile) throws IOException {
        BufferedImage bImage = SwingFXUtils.fromFXImage(pixelFile.getImage(), null);
        if (!ImageIO.write(bImage, pixelFile.getExtension().name(), pixelFile.getFile())) {
            throw new IOException();
        }
    }

}
