package com.calabi.pixelator.files.io;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import javafx.scene.image.Image;

import com.calabi.pixelator.files.PixelFileBuilder;
import com.calabi.pixelator.logging.Logger;
import com.calabi.pixelator.util.FileUtil;
import com.calabi.pixelator.util.ZipUtil;

public final class PIXImageReader extends PixelFileReader {

    @Override
    public PixelFileBuilder read(File file) throws IOException {
        File unzippedFile = ZipUtil.unpack(file, FileUtil.removeType(file.getPath()) + "_tmp_read");

        Image image = super.findImage(unzippedFile);
        PixelFileBuilder builder = new PixelFileBuilder(file, image);

        Properties config = super.findProperties(unzippedFile);
        builder.setProperties(config);
        Logger.log("config", "load", file.getName() + ": " + config);

        FileUtil.deleteRecursive(unzippedFile);

        return builder;
    }

}
