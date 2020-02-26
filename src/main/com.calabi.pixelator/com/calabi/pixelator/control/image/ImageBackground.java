package com.calabi.pixelator.control.image;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

import com.calabi.pixelator.res.Config;
import com.calabi.pixelator.res.Images;
import com.calabi.pixelator.util.BackgroundUtil;

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
        switch(type) {
            case SINGLE_COLOR:
                if (borderColor.isOpaque()) {
                    setBackground(BackgroundUtil.colorBordered(color, borderColor));
                } else {
                    setBackground(BackgroundUtil.color(color));
                }
                break;
            case CHECKERS:
                if (borderColor.isOpaque()) {
                    setBackground(BackgroundUtil.repeatBordered(Images.CHECKERS.getImage(), borderColor));
                } else {
                    setBackground(BackgroundUtil.repeat(Images.CHECKERS.getImage()));
                }
                break;
        }
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
