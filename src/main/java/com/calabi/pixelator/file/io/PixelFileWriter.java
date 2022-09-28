package com.calabi.pixelator.file.io;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javafx.scene.image.PixelReader;

import javax.imageio.IIOException;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.stream.FileImageOutputStream;

import com.sun.javafx.tk.PlatformImage;

import com.calabi.pixelator.file.Extension;
import com.calabi.pixelator.file.FileException;
import com.calabi.pixelator.file.Metadata;
import com.calabi.pixelator.file.PaletteFile;
import com.calabi.pixelator.file.PixelFile;
import com.calabi.pixelator.res.Project;
import com.calabi.pixelator.ui.image.WritableImage;

public class PixelFileWriter extends PixelFileHandler {

    public void write(PixelFile pixelFile) throws IOException {
        saveImage(pixelFile.getImage(), pixelFile.getFile(), pixelFile.getMetaData());
        saveConfig(pixelFile);
    }

    public void saveConfig(PixelFile file) throws IOException {
        if (Project.active()) {
            String path = getPropertiesPath(file.getFile());
            Project.get().writeProperties(path, file.getProperties());
        }
    }

    public void writePreview(PaletteFile file) throws IOException {
        if (Project.active()) {
            WritableImage preview = file.getPreview();
            String path = getPreviewPath(new File(preview.getUrl()));
            File previewFile = Project.get().getFile(path);

            savePNG(preview, previewFile, null);
        }
    }

    private void saveImage(WritableImage image, File file, Metadata metadata) throws IOException {
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

        try {
            if (!ImageIO.write(bImage, Extension.PNG.name(), file)) {
                throw new FileException("Failed to write image: " + file.getName());
            }
        } catch (IIOException e) {
            throw new FileException("Failed to write image: " + file.getName(), e);
        }
    }

    private void saveGIF(WritableImage image, File file, Metadata metadata) throws IOException {

        FileImageOutputStream output = new FileImageOutputStream(file);
        BufferedImage[] buffered = convertToBufferedImageArray(image);

        // Prepare metadata
        ImageWriter writer = ImageIO.getImageWritersBySuffix("gif").next();
        ImageWriteParam imageWriteParam = writer.getDefaultWriteParam();

        if (metadata == null) {
            metadata = Metadata.DEFAULT;
        }
        metadata.setDelayTime(image.getDelay());
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

}
