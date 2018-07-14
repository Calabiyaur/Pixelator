package main.java.util;

import java.io.File;

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

    public static boolean deleteRecursive(File file) {
        boolean success = true;
        if (file.isDirectory()) {
            for (File child : file.listFiles()) {
                success = success && deleteRecursive(child);
            }
        }
        return success && file.delete();
    }

}
