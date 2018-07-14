package main.java.start;

import javafx.beans.binding.BooleanExpression;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;

import main.java.res.Images;

import main.java.res.Action;

public class BasicMenu extends Menu {

    private static String SPACE = "        ";

    public BasicMenu(String text) {
        super(text);
    }

    public MenuItem addItem(Action key, String text, EventHandler<ActionEvent> event, BooleanExpression condition) {
        MenuItem item = addItem(key, text, event);
        item.disableProperty().bind(condition.not());
        return item;
    }

    public CheckMenuItem addCheckItem(Action key, String text, EventHandler<ActionEvent> event,
            BooleanExpression condition) {
        CheckMenuItem item = new CheckMenuItem(text, Images.get(key));
        accelerate(key, item);
        register(key, item, event);
        item.disableProperty().bind(condition.not());
        return item;
    }

    public MenuItem addItem(Action key, String text, EventHandler<ActionEvent> event) {
        MenuItem item = new MenuItem(text, Images.get(key));
        accelerate(key, item);
        register(key, item, event);
        return item;
    }

    static void accelerate(Action key, MenuItem item) {
        if (key.getKey() != null) {
            //KeyCodeCombination accelerator = new KeyCodeCombination(key.getKey(),
            //        key.isShift() ? KeyCombination.ModifierValue.DOWN : KeyCombination.ModifierValue.UP,
            //        key.isCtrl() ? KeyCombination.ModifierValue.DOWN : KeyCombination.ModifierValue.UP,
            //        key.isAlt() ? KeyCombination.ModifierValue.DOWN : KeyCombination.ModifierValue.UP,
            //        KeyCombination.ModifierValue.ANY, KeyCombination.ModifierValue.ANY);
            //item.setAccelerator(accelerator);
            item.setText(item.getText() + SPACE);
        }
    }

    public void addSeparator() {
        getItems().add(new SeparatorMenuItem());
    }

    private void register(Action key, MenuItem item, EventHandler<ActionEvent> event) {
        getItems().add(item);
        item.setOnAction(e -> {
            if (!item.isDisable()) {
                event.handle(e);
            }
        });
        ActionManager.registerItem(key, item);
    }
}
