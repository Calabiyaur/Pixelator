package com.calabi.pixelator.files;

public enum Extension {

    GIF("GIF", "*.gif"),
    JPEG("JPEG", "*.jpeg"),
    JPG("JPG", "*.jpg"),
    PNG("PNG", "*.png"),
    PAL("Palette", "*.pal"),
    PALI("Palette image", "*.pali"),
    PIX("Pixelator", "*.pix");

    private final String notation;
    private final String suffix;

    Extension(String notation, String suffix) {
        this.notation = notation;
        this.suffix = suffix;
    }

    public String getNotation() {
        return notation;
    }

    public String getSuffix() {
        return suffix;
    }

}
