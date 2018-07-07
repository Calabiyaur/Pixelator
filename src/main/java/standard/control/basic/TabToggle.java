package main.java.standard.control.basic;

import javafx.scene.control.ToggleButton;

import main.java.standard.Direction;

public abstract class TabToggle extends ToggleButton {

    private Direction direction;

    public TabToggle(Direction direction) {
        setDirection(direction);
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

}
