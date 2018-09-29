package main.java.control.basic;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.Skin;

public class BasicMenuBar extends MenuBar {

    public BasicMenuBar() {
    }

    public BasicMenuBar(Menu... menus) {
        super(menus);
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new CustomMenuBarSkin(this) {

        };
    }

}
