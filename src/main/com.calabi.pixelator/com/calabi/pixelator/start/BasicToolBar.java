package com.calabi.pixelator.start;

import javafx.scene.control.Button;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToolBar;

import com.calabi.pixelator.control.basic.ImageButton;
import com.calabi.pixelator.control.basic.ToggleImageButton;
import com.calabi.pixelator.res.Action;
import com.calabi.pixelator.res.Images;

public class BasicToolBar extends ToolBar {

    public Button addButton(Action key) {
        Button tool = new ImageButton(Images.getImageView(key));
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
