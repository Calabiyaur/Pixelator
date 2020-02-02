package com.calabi.pixelator.view;

public class ToolSettings {

    public Boolean replace;
    public Boolean alphaOnly;
    public Boolean fill;
    public Integer thick;
    public Integer bulge;

    public ToolSettings(Boolean replaceColor, Boolean alphaOnly, Boolean fillShape, Integer thickness, Integer bulge) {
        this.replace = replaceColor;
        this.alphaOnly = alphaOnly;
        this.fill = fillShape;
        this.thick = thickness;
        this.bulge = bulge;
    }

}
