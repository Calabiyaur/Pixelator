package com.calabi.pixelator.util;

import javafx.geometry.Insets;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;

import com.calabi.pixelator.res.Images;

public class BackgroundUtil {

    public static Background color(Color color) {
        return new Background(
                new BackgroundFill(
                        color,
                        CornerRadii.EMPTY,
                        Insets.EMPTY
                )
        );
    }

    public static Background colorBordered(Color color, Color borderColor) {
        return new Background(
                new BackgroundFill(
                        borderColor,
                        CornerRadii.EMPTY,
                        Insets.EMPTY
                ),
                new BackgroundFill(
                        color,
                        CornerRadii.EMPTY,
                        new Insets(1)
                )
        );
    }

    public static Background repeat(Images enumImage) {
        Image image = enumImage.getImage();
        return new Background(
                new BackgroundFill(
                        new ImagePattern(image, 0, 0, image.getWidth(), image.getHeight(), false),
                        CornerRadii.EMPTY,
                        Insets.EMPTY
                )
        );
    }

    public static Background repeatBordered(Images enumImage, Color borderColor) {
        Image image = enumImage.getImage();
        return new Background(
                new BackgroundFill(
                        borderColor,
                        CornerRadii.EMPTY,
                        Insets.EMPTY
                ),
                new BackgroundFill(
                        new ImagePattern(image, 0, 0, image.getWidth(), image.getHeight(), false),
                        CornerRadii.EMPTY,
                        new Insets(1)
                )
        );
    }

}
