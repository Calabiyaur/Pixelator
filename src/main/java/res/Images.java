package main.java.res;

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
    CHOOSE_COLOR,
    CLOSE,
    CLOSE_PALETTE(CLOSE.url),
    COPY,
    CROSSHAIR,
    CUT,
    ELLIPSE,
    FILL,
    FILL_COLOR,
    GRID,
    ICON("/images/icon256.png"),
    LINE,
    LOCK,
    LOCK_OPEN,
    NEW,
    NEW_PALETTE(NEW.url),
    OPEN,
    OPEN_PALETTE(OPEN.url),
    PASTE,
    PEN,
    PICK,
    POPUP,
    RECTANGLE,
    REDO,
    REDO_PALETTE(REDO.url),
    SAVE,
    SAVE_PALETTE(SAVE.url),
    SELECT,
    SELECTION_BORDER,
    SUBMIT,
    SWAP_COLOR,
    WAND,
    UNDO,
    UNDO_PALETTE(UNDO.url),
    USE_ELLIPSE,
    USE_FILL,
    USE_FILL_COLOR,
    USE_LINE,
    USE_PEN,
    USE_PICK,
    USE_RECTANGLE,
    USE_SELECT,
    USE_WAND;

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
            System.out.println("Image not found: " + url);
            return null;
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
