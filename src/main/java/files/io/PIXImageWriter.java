package main.java.files.io;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import main.java.files.PixelFile;
import main.java.files.zip.ZipUtil;
import main.java.logging.Logger;
import main.java.util.FileUtil;

public final class PIXImageWriter extends PixelFileWriter {

    @Override
    public void write(PixelFile pixelFile) throws IOException {
        File output = pixelFile.getFile();

        // Delete the old PIX file
        if (output.exists() && !output.delete()) {
            throw new IOException("Failed to delete old PIX file");
        }

        // Create parent folder
        String outputPath = FileUtil.removeType(output.getPath());
        File parentFolder = new File(outputPath + "_tmp_write");
        if (!parentFolder.mkdir()) {
            throw new IOException("Failed to create temporary PIX folder");
        }

        // Create image file
        File imageDirectory = new File(parentFolder.getPath() + File.separator + "image.png");
        if (!imageDirectory.createNewFile()) {
            throw new IOException("Failed to create image file");
        }
        super.saveImage(pixelFile.getImage(), imageDirectory);

        // Create config file
        Properties config = pixelFile.getProperties();
        File configDirectory = new File(parentFolder.getPath() + File.separator + "config.properties");
        if (!configDirectory.createNewFile()) {
            throw new IOException("Failed to create config file");
        }
        FileOutputStream outputStream = new FileOutputStream(configDirectory);
        config.store(outputStream, "");
        outputStream.close();
        Logger.log("config", "store", config);

        // Zip and then delete the parent folder
        File zipFile = new File(outputPath + ".pix");
        ZipUtil.pack(parentFolder, zipFile);
        if (!zipFile.exists()) {
            FileUtil.deleteRecursive(parentFolder);
        }
    }

    @Override
    public void writeConfig(PixelFile pixelFile) throws IOException {
        File zipFile = pixelFile.getFile();
        File temp = new File(FileUtil.removeType(zipFile.getPath()) + "_tmp_config");
        ZipUtil.unpack(zipFile, temp);
        File config = findConfig(temp);
        FileOutputStream outputStream = new FileOutputStream(config);
        pixelFile.getProperties().store(outputStream, "");
        outputStream.close();
        Logger.log("config", "store", pixelFile.getProperties());
        ZipUtil.pack(temp, zipFile);
        FileUtil.deleteRecursive(temp);
    }

}
