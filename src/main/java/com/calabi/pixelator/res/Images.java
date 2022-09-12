package com.calabi.pixelator.res;

import javafx.scene.image.Image;

public enum Images {

    ADD_FRAME,
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
    CHECK,
    CHECKERS,
    CHOOSE_COLOR,
    CLOSE,
    CLOSE_ALL("close"),
    CLOSE_BIG,
    COPY,
    CROSSHAIR,
    CUT,
    DROP_ARROW_DOWN,
    DROP_ARROW_UP,
    ELLIPSE,
    EDIT,
    EDIT_PALETTE("edit"),
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
    NEW_PALETTE("new"),
    NEXT_FRAME,
    OPEN,
    OPEN_PALETTE("open"),
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
    REMOVE_FRAME,
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

    private final String effectiveName;
    private String theme = "";
    private String url;

    Images(String effectiveName) {
        this.effectiveName = effectiveName;
    }

    Images() {
        this.effectiveName = name().toLowerCase();
    }

    public String getUrl() {
        if (url == null || !theme.equalsIgnoreCase(Config.THEME.getString())) {
            theme = Config.THEME.getString().toLowerCase();
            String path = DIR + theme + "/" + effectiveName + TYPE;
            try {
                url = Images.class.getResource(path).toURI().toString();
            } catch (Exception e) {
                throw new RuntimeException("Could not find image '" + name() + "' at " + path, e);
            }
        }
        return url;
    }

    public Image getImage() {
        return Images.getImage(getUrl());
    }

    public static Image getImage(String url) {
        try {
            return new Image(url);
        } catch (IllegalArgumentException e) {
            if (ERROR_20.getUrl().equals(url)) {
                System.out.println("Image not found: " + url);
                return null;
            } else {
                return getImage(ERROR_20.getUrl());
            }
        }
    }

    public static Images get(Action action) {
        if (Action.BETA_ACTIONS.contains(action)) {
            return Images.BETA;
        } else {
            try {
                return Images.valueOf(action.name());
            } catch (IllegalArgumentException e) {
                return null;
            }
        }
    }

}
