package com.calabi.pixelator.file;

import java.io.File;

import com.calabi.pixelator.ui.image.WritableImage;
import com.calabi.pixelator.util.FileUtil;

public class ImageFile extends PixelFile {

    public ImageFile(File file, WritableImage image) {
        super(Category.IMAGE, file, image);
    }

    ImageFile(File file, WritableImage image, Category category) {
        super(category, file, image);
    }

    @Override
    String updateName(File file) {
        return file == null ? "New Image" : FileUtil.removeType(file.getName());
    }

}
