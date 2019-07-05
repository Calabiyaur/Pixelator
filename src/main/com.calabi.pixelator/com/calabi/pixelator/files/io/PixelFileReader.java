package com.calabi.pixelator.files.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.Properties;

import javafx.scene.image.Image;

import com.calabi.pixelator.files.Extension;
import com.calabi.pixelator.files.PixelFileBuilder;
import com.calabi.pixelator.logging.Logger;
import com.calabi.pixelator.util.FileUtil;

public abstract class PixelFileReader {

    public abstract PixelFileBuilder read(File file) throws IOException;

    Image findImage(File directory) throws MalformedURLException {
        for (File file : directory.listFiles()) {
            if (Extension.PNG.equals(FileUtil.getExtension(file))) {
                return new Image(file.toURI().toURL().toString());
            }
        }
        return null;
    }

    Properties findProperties(File directory) {
        Properties properties = new Properties();
        for (File file : directory.listFiles()) {
            if (file.getName().contains(".properties")) {
                try {
                    InputStream inputStream = new FileInputStream(file);
                    properties.load(inputStream);
                    inputStream.close();
                } catch (IOException e) {
                    Logger.error(e);
                }
            }
        }
        return properties;
    }

}
