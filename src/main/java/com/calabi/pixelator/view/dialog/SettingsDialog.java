package com.calabi.pixelator.view.dialog;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Separator;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

import com.calabi.pixelator.config.Config;
import com.calabi.pixelator.config.Theme;
import com.calabi.pixelator.main.MainScene;
import com.calabi.pixelator.ui.control.BasicColorField;
import com.calabi.pixelator.ui.control.BasicComboBox;
import com.calabi.pixelator.view.ToolView;
import com.calabi.pixelator.view.editor.IWC;
import com.calabi.pixelator.view.editor.window.ImageWindow;

public class SettingsDialog extends BasicDialog {

    private final BasicComboBox<Theme> themeField;
    private final BasicColorField backgroundColorField;
    private final BasicColorField borderColorField;
    private final BasicColorField gridColorField;
    private final BasicColorField crosshairColorField;

    public SettingsDialog() {
        super(330, 240);
        setTitle("Settings");
        setOkText("Apply");

        GridPane content = new GridPane();
        addContent(content, 0, 0);

        // CONFIGURABLE SETTINGS:

        // Theme
        Theme theme = Theme.valueOf(Config.THEME.getString());
        themeField = new BasicComboBox<>("Theme", Theme.values(), theme);
        themeField.setValue(theme);

        // Image background color
        Color backgroundColor = Color.valueOf(Config.IMAGE_BACKGROUND_COLOR.getString());
        backgroundColorField = new BasicColorField("Image background", backgroundColor);
        backgroundColorField.setValue(backgroundColor);

        // Image border color
        Color borderColor = Color.valueOf(Config.IMAGE_BORDER_COLOR.getString());
        borderColorField = new BasicColorField("Image border", borderColor);
        borderColorField.setValue(borderColor);

        // Grid color
        Color gridColor = Color.valueOf(Config.GRID_COLOR.getString());
        gridColorField = new BasicColorField("Grid color", gridColor);
        gridColorField.setValue(gridColor);

        // Crosshair color
        Color crosshairColor = Color.valueOf(Config.CROSSHAIR_COLOR.getString());
        crosshairColorField = new BasicColorField("Crosshair color", crosshairColor);
        crosshairColorField.setValue(crosshairColor);

        // LAYOUT:

        // Add input fields to content
        content.addRow(0, themeField.getFrontLabel(), themeField.getControlWrapper());
        content.add(new Separator(), 0, 1, 3, 1);
        content.addRow(2, backgroundColorField.getFrontLabel(), backgroundColorField.getControlWrapper());
        content.addRow(3, borderColorField.getFrontLabel(), borderColorField.getControlWrapper());
        content.add(new Separator(), 0, 4, 3, 1);
        content.addRow(5, gridColorField.getFrontLabel(), gridColorField.getControlWrapper());
        content.addRow(6, crosshairColorField.getFrontLabel(), crosshairColorField.getControlWrapper());

        // Layout separators
        for (Node child : content.getChildren()) {
            if (child instanceof Separator) {
                GridPane.setMargin(child, new Insets(6, 0, 6, 0));
                ((Separator) child).setPrefWidth(Integer.MAX_VALUE);
            }
        }

        setOnOk(e -> {
            apply();
            close();
        });
    }

    private void apply() {
        // Get values from input fields
        Theme theme = themeField.getValue();
        Color color = backgroundColorField.getValue();
        Color borderColor = borderColorField.getValue();
        Color gridColor = gridColorField.getValue();
        Color crosshairColor = crosshairColorField.getValue();

        // Update global config
        Config.THEME.putString(theme.name());
        Config.IMAGE_BACKGROUND_COLOR.putString(color.toString());
        Config.IMAGE_BORDER_COLOR.putString(borderColor.toString());
        Config.GRID_COLOR.putString(gridColor.toString());
        Config.CROSSHAIR_COLOR.putString(crosshairColor.toString());

        // Update application
        MainScene.setTheme(theme);

        ToolView.get().getPreviewBackground().setColor(color);
        ToolView.get().getPreviewBackground().setBorderColor(borderColor);
        ToolView.get().getPreviewBackground().refresh();

        // Update open windows
        for (ImageWindow imageWindow : IWC.get().imageWindows()) {
            imageWindow.getEditor().getImageBackground().setColor(color);
            imageWindow.getEditor().getImageBackground().setBorderColor(borderColor);
            imageWindow.getEditor().setGridColor(gridColor);
            imageWindow.getEditor().setCrosshairColor(crosshairColor);

            imageWindow.getEditor().getImageBackground().refresh();
        }
    }

    @Override
    public void focus() {
        backgroundColorField.requestFocus();
    }
}
