package com.calabi.pixelator.files.io;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import javafx.scene.image.Image;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritablePixelFormat;

import com.calabi.pixelator.files.Extension;
import com.calabi.pixelator.files.PixelFile;
import com.calabi.pixelator.util.FileUtil;

public abstract class PixelFileWriter {

    public abstract void write(PixelFile pixelFile) throws IOException;

    public abstract void writeConfig(PixelFile pixelFile) throws IOException;

    final void saveImage(Image image, File file) throws IOException {
        int width = (int) image.getWidth();
        int height = (int) image.getHeight();
        PixelReader reader = image.getPixelReader();
        byte[] buffer = new byte[width * height * 4];
        WritablePixelFormat<ByteBuffer> format = PixelFormat.getByteBgraInstance();
        reader.getPixels(0, 0, width, height, format, buffer, 0, width * 4);
        BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(file));
        for (int count = 0; count < buffer.length; count += 4) {
            out.write(buffer[count + 2]);
            out.write(buffer[count + 1]);
            out.write(buffer[count]);
            out.write(buffer[count + 3]);
        }
        out.flush();
        out.close();
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
