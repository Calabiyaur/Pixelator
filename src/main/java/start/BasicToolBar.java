package main.java.start;

import javafx.scene.control.Button;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToolBar;

import main.java.res.Images;
import main.java.standard.control.ImageButton;
import main.java.standard.control.ToggleImageButton;
import main.java.res.Action;

public class BasicToolBar extends ToolBar {

    public Button addButton(Action key, Images image) {
        Button tool = new ImageButton(image);
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
