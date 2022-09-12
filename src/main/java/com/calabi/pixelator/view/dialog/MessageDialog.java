package com.calabi.pixelator.view.dialog;

import javafx.scene.control.Label;

public class MessageDialog extends BasicDialog {

    private final Label messageLabel = new Label();

    public MessageDialog(String title, String message) {
        messageLabel.setWrapText(true);

        addContent(this.messageLabel, 0, 0);

        setTitle(title);
        setMessage(message);

        setPrefSize(400, 250);
        setOnOk(button -> close());
    }

    public void setMessage(String message) {
        messageLabel.setText(message);
    }

    @Override
    public void focus() {
    }

}
