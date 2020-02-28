package com.calabi.pixelator.files.io;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import com.calabi.pixelator.files.FileConfig;
import com.calabi.pixelator.files.PaletteFile;
import com.calabi.pixelator.files.PixelFile;
import com.calabi.pixelator.logging.Logger;
import com.calabi.pixelator.util.FileUtil;
import com.calabi.pixelator.util.ZipUtil;

public final class PIXImageWriter extends PixelFileWriter {

    @Override
    public void write(PixelFile pixelFile) throws IOException {
        File zipFile = pixelFile.getFile();
        boolean isNew;
        if ((isNew = !zipFile.exists()) && !zipFile.createNewFile()) {
            throw new IOException("Failed to create PIX file");
        }
        String outputPath = FileUtil.removeType(zipFile.getPath());

        // Create temporal folder
        File temp = ZipUtil.unpack(zipFile, outputPath + FileConfig.TEMP_WRITE);

        // Create image file
        File imageDirectory = new File(temp.getPath() + File.separator + FileConfig.NAME_IMAGE);
        if (!imageDirectory.exists() && !imageDirectory.createNewFile()) {
            throw new IOException("Failed to create image file");
        }
        super.saveImage(pixelFile.getImage(), imageDirectory);

        // Create preview file
        if (isNew && pixelFile instanceof PaletteFile && ((PaletteFile) pixelFile).getPreview() != null) {
            savePreviewFile((PaletteFile) pixelFile, temp);
        }

        // Create config file
        Properties config = pixelFile.getProperties();
        File configDirectory = new File(temp.getPath() + File.separator + FileConfig.NAME_PROPERTIES);
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
        if (!zipFile.exists() && !zipFile.createNewFile()) {
            throw new IOException("Failed to create PIX file");
        }
        File temp = ZipUtil.unpack(zipFile, FileUtil.removeType(zipFile.getPath()) + FileConfig.TEMP_CONFIG);
        File config = findConfig(temp);
        FileOutputStream outputStream = new FileOutputStream(config);
        pixelFile.getProperties().store(outputStream, "");
        outputStream.close();
        Logger.log("config", "store",
                pixelFile.getFile().getName() + ": " + pixelFile.getProperties());

        ZipUtil.pack(temp, zipFile.getPath());
        FileUtil.deleteRecursive(temp);
    }

    public void writePreview(PaletteFile paletteFile) throws IOException {
        File zipFile = paletteFile.getFile();
        String outputPath = FileUtil.removeType(zipFile.getPath());
        File temp = ZipUtil.unpack(zipFile, outputPath + FileConfig.TEMP_PREVIEW);

        if (paletteFile.getPreview() != null) {
            savePreviewFile(paletteFile, temp);
        }

        ZipUtil.pack(temp, zipFile.getPath());
        FileUtil.deleteRecursive(temp);
    }

    private void savePreviewFile(PaletteFile paletteFile, File temp) throws IOException {
        // Create preview image file
        File previewDirectory = new File(temp.getPath() + File.separator + FileConfig.NAME_PREVIEW);
        if (!previewDirectory.exists() && !previewDirectory.createNewFile()) {
            throw new IOException("Failed to create preview file");
        }
        super.saveImage(paletteFile.getPreview(), previewDirectory);
    }

}
