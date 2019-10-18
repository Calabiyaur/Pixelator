package com.calabi.pixelator.files;

import java.io.File;

import javafx.scene.image.Image;

import com.calabi.pixelator.util.FileUtil;

public class PaletteFile extends PixelFile {

    private Image preview;

    public PaletteFile(File file, Image palette) {
        super(Category.PALETTE, file, palette);
    }

    @Override
    String updateName(File file) {
        return file == null ? "New Palette" : FileUtil.removeType(file.getName());
    }

    public Image getPreview() {
        return preview;
    }

    public void setPreview(Image preview) {
        this.preview = preview;
    }
}
