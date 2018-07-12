package main.java.util;

import javafx.scene.input.GestureEvent;
import javafx.scene.input.InputEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.ScrollEvent;

public class EventUtil {

    public static Boolean isControlDown(InputEvent e) {
        if (e instanceof KeyEvent) {
            return ((KeyEvent) e).isControlDown();
        }
        if (e instanceof GestureEvent) {
            return ((GestureEvent) e).isControlDown();
        }
        return null;
    }

    public static Boolean isShiftDown(InputEvent e) {
        if (e instanceof KeyEvent) {
            return ((KeyEvent) e).isShiftDown();
        }
        if (e instanceof GestureEvent) {
            return ((GestureEvent) e).isShiftDown();
        }
        return null;
    }

    public static Boolean isAltDown(InputEvent e) {
        if (e instanceof KeyEvent) {
            return ((KeyEvent) e).isAltDown();
        }
        if (e instanceof GestureEvent) {
            return ((GestureEvent) e).isAltDown();
        }
        return null;
    }

    public static KeyCode getKey(InputEvent e) {
        if (e instanceof KeyEvent) {
            return ((KeyEvent) e).getCode();
        }
        return null;
    }

    public static Double getVScroll(InputEvent e) {
        if (e instanceof ScrollEvent) {
            return ((ScrollEvent) e).getDeltaY();
        }
        return null;
    }

    public static Double getHScroll(InputEvent e) {
        if (e instanceof ScrollEvent) {
            return ((ScrollEvent) e).getDeltaX();
        }
        return null;
    }

}
