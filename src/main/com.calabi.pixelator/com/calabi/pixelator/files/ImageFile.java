package com.calabi.pixelator.files;

import java.io.File;

import javafx.scene.image.Image;

public class ImageFile extends PixelFile {

    public ImageFile(File file, Image image) {
        super(Category.IMAGE, file, image);
    }

}
