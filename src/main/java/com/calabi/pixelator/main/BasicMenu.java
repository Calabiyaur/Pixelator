package com.calabi.pixelator.main;

import javafx.beans.binding.BooleanExpression;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;

import com.calabi.pixelator.config.Action;
import com.calabi.pixelator.config.Images;

public class BasicMenu extends Menu {

    private static final String SPACE = "        ";

    public BasicMenu(String text) {
        super(text);
    }

    public MenuItem addItem(Action key, EventHandler<ActionEvent> event, BooleanExpression condition) {
        MenuItem item = addItem(key, event);
        item.disableProperty().bind(condition.not());
        return item;
    }

    public CheckMenuItem addCheckItem(Action key, EventHandler<ActionEvent> event, BooleanExpression condition) {
        CheckMenuItem item = new CheckMenuItem(key.getText(), createImageView(key));
        accelerate(key, item);
        register(key, item, event);
        item.disableProperty().bind(condition.not());
        return item;
    }

    public MenuItem addItem(Action key, EventHandler<ActionEvent> event) {
        MenuItem item = new MenuItem(key.getText(), createImageView(key));
        accelerate(key, item);
        register(key, item, event);
        return item;
    }

    private ImageView createImageView(Action key) {
        Images image = Images.get(key);
        if (image != null) {
            ImageView imageView = new ImageView(image.getImage());
            MainScene.themeProperty().addListener((ov, o, n) -> imageView.setImage(image.getImage()));
            return imageView;
        } else {
            return new ImageView();
        }
    }

    static void accelerate(Action key, MenuItem item) {
        if (key.getKey() != null) {
            KeyCodeCombination accelerator = new KeyCodeCombination(key.getKey(),
                    key.isShift() ? KeyCombination.ModifierValue.DOWN : KeyCombination.ModifierValue.UP,
                    key.isCtrl() ? KeyCombination.ModifierValue.DOWN : KeyCombination.ModifierValue.UP,
                    key.isAlt() ? KeyCombination.ModifierValue.DOWN : KeyCombination.ModifierValue.UP,
                    KeyCombination.ModifierValue.ANY, KeyCombination.ModifierValue.ANY);
            item.setAccelerator(accelerator);
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
