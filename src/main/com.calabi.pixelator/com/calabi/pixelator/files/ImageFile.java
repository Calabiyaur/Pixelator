package com.calabi.pixelator.files;

import java.io.File;

import javafx.scene.image.Image;

import com.calabi.pixelator.util.FileUtil;

public class ImageFile extends PixelFile {

    public ImageFile(File file, Image image) {
        super(Category.IMAGE, file, image);
    }

    ImageFile(File file, Image image, Category category) {
        super(category, file, image);
    }

    @Override
    String updateName(File file) {
        return file == null ? "New Image" : FileUtil.removeType(file.getName());
    }

}
