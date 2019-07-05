package com.calabi.pixelator.res;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public enum Images {

    ARROW_C,
    ARROW_E,
    ARROW_NE,
    ARROW_N,
    ARROW_NW,
    ARROW_W,
    ARROW_SW,
    ARROW_S,
    ARROW_SE,
    ASTERISK,
    BACKGROUND,
    CHECKERS,
    CHOOSE_COLOR,
    CLOSE,
    CLOSE_SMALL,
    COPY,
    CROSSHAIR,
    CUT,
    ELLIPSE,
    ERROR_20,
    FILL,
    FILL_COLOR,
    FILL_SELECT,
    GRID,
    ICON("/images/icon256.png"),
    LINE,
    LOCK,
    LOCK_OPEN,
    NEW,
    OPEN,
    PASTE,
    PEN,
    PICK,
    POPUP,
    RECTANGLE,
    REDO,
    SAVE,
    SELECT,
    SUBMIT,
    SWAP_COLOR,
    UNDO,
    USE_ELLIPSE,
    USE_FILL,
    USE_FILL_COLOR,
    USE_FILL_SELECT,
    USE_LINE,
    USE_PEN,
    USE_PICK,
    USE_RECTANGLE,
    USE_SELECT,
    USE_WAND,
    WAND;

    private final static String DIR = "/images/";
    private final static String TYPE = ".png";
    private String url;

    Images(String url) {
        this.url = url;
    }

    Images() {
        this.url = DIR + name().toLowerCase() + TYPE;
    }

    public String getUrl() {
        return url;
    }

    public Image getImage() {
        return Images.get(getUrl());
    }

    public static Image get(String url) {
        try {
            return new Image(url);
        } catch (IllegalArgumentException e) {
            if (ERROR_20.url.equals(url)) {
                System.out.println("Image not found: " + url);
                return null;
            } else {
                return get(ERROR_20.url);
            }
        }
    }

    public static ImageView get(Action action) {
        ImageView imageView = new ImageView();
        try {
            imageView.setImage(Images.valueOf(action.name()).getImage());
        } catch (IllegalArgumentException e) {
            // Do nothing.
        }
        return imageView;
    }

}
