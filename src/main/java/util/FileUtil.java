package main.java.util;

import java.io.File;

import javafx.scene.image.Image;

import main.java.files.Category;
import main.java.files.Extension;
import main.java.files.ImageFile;
import main.java.files.PaletteFile;
import main.java.files.PixelFile;
import org.apache.commons.lang3.StringUtils;

public class FileUtil {

    public static String removeType(String fileName) {
        return StringUtils.substringBefore(fileName, ".");
    }

    public static Extension getExtension(File file) {
        if (file == null) {
            return null;
        } else {
            String extension = StringUtils.substringAfter(file.getName(), ".");
            return Extension.valueOf(extension.toUpperCase());
        }
    }

    public static PixelFile createFile(Image image, File file) {
        if (Category.PALETTE.getExtensions().contains(getExtension(file).getSuffix())) {
            return createFile(Category.PALETTE, image, file);
        }
        return createFile(Category.IMAGE, image, file);
    }

    public static PixelFile createFile(Category category, Image image, File file) {
        switch(category) {
            case IMAGE:
                return new ImageFile(file, image);
            case PALETTE:
                return new PaletteFile(file, image);
            default:
                throw new IllegalStateException();
        }
    }

}
