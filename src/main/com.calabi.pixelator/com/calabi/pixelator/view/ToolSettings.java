package com.calabi.pixelator.view;

public class ToolSettings {

    public static final ToolSettings DEFAULT = new ToolSettings(null, false, 1, 0);
    public static final ToolSettings FILL = new ToolSettings(null, true, 1, 0);

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
