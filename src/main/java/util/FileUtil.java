package main.java.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import main.java.files.Extension;
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
            try {
                return Extension.valueOf(extension.toUpperCase());
            } catch (IllegalArgumentException e) {
                return null;
            }
        }
    }

    public static void deleteRecursive(File file) throws IOException {
        if (file.isDirectory()) {
            for (File child : file.listFiles()) {
                deleteRecursive(child);
            }
        }
        Files.delete(file.toPath());
    }

}
