package com.calabi.pixelator.file.io;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import com.calabi.pixelator.file.PixelFileBuilder;
import com.calabi.pixelator.res.Project;
import com.calabi.pixelator.ui.image.WritableImage;

public class PixelFileReader extends PixelFileHandler {

    public PixelFileBuilder read(File file) throws IOException {
        WritableImage image = new WritableImage(file.toURI().toURL().toString());
        return new PixelFileBuilder()
                .file(file)
                .preview(getPreview(file))
                .properties(getProperties(file))
                .image(image);
    }

    private WritableImage getPreview(File file) {
        if (!Project.active()) {
            return null;
        }

        // Read from project
        String path = getPreviewPath(file);
        File previewFile = Project.get().getFile(path);
        if (previewFile.exists()) {
            return new WritableImage(previewFile.getPath());
        } else {
            return null;
        }
    }

    private Properties getProperties(File file) {
        if (!Project.active()) {
            return new Properties();
        }

        // Read from project
        String path = getPropertiesPath(file);
        return Project.get().readProperties(path);
    }

}
