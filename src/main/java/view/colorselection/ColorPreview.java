package main.java.view.colorselection;

import javafx.beans.property.StringProperty;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

class ColorPreview extends VBox {

    private final Pane colorPane;
    private final TextField hexField;

    public ColorPreview() {
        colorPane = new Pane();
        hexField = new TextField("0x000000FF");
        getChildren().addAll(colorPane, hexField);
    }

    public String getText() {
        return hexField.getText();
    }

    public void setText(String text) {
        hexField.setText(text);
    }

    public StringProperty textProperty() {
        return hexField.textProperty();
    }
}
