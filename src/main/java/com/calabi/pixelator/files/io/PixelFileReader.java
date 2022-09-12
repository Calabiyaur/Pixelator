package com.calabi.pixelator.files.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.EnumSet;
import java.util.Properties;
import java.util.Set;

import com.calabi.pixelator.files.Extension;
import com.calabi.pixelator.files.FileConfig;
import com.calabi.pixelator.files.PixelFileBuilder;
import com.calabi.pixelator.logging.Logger;
import com.calabi.pixelator.ui.image.WritableImage;
import com.calabi.pixelator.util.FileUtil;

public abstract class PixelFileReader {

    private static final Set<Extension> validExtensions = EnumSet.of(Extension.PNG, Extension.GIF);

    public abstract PixelFileBuilder read(File file) throws IOException;

    WritableImage findImage(File directory, String name) throws MalformedURLException {
        for (File file : directory.listFiles()) {
            if (name.equals(file.getName())
                    && validExtensions.contains(FileUtil.getExtension(file))) {
                return new WritableImage(file.toURI().toURL().toString());
            }
        }
        return null;
    }

    Properties findProperties(File directory) {
        Properties properties = new Properties();
        for (File file : directory.listFiles()) {
            if (FileConfig.NAME_PROPERTIES.equals(file.getName())) {
                try (InputStream inputStream = new FileInputStream(file)) {
                    properties.load(inputStream);
                } catch (IOException e) {
                    Logger.error(e);
                }
            }
        }
        return properties;
    }

}
