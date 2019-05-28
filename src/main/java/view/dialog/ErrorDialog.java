package main.java.view.dialog;

import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.StackPane;

public class ErrorDialog extends BasicDialog {

    private Label message = new Label();
    private Label header = new Label();

    public ErrorDialog() {
        setTitle("An error has occurred.");
        addContent(header, 0, 0);

        StackPane textStack = new StackPane();
        TextArea textField = new TextArea(message.getText());
        textField.setEditable(false);
        textField.setStyle(
                "-fx-background-color: transparent; -fx-background-insets: 0; -fx-background-radius: 0; -fx-padding: 0;"
        );
        Label invisibleLabel = new Label();
        invisibleLabel.textProperty().bind(message.textProperty());
        invisibleLabel.setVisible(false);
        textStack.getChildren().addAll(invisibleLabel, textField);
        message.textProperty().bindBidirectional(textField.textProperty());
        message.setGraphic(textStack);
        message.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);

        addContent(message, 0, 1);

        setPrefSize(750, 500);
        setOnOk(button -> close());
    }

    public void setHeader(String header) {
        this.header.setText(header);
    }

    public void setMessage(String message) {
        this.message.setText(message);
    }

    @Override
    public void focus() {

    }

}
