package main.java.files.io;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import javafx.scene.image.Image;

import main.java.files.PixelFileBuilder;
import main.java.files.zip.ZipUtil;
import main.java.logging.Logger;
import main.java.util.FileUtil;

public final class PIXImageReader extends PixelFileReader {

    @Override
    public PixelFileBuilder read(File file) throws IOException {
        File unzippedFile = new File(FileUtil.removeType(file.getPath()) + "_tmp_read");
        ZipUtil.unpack(file, unzippedFile);

        Image image = super.findImage(unzippedFile);
        PixelFileBuilder builder = new PixelFileBuilder(file, image);

        Properties config = super.findProperties(unzippedFile);
        builder.setProperties(config);
        Logger.log("config", "load", file.getName() + ": " + config);

        FileUtil.deleteRecursive(unzippedFile);

        return builder;
    }

}
