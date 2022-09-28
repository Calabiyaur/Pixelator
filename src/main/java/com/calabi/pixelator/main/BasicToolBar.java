package com.calabi.pixelator.main;

import javafx.scene.control.Button;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToolBar;

import com.calabi.pixelator.res.Action;
import com.calabi.pixelator.res.Images;
import com.calabi.pixelator.ui.control.ImageButton;
import com.calabi.pixelator.ui.control.ToggleImageButton;

public class BasicToolBar extends ToolBar {

    public Button addButton(Action key) {
        Button tool = new ImageButton(Images.get(key));
        tool.getStyleClass().setAll("tool-button");
        register(key, tool);
        return tool;
    }

    public Button addButton(Action key, Images image) {
        Button tool = new ImageButton(image);
        tool.getStyleClass().setAll("tool-button");
        register(key, tool);
        return tool;
    }

    public ToggleButton addToggle(Action key, Images image) {
        ToggleButton tool = new ToggleImageButton(image);
        register(key, tool);
        return tool;
    }

    private void register(Action key, ButtonBase tool) {
        getItems().add(tool);
        tool.disableProperty().bind(ActionManager.getCondition(key).not());
        tool.setOnAction(e -> ActionManager.fire(key));
    }
}
