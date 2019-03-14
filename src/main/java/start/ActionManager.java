package main.java.start;

import java.util.HashMap;
import java.util.Map;

import javafx.beans.binding.BooleanBinding;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyEvent;

import main.java.logging.Logger;
import main.java.res.Action;

public class ActionManager {

    private static Map<Action, MenuItem> controlMap = new HashMap<>();
    private static Map<Action, EventHandler<ActionEvent>> eventMap = new HashMap<>();

    private ActionManager() {
    }

    public static void fire(KeyEvent event) {
        Action action = Action.get(event.isControlDown(), event.isShiftDown(), event.isAltDown(), event.getCode());
        if (action != null) {
            Logger.logEvent(event, action.name());

            MenuItem control = controlMap.get(action);
            if (control == null || control.getAccelerator() == null) {
                fire(action);
            }
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

}
