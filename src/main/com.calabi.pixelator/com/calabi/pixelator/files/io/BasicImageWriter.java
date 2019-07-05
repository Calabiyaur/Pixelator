package com.calabi.pixelator.files.io;

import java.io.IOException;

import com.calabi.pixelator.files.PixelFile;

public final class BasicImageWriter extends PixelFileWriter {

    @Override
    public void write(PixelFile pixelFile) throws IOException {
        super.saveImage(pixelFile.getImage(), pixelFile.getFile());
    }

    @Override
    public void writeConfig(PixelFile pixelFile) {
        // Basic images do not have a config file.
    }
}
