package com.calabi.pixelator.view.dialog;

import javafx.application.Platform;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

import com.calabi.pixelator.control.basic.BasicCheckBox;
import com.calabi.pixelator.control.basic.BasicColorField;
import com.calabi.pixelator.res.Config;
import com.calabi.pixelator.view.ToolView;
import com.calabi.pixelator.view.editor.IWC;
import com.calabi.pixelator.view.editor.window.ImageWindow;

public class SettingsDialog extends BasicDialog {

    private BasicColorField imageBackgroundColor;
    private BasicColorField imageBorderColor;
    private BasicCheckBox replaceColorCheckbox;
    private CheckBox replaceColorLocalCheckbox;
    private BasicCheckBox fillShapeCheckbox;
    private CheckBox fillShapeLocalCheckbox;

    public SettingsDialog() {
        setTitle("Settings");
        setOkText("Apply");

        GridPane content = new GridPane();
        addContent(content, 0, 0);

        Label storeLocalText = new Label("Store per image?");
        GridPane.setMargin(storeLocalText, new Insets(0, 0, 0, 10));

        // CONFIGURABLE SETTINGS:

        // Image background color
        Color color = Color.valueOf(Config.IMAGE_BACKGROUND_COLOR.getString());
        imageBackgroundColor = new BasicColorField("Image background", color);
        imageBackgroundColor.setValue(color);

        // Image border color
        Color borderColor = Color.valueOf(Config.IMAGE_BORDER_COLOR.getString());
        imageBorderColor = new BasicColorField("Image border", borderColor);
        imageBorderColor.setValue(borderColor);

        // Replace color
        boolean replaceColor = Config.REPLACE.getBoolean();
        replaceColorCheckbox = new BasicCheckBox("Replace color", replaceColor);
        boolean replaceColorLocal = Config.REPLACE.isUserDefinedAsLocal();
        replaceColorLocalCheckbox = new CheckBox();
        replaceColorLocalCheckbox.setSelected(replaceColorLocal);
        replaceColorCheckbox.disableProperty().bind(replaceColorLocalCheckbox.selectedProperty());

        // Fill shape
        boolean fillShape = Config.FILL_SHAPE.getBoolean();
        fillShapeCheckbox = new BasicCheckBox("Fill shape", fillShape);
        boolean fillShapeLocal = Config.FILL_SHAPE.isUserDefinedAsLocal();
        fillShapeLocalCheckbox = new CheckBox();
        fillShapeLocalCheckbox.setSelected(fillShapeLocal);
        fillShapeCheckbox.disableProperty().bind(fillShapeLocalCheckbox.selectedProperty());

        // LAYOUT:

        // Add input fields to content
        content.add(storeLocalText, 2, 0);
        content.addRow(1, imageBackgroundColor.getFrontLabel(), imageBackgroundColor.getControlWrapper());
        content.addRow(2, imageBorderColor.getFrontLabel(), imageBorderColor.getControlWrapper());
        content.add(new Separator(), 0, 3, 3, 1);
        content.addRow(4, replaceColorCheckbox.getFrontLabel(), replaceColorCheckbox.getControlWrapper(),
                replaceColorLocalCheckbox);
        content.addRow(5, fillShapeCheckbox.getFrontLabel(), fillShapeCheckbox.getControlWrapper(),
                fillShapeLocalCheckbox);

        // Layout for specific objects
        for (Node child : content.getChildren()) {
            if (child instanceof CheckBox) {
                GridPane.setHalignment(child, HPos.CENTER);
            } else if (child instanceof Separator) {
                GridPane.setMargin(child, new Insets(6, 0, 6, 0));
            }
        }

        Platform.runLater(() -> content.setMinWidth(content.getWidth()));

        setOnOk(e -> {
            apply();
            close();
        });
    }

    private void apply() {
        // Get values from input fields
        Color color = imageBackgroundColor.getValue();
        Color borderColor = imageBorderColor.getValue();
        boolean replaceColor = replaceColorCheckbox.getValue();
        boolean replaceColorLocal = replaceColorLocalCheckbox.isSelected();
        boolean fillShape = fillShapeCheckbox.getValue();
        boolean fillShapeLocal = fillShapeLocalCheckbox.isSelected();

        // Update global config
        Config.IMAGE_BACKGROUND_COLOR.putString(color.toString());
        Config.IMAGE_BORDER_COLOR.putString(borderColor.toString());
        if (!replaceColorLocal) {
            ToolView.get().setReplaceColor(replaceColor);
        }
        Config.REPLACE.setUserDefinedAs(replaceColorLocal);
        if (!fillShapeLocal) {
            ToolView.get().setFillShape(fillShape);
        }
        Config.FILL_SHAPE.setUserDefinedAs(fillShapeLocal);

        // Update open windows
        for (ImageWindow imageWindow : IWC.get().imageWindows()) {
            imageWindow.getEditor().getImageBackground().setColor(color);
            imageWindow.getEditor().getImageBackground().setBorderColor(borderColor);
            imageWindow.getEditor().getImageBackground().refresh();
        }
    }

    @Override
    public void focus() {
        imageBackgroundColor.requestFocus();
    }
}
