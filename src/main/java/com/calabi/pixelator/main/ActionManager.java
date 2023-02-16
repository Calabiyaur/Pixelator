package com.calabi.pixelator.main;

import java.util.HashMap;
import java.util.Map;

import javafx.beans.binding.BooleanBinding;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyEvent;

import com.calabi.pixelator.config.Action;
import com.calabi.pixelator.log.Logger;

public class ActionManager {

    private static final Map<Action, MenuItem> controlMap = new HashMap<>();
    private static final Map<Action, EventHandler<ActionEvent>> eventMap = new HashMap<>();

    private ActionManager() {
    }

    public static void fire(KeyEvent event) {
        fire(event, false);
    }

    public static void fire(KeyEvent event, boolean forceFire) {
        Action action = Action.get(event.isControlDown(), event.isShiftDown(), event.isAltDown(), event.getCode());
        if (action != null) {
            Logger.logEvent(event, action.name());

            MenuItem control = controlMap.get(action);
            if (control == null || control.getAccelerator() == null || forceFire || action.isSecondary(event.getCode())) {
                fire(action);
                event.consume();
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
        Logger.error("There is no action key '" + key + "'!");
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
