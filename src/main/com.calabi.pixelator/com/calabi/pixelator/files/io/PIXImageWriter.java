package com.calabi.pixelator.files.io;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import com.calabi.pixelator.files.PixelFile;
import com.calabi.pixelator.logging.Logger;
import com.calabi.pixelator.util.FileUtil;
import com.calabi.pixelator.util.ZipUtil;

public final class PIXImageWriter extends PixelFileWriter {

    @Override
    public void write(PixelFile pixelFile) throws IOException {
        File zipFile = pixelFile.getFile();
        String outputPath = FileUtil.removeType(zipFile.getPath());

        // Create temporal folder
        File temp = ZipUtil.unpack(zipFile, outputPath + "_tmp_write");

        // Create image file
        File imageDirectory = new File(temp.getPath() + File.separator + "image.png");
        if (!imageDirectory.exists() && !imageDirectory.createNewFile()) {
            throw new IOException("Failed to create image file");
        }
        super.saveImage(pixelFile.getImage(), imageDirectory);

        // Create config file
        Properties config = pixelFile.getProperties();
        File configDirectory = new File(temp.getPath() + File.separator + "config.properties");
        if (!configDirectory.exists() && !configDirectory.createNewFile()) {
            throw new IOException("Failed to create config file");
        }
        FileOutputStream outputStream = new FileOutputStream(configDirectory);
        config.store(outputStream, "");
        outputStream.close();
        Logger.log("config", "store", pixelFile.getFile().getName() + ": " + config);

        // Zip and then delete the temporal folder
        ZipUtil.pack(temp, zipFile.getPath());
        FileUtil.deleteRecursive(temp);
    }

    @Override
    public void writeConfig(PixelFile pixelFile) throws IOException {
        File zipFile = pixelFile.getFile();
        File temp = ZipUtil.unpack(zipFile, FileUtil.removeType(zipFile.getPath()) + "_tmp_config");
        File config = findConfig(temp);
        FileOutputStream outputStream = new FileOutputStream(config);
        pixelFile.getProperties().store(outputStream, "");
        outputStream.close();
        Logger.log("config", "store",
                pixelFile.getFile().getName() + ": " + pixelFile.getProperties());
        ZipUtil.pack(temp, zipFile.getPath());
        FileUtil.deleteRecursive(temp);
    }

}
