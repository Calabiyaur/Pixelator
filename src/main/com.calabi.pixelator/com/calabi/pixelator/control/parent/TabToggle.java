package com.calabi.pixelator.control.parent;

import javafx.scene.Cursor;
import javafx.scene.control.ToggleButton;

import com.calabi.pixelator.meta.Direction;

public abstract class TabToggle extends ToggleButton {

    private Direction direction;
    private boolean closable;

    public TabToggle(Direction direction) {
        setDirection(direction);
        setOnMouseEntered(event -> setCursor(Cursor.DEFAULT));
        // Add close button (getChildren().add(...))
    }

    public final Direction getDirection() {
        return direction;
    }

    public final void setDirection(Direction direction) {
        if (!direction.isSimple()) {
            throw new IllegalArgumentException("Direction must be simple!");
        }
        this.direction = direction;
    }

    public boolean isClosable() {
        return closable;
    }

    public void setClosable(boolean closable) {
        this.closable = closable;
    }

}
