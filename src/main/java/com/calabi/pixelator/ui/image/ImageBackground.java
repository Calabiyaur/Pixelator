package com.calabi.pixelator.ui.image;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

import com.calabi.pixelator.config.Config;
import com.calabi.pixelator.config.Images;
import com.calabi.pixelator.util.BackgroundBuilder;

public class ImageBackground extends Pane {

    private FillType type;
    private Color color;
    private Color borderColor;

    public ImageBackground() {
        this.type = FillType.SINGLE_COLOR;
        this.color = Color.valueOf(Config.IMAGE_BACKGROUND_COLOR.getString());
        this.borderColor = Color.valueOf(Config.IMAGE_BORDER_COLOR.getString());
    }

    public void setType(FillType type) {
        this.type = type;

        BackgroundBuilder builder = switch(type) {
            case SINGLE_COLOR -> BackgroundBuilder.color(color);
            case CHECKERS -> BackgroundBuilder.repeat(Images.CHECKERS.getImage());
        };
        if (borderColor.isOpaque()) {
            builder.border(borderColor);
        }
        setBackground(builder.build());
    }

    public void refresh() {
        setType(getType());
    }

    public FillType getType() {
        return type;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Color getBorderColor() {
        return borderColor;
    }

    public void setBorderColor(Color borderColor) {
        this.borderColor = borderColor;
    }

    public enum FillType {
        SINGLE_COLOR,
        CHECKERS
    }
}
