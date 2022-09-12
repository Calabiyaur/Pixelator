package com.calabi.pixelator.view.palette;

import javafx.geometry.Insets;
import javafx.scene.layout.GridPane;

public class PalettePane extends GridPane {

    private PaletteEditor content;

    public PalettePane() {
        setStyle("-fx-background-color: -px_selected");
    }

    public void setContent(PaletteEditor content) {
        if (this.content != null) {
            getChildren().remove(this.content);
        }

        if (content != null) {
            add(content, 0, 0);
            GridPane.setMargin(content, new Insets(2));
        }

        this.content = content;
    }

    @Override
    protected double computeMaxWidth(double height) {
        if (content == null) {
            return super.computeMaxWidth(height);
        } else {
            return content.prefWidth(height - 4) * content.getImageView().getScaleX() + 4;
        }
    }

    @Override
    protected double computeMaxHeight(double width) {
        if (content == null) {
            return super.computeMaxHeight(width);
        } else {
            return content.prefHeight(width - 4) * content.getImageView().getScaleY() + 4;
        }
    }
}
