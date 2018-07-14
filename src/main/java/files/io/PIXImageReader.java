package main.java.files.io;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import javafx.scene.image.Image;

import main.java.files.ImageFile;
import main.java.files.PixelFile;
import main.java.files.zip.ZipUtil;
import main.java.util.FileUtil;

public final class PIXImageReader extends PixelFileReader {

    @Override
    public PixelFile read(File file) throws IOException {
        File unzippedFile = new File(FileUtil.removeType(file.getPath()) + "_tmp_read");
        ZipUtil.unpack(file, unzippedFile);

        Image image = super.findImage(unzippedFile);
        PixelFile pixelFile = new ImageFile(file, image);

        Properties config = super.findProperties(unzippedFile);
        pixelFile.getProperties().putAll(config);

        FileUtil.deleteRecursive(unzippedFile);

        return pixelFile;
    }

}
