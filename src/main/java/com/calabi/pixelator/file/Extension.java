package com.calabi.pixelator.file;

public enum Extension {

    GIF("GIF", "*.gif"),
    JPEG("JPEG", "*.jpeg"),
    JPG("JPG", "*.jpg"),
    PNG("PNG", "*.png"),
    PROJECT("Project", ".project");

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
