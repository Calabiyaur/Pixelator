package com.calabi.pixelator.view;

public class ToolSettings {

    public Boolean replace;
    public Boolean fill;
    public Integer thick;
    public Integer bulge;

    public ToolSettings(Boolean replaceColor, Boolean fillShape, Integer thickness, Integer bulge) {
        this.replace = replaceColor;
        this.fill = fillShape;
        this.thick = thickness;
        this.bulge = bulge;
    }

}
