package main.java.view.colorselection;

import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
import javafx.scene.control.TextField;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import main.java.res.Images;
import main.java.util.BackgroundUtil;
import main.java.util.ColorUtil;

class ColorPreview extends StackPane {

    private final VBox colorPane;
    private final TextField hexField;

    public ColorPreview() {
        hexField = new TextField("0x000000FF");
        colorPane = new VBox(hexField);
        hexField.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, null, null)));

        setBackground(BackgroundUtil.repeat(Images.CHECKERS));

        colorPane.backgroundProperty().addListener((ov, o, n) -> {
            Color backGround = (Color) n.getFills().get(0).getFill();
            Color textColor = ColorUtil.getHighContrast(backGround);
            hexField.setStyle("-fx-text-fill: " + ColorUtil.toString(textColor));
        });

        getChildren().addAll(colorPane);
    }

    public Color getColor() {
        return ColorUtil.valueOf(getText());
    }

    public void setColor(Color color) {
        colorPane.setBackground(new Background(new BackgroundFill(color, CornerRadii.EMPTY, Insets.EMPTY)));
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
