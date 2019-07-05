package com.calabi.pixelator.files;

import java.io.File;

import javafx.scene.image.Image;

public class PaletteFile extends PixelFile {

    public PaletteFile(File file, Image palette) {
        super(Category.PALETTE, file, palette);
    }

}
