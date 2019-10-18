package com.calabi.pixelator.files.io;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import javafx.scene.image.Image;

import com.calabi.pixelator.files.FileConfig;
import com.calabi.pixelator.files.PixelFileBuilder;
import com.calabi.pixelator.logging.Logger;
import com.calabi.pixelator.util.FileUtil;
import com.calabi.pixelator.util.ZipUtil;

public final class PIXImageReader extends PixelFileReader {

    @Override
    public PixelFileBuilder read(File file) throws IOException {
        File unzippedFile = ZipUtil.unpack(file, FileUtil.removeType(file.getPath()) + FileConfig.TEMP_READ);

        Image image = super.findImage(unzippedFile, FileConfig.NAME_IMAGE);
        PixelFileBuilder builder = new PixelFileBuilder(file, image);

        Image preview = super.findImage(unzippedFile, FileConfig.NAME_PREVIEW);
        if (preview != null) {
            builder.preview(preview);
        }

        Properties config = super.findProperties(unzippedFile);
        builder.properties(config);
        Logger.log("config", "load", file.getName() + ": " + config);

        FileUtil.deleteRecursive(unzippedFile);

        return builder;
    }

}
