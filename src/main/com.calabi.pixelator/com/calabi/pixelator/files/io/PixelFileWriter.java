package com.calabi.pixelator.files.io;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javafx.scene.image.PixelReader;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.stream.FileImageOutputStream;

import com.sun.javafx.tk.PlatformImage;

import com.calabi.pixelator.control.image.WritableImage;
import com.calabi.pixelator.files.Extension;
import com.calabi.pixelator.files.FileConfig;
import com.calabi.pixelator.files.Metadata;
import com.calabi.pixelator.files.PixelFile;
import com.calabi.pixelator.util.FileUtil;

public abstract class PixelFileWriter {

    public abstract void write(PixelFile pixelFile) throws IOException;

    public abstract void writeConfig(PixelFile pixelFile) throws IOException;

    final void saveImage(WritableImage image, File file, Metadata metadata) throws IOException {
        if (!image.isAnimated()) {
            savePNG(image, file, metadata);
        } else {
            saveGIF(image, file, metadata);
        }
    }

    private void savePNG(WritableImage image, File file, Metadata metadata) throws IOException {
        int width = (int) image.getWidth();
        int height = (int) image.getHeight();
        PixelReader reader = image.getPixelReader();
        BufferedImage bImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int argb = reader.getArgb(i, j);
                bImage.setRGB(i, j, argb); //TODO: Replace with single call?
            }
        }

        if (!ImageIO.write(bImage, Extension.PNG.name(), file)) {
            throw new IOException("Failed to write image: " + bImage.toString());
        }
    }

    private void saveGIF(WritableImage image, File file, Metadata metadata) throws IOException {

        FileImageOutputStream output = new FileImageOutputStream(file);
        BufferedImage[] buffered = convertToBufferedImageArray(image);

        // Prepare metadata
        ImageWriter writer = ImageIO.getImageWritersBySuffix("gif").next();
        ImageWriteParam imageWriteParam = writer.getDefaultWriteParam();

        IIOMetadata iioMetadata = Metadata.write(writer, metadata);

        writer.setOutput(output);

        writer.prepareWriteSequence(null);

        // Write frames
        for (BufferedImage nextImage : buffered) {
            writer.writeToSequence(new IIOImage(nextImage, null, iioMetadata), imageWriteParam);
        }

        writer.endWriteSequence();
        output.close();
    }

    private BufferedImage[] convertToBufferedImageArray(WritableImage image) {
        int width = (int) image.getWidth();
        int height = (int) image.getHeight();

        PlatformImage[] frames = image.getFrames();
        BufferedImage[] buffered = new BufferedImage[frames.length];

        for (int n = 0; n < frames.length; n++) {
            PlatformImage animFrame = frames[n];
            BufferedImage frame = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    frame.setRGB(i, j, animFrame.getArgb(i, j)); //TODO: Replace with single call?
                }
            }
            buffered[n] = frame;
        }

        return buffered;
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
