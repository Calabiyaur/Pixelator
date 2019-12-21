package com.calabi.pixelator.control.image;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

import com.calabi.pixelator.res.Config;
import com.calabi.pixelator.res.Images;
import com.calabi.pixelator.util.BackgroundUtil;

public class ImageBackground extends Pane {

    private FillType type;
    private Color color;

    public ImageBackground() {
        this.type = FillType.SINGLE_COLOR;
        this.color = Color.valueOf(Config.IMAGE_BACKGROUND_COLOR.getString());
    }

    public void setType(FillType type) {
        this.type = type;
        switch(type) {
            case SINGLE_COLOR:
                setBackground(BackgroundUtil.color(color));
                break;
            case CHECKERS:
                setBackground(BackgroundUtil.repeat(Images.CHECKERS));
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

    public enum FillType {
        SINGLE_COLOR,
        CHECKERS
    }
}
