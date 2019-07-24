package com.calabi.pixelator.control.basic;

import javafx.beans.binding.DoubleBinding;
import javafx.collections.FXCollections;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.paint.Color;
import javafx.util.StringConverter;

import com.calabi.pixelator.control.image.PixelatedImageView;
import com.calabi.pixelator.files.PaletteFile;
import com.calabi.pixelator.util.ColorUtil;
import com.calabi.pixelator.view.palette.PaletteEditor;

public class ChangeColorButton extends ComboBox<PaletteEditor> {

    private final Color leftColor;

    public ChangeColorButton(PaletteFile paletteFile, Color leftColor) {
        super(FXCollections.observableArrayList(new PaletteEditor(paletteFile)));
        this.leftColor = leftColor;

        setValue(getItems().get(0));
        getValue().getImageView().setScaleX(20);
        getValue().getImageView().setScaleY(20);
        getValue().setVisible(false);

        setConverter(new StringConverter<>() {
            @Override public String toString(PaletteEditor object) {
                return null;
            }

            @Override public PaletteEditor fromString(String string) {
                return null;
            }
        });
        setPrefSize(80, 30);
        initStyle();
    }

    private void initStyle() {
        setCellFactory(param -> new ListCell<>() {
            @Override
            public void updateItem(PaletteEditor item, boolean empty) {
                super.updateItem(item, empty);
                if (!empty && item != null && item != getGraphic()) {
                    setGraphic(item);
                    item.setVisible(true);
                    setStyle("-fx-background-color: transparent");
                    PixelatedImageView imageView = getValue().getImageView();
                    DoubleBinding width = imageView.widthProperty().multiply(imageView.getScaleX()).add(5);
                    DoubleBinding height = imageView.heightProperty().multiply(imageView.getScaleY()).add(5);
                    prefWidthProperty().bind(width);
                    prefHeightProperty().bind(height);
                }
            }
        });

        updateColor(leftColor);

        getValue().selectedColorProperty().addListener((ov, o, n) -> updateColor(n));
    }

    private void updateColor(Color rightColor) {
        setStyle(String.format("-fx-background-color: %s, %s; -fx-background-insets: 0, 0 0 0 40;",
                ColorUtil.toString(leftColor),
                ColorUtil.toString(rightColor)));
    }

}
