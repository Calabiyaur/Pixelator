package main.java.start;

import java.util.HashMap;
import java.util.Map;

import javafx.beans.binding.BooleanBinding;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import main.java.logging.Logger;

public class ActionManager {

    private static Map<Action, MenuItem> controlMap = new HashMap<>();
    private static Map<Action, EventHandler<ActionEvent>> eventMap = new HashMap<>();

    private ActionManager() {
    }

    public static void fire(KeyEvent event) {
        Action action = Action.get(event.isControlDown(), event.isShiftDown(), event.isAltDown(), event.getCode());
        if (action != null) {
            Logger.logEvent(event, action.name());
            fire(action);
        }
    }

    public static void fire(Action key) {
        MenuItem control = controlMap.get(key);
        if (control != null) {
            control.fire();
            return;
        }
        EventHandler<ActionEvent> event = eventMap.get(key);
        if (event != null) {
            event.handle(new ActionEvent());
            return;
        }
        throw new IllegalArgumentException("There is no action key '" + key + "'!");
    }

    public static void registerItem(Action key, MenuItem control) {
        controlMap.put(key, control);
    }

    public static void registerAction(Action key, EventHandler<ActionEvent> event) {
        eventMap.put(key, event);
    }

    public static BooleanBinding getCondition(Action key) {
        return controlMap.get(key).disableProperty().not();
    }

    public enum Action {
        CLOSE(true, false, false, KeyCode.F4),
        CLOSE_PALETTE(true, false, true, KeyCode.F4),
        COPY(true, false, false, KeyCode.C),
        CREATE_FROM_CLIPBOARD(),
        CROP(),
        CROSSHAIR(true, true, false, KeyCode.C),
        CUT(true, false, false, KeyCode.X),
        DELETE(false, false, false, KeyCode.DELETE),
        DOWN(false, false, false, KeyCode.DOWN),
        ESCAPE(false, false, false, KeyCode.ESCAPE),
        EXTRACT_PALETTE(true, false, true, KeyCode.P),
        FIT_WINDOW(false, false, false, KeyCode.F4),
        FLIP_HORIZONTALLY(),
        FLIP_VERTICALLY(),
        GRID(true, true, false, KeyCode.G),
        LEFT(false, false, false, KeyCode.LEFT),
        MINUS(false, false, false, KeyCode.MINUS),
        MOVE_IMAGE(),
        NEW(true, false, false, KeyCode.N),
        NEW_PALETTE(true, false, true, KeyCode.N),
        OPEN(true, false, false, KeyCode.O),
        OPEN_PALETTE(true, false, true, KeyCode.O),
        OUTLINE(),
        PASTE(true, false, false, KeyCode.V),
        PLUS(false, false, false, KeyCode.PLUS),
        REDO(true, false, false, KeyCode.Y),
        REDO_PALETTE(true, false, true, KeyCode.Y),
        RESIZE(),
        RIGHT(false, false, false, KeyCode.RIGHT),
        ROTATE_CLOCKWISE(),
        ROTATE_COUNTER_CLOCKWISE(),
        SAVE(true, false, false, KeyCode.S),
        SAVE_AS(true, true, false, KeyCode.S),
        SAVE_PALETTE(true, false, true, KeyCode.S),
        SELECT_ALL(true, false, false, KeyCode.A),
        STRETCH(),
        SWITCH_TAB(true, false, false, KeyCode.TAB),
        UNDO(true, false, false, KeyCode.Z),
        UNDO_PALETTE(true, false, true, KeyCode.Z),
        UP(false, false, false, KeyCode.UP);

        private boolean ctrl;
        private boolean shift;
        private boolean alt;
        private KeyCode key;

        Action(boolean ctrl, boolean shift, boolean alt, KeyCode key) {
            this.ctrl = ctrl;
            this.shift = shift;
            this.alt = alt;
            this.key = key;
        }

        Action() {
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

}
