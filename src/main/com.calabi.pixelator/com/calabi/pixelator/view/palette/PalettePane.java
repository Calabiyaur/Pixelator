package com.calabi.pixelator.view.palette;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;

public class PalettePane extends GridPane {

    private Node content;

    public PalettePane() {
        setStyle("-fx-background-color: #AAAAAAFF");
    }

    public void setContent(Node content) {
        if (this.content != null) {
            getChildren().remove(this.content);
        }
        add(content, 0, 0);
        GridPane.setMargin(content, new Insets(2));

        this.content = content;
    }
}
