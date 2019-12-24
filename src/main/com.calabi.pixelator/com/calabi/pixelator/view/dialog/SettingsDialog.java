package com.calabi.pixelator.view.dialog;

import javafx.scene.paint.Color;

import com.calabi.pixelator.control.basic.BasicColorField;
import com.calabi.pixelator.res.Config;
import com.calabi.pixelator.view.editor.IWC;
import com.calabi.pixelator.view.editor.ImageWindow;

public class SettingsDialog extends BasicDialog {

    private BasicColorField imageBackgroundColor;
    private BasicColorField imageBorderColor;

    public SettingsDialog() {
        setTitle("Settings");
        setOkText("Apply");

        Color color = Color.valueOf(Config.IMAGE_BACKGROUND_COLOR.getString());
        imageBackgroundColor = new BasicColorField("Image background", color);
        imageBackgroundColor.setValue(color);

        Color borderColor = Color.valueOf(Config.IMAGE_BORDER_COLOR.getString());
        imageBorderColor = new BasicColorField("Image border", borderColor);
        imageBorderColor.setValue(borderColor);

        addContent(imageBackgroundColor, 0, 0);
        addContent(imageBorderColor, 0, 1);

        setOnOk(e -> apply());
    }

    private void apply() {
        Color color = imageBackgroundColor.getValue();
        Color borderColor = imageBorderColor.getValue();

        Config.IMAGE_BACKGROUND_COLOR.putString(color.toString());
        Config.IMAGE_BORDER_COLOR.putString(borderColor.toString());

        for (ImageWindow imageWindow : IWC.get().imageWindows()) {
            imageWindow.getEditor().getImageBackground().setColor(color);
            imageWindow.getEditor().getImageBackground().setBorderColor(borderColor);
            imageWindow.getEditor().getImageBackground().refresh();
        }

        close();
    }

    @Override
    public void focus() {
        imageBackgroundColor.requestFocus();
    }
}
