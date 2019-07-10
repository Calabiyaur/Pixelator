package com.calabi.pixelator.view.dialog;

import javafx.scene.control.Label;

public class MessageDialog extends BasicDialog {

    private Label messageLabel = new Label();

    public MessageDialog() {
        this("You got a message!", "Someone forgot to put text here...");
    }

    public MessageDialog(String title, String message) {
        addContent(this.messageLabel, 0, 0);

        setTitle(title);
        setMessage(message);

        setPrefSize(400, 250);
        setOnOk(button -> close());
    }

    public void setMessage(String message) {
        this.messageLabel.setText(message);
    }

    @Override
    public void focus() {
    }

}
