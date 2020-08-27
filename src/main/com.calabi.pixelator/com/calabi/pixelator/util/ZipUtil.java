package com.calabi.pixelator.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import com.calabi.pixelator.files.FileException;
import com.calabi.pixelator.logging.Logger;

public final class ZipUtil {

    public static File unpack(File zipFile, String outputPath) throws IOException {
        if (!zipFile.exists()) {
            throw new IOException("File does not exist: " + zipFile.getAbsolutePath());
        }
        File dir = new File(outputPath);
        //create output directory if it doesn't exist
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                throw new FileException("Failed to create directory '" + outputPath + "'");
            }
        }
        FileInputStream fis;
        //buffer for read and write data to file
        byte[] buffer = new byte[1024];
        fis = new FileInputStream(zipFile);
        ZipInputStream zis = new ZipInputStream(fis);
        ZipEntry ze = zis.getNextEntry();
        while (ze != null) {
            String fileName = ze.getName();
            File newFile = new File(outputPath + File.separator + fileName);
            Logger.log("ZipUtil", "Unpacking", newFile.getAbsolutePath());
            //create directories for sub directories in zip
            File subDirectory = new File(newFile.getParent());
            if (!subDirectory.mkdirs()) {
                //throw new IOException("Failed to create directory '" + newFile.getParent() + "'");
            }
            FileOutputStream fos = new FileOutputStream(newFile);
            int len;
            while ((len = zis.read(buffer)) > 0) {
                fos.write(buffer, 0, len);
            }
            fos.close();
            //close this ZipEntry
            zis.closeEntry();
            ze = zis.getNextEntry();
        }
        //close last ZipEntry
        zis.closeEntry();
        zis.close();
        fis.close();

        return dir;
    }

    public static void pack(File file, String outputPath) throws IOException {
        List<String> filesListInDir = populateFilesList(file);
        //now zip files one by one
        //create ZipOutputStream to write to the zip file
        FileOutputStream fos = new FileOutputStream(outputPath);
        ZipOutputStream zos = new ZipOutputStream(fos);
        for (String filePath : filesListInDir) {
            Logger.log("ZipUtil", "Packing", filePath);
            //for ZipEntry we need to keep only relative file path, so we used substring on absolute path
            ZipEntry ze = new ZipEntry(filePath.substring(file.getAbsolutePath().length() + 1, filePath.length()));
            zos.putNextEntry(ze);
            //read the file and write to ZipOutputStream
            FileInputStream fis = new FileInputStream(filePath);
            byte[] buffer = new byte[1024];
            int len;
            while ((len = fis.read(buffer)) > 0) {
                zos.write(buffer, 0, len);
            }
            zos.closeEntry();
            fis.close();
        }
        zos.close();
        fos.close();
    }

    private static List<String> populateFilesList(File dir) throws IOException {
        List<String> filesList = new ArrayList<>();
        populate(dir, filesList);
        return filesList;
    }

    private static void populate(File dir, List<String> filesList) throws IOException {
        File[] files = dir.listFiles();
        for (File file : files) {
            if (file.isFile()) {
                filesList.add(file.getAbsolutePath());
            } else {
                populate(file, filesList);
            }
        }
    }

}
