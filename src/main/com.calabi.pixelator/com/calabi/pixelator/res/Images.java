package com.calabi.pixelator.res;

import java.net.URISyntaxException;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import com.calabi.pixelator.logging.Logger;

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
    BETA,
    BULGE_CENTER,
    BULGE_LEFT,
    BULGE_RIGHT,
    CHECKERS,
    CHOOSE_COLOR,
    CLOSE,
    CLOSE_ALL("close"),
    CLOSE_BIG,
    COPY,
    CROSSHAIR,
    CUT,
    ELLIPSE,
    EDIT,
    ERROR_20,
    FILL,
    FILL_COLOR,
    FULL_SCREEN,
    SELECT_COLOR,
    GRID,
    ICON("icon256"),
    LINE,
    LOCK,
    LOCK_OPEN,
    NEW,
    NEXT_FRAME,
    OPEN,
    PALETTE,
    PASTE,
    PAUSE,
    PEN,
    PICK,
    PLAY,
    POPUP,
    PREVIOUS_FRAME,
    RECTANGLE,
    REDO,
    SAVE,
    SELECT,
    SETTINGS,
    SPINNER_UP,
    SPINNER_DOWN,
    SUBMIT,
    SWAP_COLOR,
    UNDO,
    USE_ELLIPSE,
    USE_FILL,
    USE_FILL_COLOR,
    USE_SELECT_COLOR,
    USE_SELECT_COLOR_ADD,
    USE_SELECT_COLOR_SUBTRACT,
    USE_LINE,
    USE_PEN,
    USE_PICK,
    USE_RECTANGLE,
    USE_SELECT,
    USE_SELECT_ADD,
    USE_SELECT_SUBTRACT,
    USE_WAND,
    USE_WAND_ADD,
    USE_WAND_SUBTRACT,
    WAND,
    ZOOM_IN,
    ZOOM_OUT,
    ZOOM_ZERO;

    private final static String DIR = "/images/";
    private final static String TYPE = ".png";
    private String url;

    Images(String url) {
        setUrl(url);
    }

    Images() {
        setUrl(name().toLowerCase());
    }

    private void setUrl(String url) {
        try {
            this.url = Images.class.getResource(DIR + url + TYPE).toURI().toString();
        } catch (URISyntaxException e) {
            Logger.error(e, name());
        }
    }

    public String getUrl() {
        return url;
    }

    public Image getImage() {
        return Images.get(getUrl());
    }

    public ImageView getImageView() {
        return new ImageView(Images.get(getUrl()));
    }

    public static Image get(String url) {
        try {
            return new Image(url);
        } catch (IllegalArgumentException e) {
            if (ERROR_20.getUrl().equals(url)) {
                System.out.println("Image not found: " + url);
                return null;
            } else {
                return get(ERROR_20.getUrl());
            }
        }
    }

    public static ImageView getImageView(Action action) {
        ImageView imageView = new ImageView();

        if (Action.BETA_ACTIONS.contains(action)) {
            imageView.setImage(Images.BETA.getImage());
        } else {
            try {
                imageView.setImage(Images.valueOf(action.name()).getImage());
            } catch (IllegalArgumentException e) {
                // Do nothing.
            }
        }
        return imageView;
    }

}
