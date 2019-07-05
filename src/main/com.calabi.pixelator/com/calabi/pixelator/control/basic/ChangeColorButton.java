package com.calabi.pixelator.control.basic;

import java.util.Collection;

import javafx.collections.FXCollections;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.paint.Color;
import javafx.util.StringConverter;

import com.calabi.pixelator.util.ColorUtil;

public class ChangeColorButton extends ComboBox<Color> {

    private final Color leftColor;

    public ChangeColorButton(Color leftColor, Collection<Color> rightColors) {
        super(FXCollections.observableArrayList(rightColors));
        this.leftColor = leftColor;
        setValue(leftColor);
        setConverter(new StringConverter<Color>() {
            @Override public String toString(Color object) {
                return null;
            }

            @Override public Color fromString(String string) {
                return null;
            }
        });
        setMinSize(80, 30);
        initStyle();
    }

    private void initStyle() {
        setCellFactory(param -> new ListCell<Color>() {
            @Override public void updateItem(Color item, boolean empty) {
                super.updateItem(item, empty);
                if (item != null) {
                    setStyle("-fx-background-color: " + ColorUtil.toString(item));
                }
            }
        });

        updateColor(Color.TRANSPARENT);

        valueProperty().addListener((ov, o, n) -> updateColor(n));
    }

    private void updateColor(Color rightColor) {
        setStyle(String.format("-fx-background-color: %s, %s; -fx-background-insets: 0, 0 0 0 40;",
                ColorUtil.toString(leftColor),
                ColorUtil.toString(rightColor)));
    }

}
