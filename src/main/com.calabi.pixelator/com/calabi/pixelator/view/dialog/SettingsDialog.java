package com.calabi.pixelator.view.dialog;

import javafx.scene.paint.Color;

import com.calabi.pixelator.control.basic.BasicColorField;
import com.calabi.pixelator.res.Config;
import com.calabi.pixelator.view.editor.IWC;
import com.calabi.pixelator.view.editor.ImageWindow;

public class SettingsDialog extends BasicDialog {

    private BasicColorField imageBackgroundColor;

    public SettingsDialog() {
        setTitle("Settings");
        setOkText("Apply");

        Color color = Color.valueOf(Config.IMAGE_BACKGROUND_COLOR.getString());
        imageBackgroundColor = new BasicColorField("Image background", color);
        imageBackgroundColor.setValue(color);

        addContent(imageBackgroundColor, 0, 0);

        setOnOk(e -> apply());
    }

    private void apply() {
        Color color = imageBackgroundColor.getValue();
        Config.IMAGE_BACKGROUND_COLOR.putString(color.toString());
        for (ImageWindow imageWindow : IWC.get().imageWindows()) {
            imageWindow.getEditor().getImageBackground().setColor(color);
            imageWindow.getEditor().getImageBackground().refresh();
        }
        close();
    }

    @Override
    public void focus() {
        imageBackgroundColor.requestFocus();
    }
}
