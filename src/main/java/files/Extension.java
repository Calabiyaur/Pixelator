package main.java.files;

public enum Extension {

    GIF("GIF", "*.gif"),
    JPEG("JPEG", "*.jpeg"),
    JPG("JPG", "*.jpg"),
    PNG("PNG", "*.png"),
    PAL("Palette", "*.pal"),
    PALI("Palette image", "*.pali"),
    PIX("Pixelator", "*.pix");

    private String name;
    private String suffix;

    Extension(String name, String suffix) {
        this.name = name;
        this.suffix = suffix;
    }

    public String getName() {
        return name;
    }

    public String getSuffix() {
        return suffix;
    }

}
