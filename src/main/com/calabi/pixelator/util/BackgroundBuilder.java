package com.calabi.pixelator.util;

import java.util.ArrayList;
import java.util.List;

import javafx.geometry.Insets;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.paint.Paint;
import javafx.util.Builder;

public final class BackgroundBuilder implements Builder<Background> {

    private final Paint base;
    private Color borderColor;
    private int borderWidth = 0;

    private BackgroundBuilder(Paint base) {
        this.base = base;
    }

    public static BackgroundBuilder color(Color color) {
        return new BackgroundBuilder(color);
    }

    public static BackgroundBuilder repeat(Image image) {
        return new BackgroundBuilder(new ImagePattern(image, 0, 0, image.getWidth(), image.getHeight(), false));
    }

    public BackgroundBuilder border(Color borderColor) {
        this.borderColor = borderColor;
        return this;
    }

    public BackgroundBuilder borderWidth(int borderWidth) {
        this.borderWidth = borderWidth;
        return this;
    }

    public Background build() {
        List<BackgroundFill> fills = new ArrayList<>();

        if (borderColor != null) {
            fills.add(new BackgroundFill(
                    borderColor,
                    CornerRadii.EMPTY,
                    Insets.EMPTY
            ));
        }

        fills.add(new BackgroundFill(
                base,
                CornerRadii.EMPTY,
                borderWidth == 0 ? Insets.EMPTY : new Insets(borderWidth)
        ));

        return new Background(fills, null);
    }

}
