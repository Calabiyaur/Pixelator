package main.java.standard;

import javafx.scene.layout.Region;

import main.java.standard.control.basic.BasicTab;
import main.java.standard.control.basic.TabToggle;

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
