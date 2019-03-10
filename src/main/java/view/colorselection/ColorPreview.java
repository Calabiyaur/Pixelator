package main.java.view.colorselection;

import javafx.beans.property.StringProperty;
import javafx.scene.control.TextField;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import main.java.util.ColorUtil;
import main.java.util.StyleUtil;

class ColorPreview extends VBox {

    private final TextField hexField;

    public ColorPreview() {
        hexField = new TextField("0x000000FF");
        hexField.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, null, null)));

        styleProperty().addListener((ov, o, n) -> {
            Color backGround = StyleUtil.getBackgroundColor(n);
            Color textColor = ColorUtil.getHighContrast(backGround);
            hexField.setStyle("-fx-text-fill: " + ColorUtil.toString(textColor));
        });

        getChildren().addAll(hexField);
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
