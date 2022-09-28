package com.calabi.pixelator.view.dialog;

import com.calabi.pixelator.config.Config;

public class NewImageDialog extends NewDialog {

    public NewImageDialog() {
        setTitle("Create a new Image");
        setOkText("Create");

        widthField.setValue(Config.NEW_IMAGE_WIDTH.getInt());
        widthField.valueProperty().addListener((ov, o, n) -> {
            if (n != null) {
                Config.NEW_IMAGE_WIDTH.putInt(n);
            }
        });

        heightField.setValue(Config.NEW_IMAGE_HEIGHT.getInt());
        heightField.valueProperty().addListener((ov, o, n) -> {
            if (n != null) {
                Config.NEW_IMAGE_HEIGHT.putInt(n);
            }
        });
    }

}
