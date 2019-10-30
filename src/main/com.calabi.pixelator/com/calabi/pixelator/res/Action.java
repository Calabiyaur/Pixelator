package com.calabi.pixelator.res;

import java.util.Set;

import javafx.scene.input.KeyCode;

import com.calabi.pixelator.util.CollectionUtil;

public enum Action {

    BACKGROUND("Show Background", true, true, false, KeyCode.B),
    CHANGE_PALETTE("Change Palette..."),
    CHANGE_PALETTE_PREVIEW("Change Preview..."),
    CLOSE("Close", true, false, false, KeyCode.F4),
    CLOSE_PALETTE("Close", true, false, true, KeyCode.F4),
    COPY("Copy", true, false, false, KeyCode.C),
    CREATE_FROM_CLIPBOARD("Create from clipboard"),
    CROP("Crop"),
    CROSSHAIR("Show Cross-Hair", true, true, false, KeyCode.C),
    CUT("Cut", true, false, false, KeyCode.X),
    DELETE("Delete", false, false, false, KeyCode.DELETE),
    DOWN("Down", false, false, false, KeyCode.DOWN),
    EDIT_PALETTE("Edit palette"),
    ESCAPE("Escape", false, false, false, KeyCode.ESCAPE),
    EXTRACT_PALETTE("Extract Palette", true, false, true, KeyCode.P),
    FIT_WINDOW("Fit window", false, false, false, KeyCode.F4),
    FLIP_HORIZONTALLY("Flip horizontally"),
    FLIP_VERTICALLY("Flip vertically"),
    GRID("Show Grid", true, true, false, KeyCode.G),
    INVERT("Invert"),
    INVERT_WITHIN_PALETTE("Invert within Palette"),
    LEFT("Left", false, false, false, KeyCode.LEFT),
    MINUS("Minus", false, false, false, KeyCode.MINUS),
    MOVE_IMAGE("Move Image..."),
    NEW("New...", true, false, false, KeyCode.N),
    NEW_PALETTE("New...", true, false, true, KeyCode.N),
    OPEN("Open...", true, false, false, KeyCode.O),
    OPEN_PALETTE("Open...", true, false, true, KeyCode.O),
    OUTLINE("Outline..."),
    P_DOWN("P down", false, false, true, KeyCode.DOWN),
    P_LEFT("P left", false, false, true, KeyCode.LEFT),
    P_RIGHT("P right", false, false, true, KeyCode.RIGHT),
    P_UP("P up", false, false, true, KeyCode.UP),
    PASTE("Paste", true, false, false, KeyCode.V),
    PLUS("Plus", false, false, false, KeyCode.PLUS),
    RANDOM_COLOR("Random color", false, false, false, KeyCode.R),
    REDO("Redo", true, false, false, KeyCode.Y),
    REDO_PALETTE("Redo palette", true, false, true, KeyCode.Y),
    RESIZE("Resize..."),
    RIGHT("Right", false, false, false, KeyCode.RIGHT),
    ROTATE_CLOCKWISE("Rotate clockwise"),
    ROTATE_COUNTER_CLOCKWISE("Rotate counter-clockwise"),
    SAVE("Save", true, false, false, KeyCode.S),
    SAVE_AS("Save As...", true, true, false, KeyCode.S),
    SAVE_PALETTE("", true, false, true, KeyCode.S),
    SELECT_ALL("Select All", true, false, false, KeyCode.A),
    STRETCH("Stretch..."),
    SWITCH_TAB("Switch tab", true, false, false, KeyCode.TAB),
    SWITCH_TAB_BACK("Switch tab back", true, true, false, KeyCode.TAB),
    UNDO("Undo", true, false, false, KeyCode.Z),
    UNDO_PALETTE("Undo palette", true, false, true, KeyCode.Z),
    UP("Up", false, false, false, KeyCode.UP);

    static Set<Action> BETA_ACTIONS = CollectionUtil.toSet();

    private String text;
    private boolean ctrl;
    private boolean shift;
    private boolean alt;
    private KeyCode key;

    Action(String text, boolean ctrl, boolean shift, boolean alt, KeyCode key) {
        this.text = text;
        this.ctrl = ctrl;
        this.shift = shift;
        this.alt = alt;
        this.key = key;
    }

    Action(String text) {
        this.text = text;
    }

    public static Action get(boolean ctrl, boolean shift, boolean alt, KeyCode key) {
        if (key == null) {
            return null;
        }
        for (Action action : Action.values()) {
            if (action.ctrl == ctrl
                    && action.shift == shift
                    && action.alt == alt
                    && action.key == key) {
                return action;
            }
        }
        return null;
    }

    public String getText() {
        return text;
    }

    public boolean isCtrl() {
        return ctrl;
    }

    public boolean isShift() {
        return shift;
    }

    public boolean isAlt() {
        return alt;
    }

    public KeyCode getKey() {
        return key;
    }
}

