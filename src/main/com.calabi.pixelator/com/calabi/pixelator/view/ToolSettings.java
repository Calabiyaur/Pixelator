package com.calabi.pixelator.view;

public class ToolSettings {

    public int maxX;
    public int maxY;
    public Boolean replace;
    public Boolean alphaOnly;
    public Boolean fill;
    public Integer thick;
    public Integer bulge;

    public ToolSettings(int maxX, int maxY, Boolean replaceColor, Boolean alphaOnly, Boolean fillShape, Integer thickness, Integer bulge) {
        this.maxX = maxX;
        this.maxY = maxY;
        this.replace = replaceColor;
        this.alphaOnly = alphaOnly;
        this.fill = fillShape;
        this.thick = thickness;
        this.bulge = bulge;
    }

}
