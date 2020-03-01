package com.calabi.pixelator.files.io;

import java.io.File;
import java.io.IOException;

import com.calabi.pixelator.control.image.WritableImage;
import com.calabi.pixelator.files.PixelFileBuilder;

public final class BasicImageReader extends PixelFileReader {

    @Override
    public PixelFileBuilder read(File file) throws IOException {
        WritableImage image = new WritableImage(file.toURI().toURL().toString());
        return new PixelFileBuilder().file(file).image(image);
    }

}
