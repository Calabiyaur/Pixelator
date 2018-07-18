package main.java.view;

import javafx.scene.layout.Region;

import main.java.meta.Direction;
import main.java.control.parent.BasicTab;
import main.java.control.parent.TabToggle;

public class ColorTab extends BasicTab {

    public ColorTab(Region content) {
        super(content);
    }

    @Override protected TabToggle createToggle() {
        return new ColorToggle();
    }

    private class ColorToggle extends TabToggle {

        public ColorToggle() {
            super(Direction.WEST);
        }

    }
}
