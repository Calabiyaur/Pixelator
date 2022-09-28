package com.calabi.pixelator.file.io;

import java.io.File;

public abstract class PixelFileHandler {

    String getPreviewPath(File file) {
        return file.getPath().replaceAll("\\..*", ".preview");
    }

    String getPropertiesPath(File file) {
        return file.getPath().replaceAll("\\..*", ".properties");
    }

}
