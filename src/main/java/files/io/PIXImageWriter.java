package main.java.files.io;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import main.java.files.PixelFile;

public class PIXImageWriter extends BasicImageWriter {

    public void write(PixelFile pixelFile) throws IOException {
        File output = pixelFile.getFile();

        // Delete the old PIX file
        if (!output.delete()) {
            throw new IOException();
        }

        // Create parent folder
        File parentFolder = new File(output.getPath());
        if (!parentFolder.createNewFile()) {
            throw new IOException();
        }

        // Create image file
        File imageDirectory = new File(parentFolder.getPath() + File.pathSeparator + "image.png");
        super.saveImage(pixelFile.getImage(), imageDirectory);

        // Create config file
        File configDirectory = new File(parentFolder.getPath() + File.pathSeparator + "config.txt");
        FileOutputStream outputStream = new FileOutputStream(pixelFile.getProperties())
        // TODO: 12.07.2018

        // ZIP the parent folder
        // TODO: 12.07.2018
    }

}
