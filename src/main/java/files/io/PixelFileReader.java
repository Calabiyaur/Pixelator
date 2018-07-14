package main.java.files.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.Properties;

import javafx.scene.image.Image;

import main.java.files.Extension;
import main.java.files.PixelFile;
import main.java.logging.Logger;
import main.java.util.FileUtil;

public abstract class PixelFileReader {

    public abstract PixelFile read(File file) throws IOException;

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
                } catch (IOException e) {
                    Logger.error(e);
                }
            }
        }
        return properties;
    }

}
