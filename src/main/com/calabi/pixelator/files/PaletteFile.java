package com.calabi.pixelator.files;

import java.io.File;

import com.calabi.pixelator.ui.image.WritableImage;
import com.calabi.pixelator.util.FileUtil;

public class PaletteFile extends PixelFile {

    private WritableImage preview;

    public PaletteFile(File file, WritableImage palette) {
        super(Category.PALETTE, file, palette);
    }

    @Override
    String updateName(File file) {
        return file == null ? "New Palette" : FileUtil.removeType(file.getName());
    }

    public WritableImage getPreview() {
        return preview;
    }

    public void setPreview(WritableImage preview) {
        this.preview = preview;
    }
}
