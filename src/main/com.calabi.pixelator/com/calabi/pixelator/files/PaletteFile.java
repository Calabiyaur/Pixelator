package com.calabi.pixelator.files;

import java.io.File;

import javafx.scene.image.Image;

import com.calabi.pixelator.util.FileUtil;

public class PaletteFile extends PixelFile {

    private File preview;

    public PaletteFile(File file, Image palette) {
        super(Category.PALETTE, file, palette);
    }

    @Override
    String updateName(File file) {
        return file == null ? "New Palette" : FileUtil.removeType(file.getName());
    }

    public File getPreview() {
        return preview;
    }

    public void setPreview(File preview) {
        this.preview = preview;
    }
}
