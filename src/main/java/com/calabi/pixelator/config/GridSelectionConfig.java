package com.calabi.pixelator.config;

/**
 * This config object stores whether or not a grid is selected, and if so, the selected grid.
 */
public class GridSelectionConfig extends ConfigObject {

    private boolean selected;
    private int xInterval;
    private int yInterval;
    private int xOffset;
    private int yOffset;

    public GridSelectionConfig() {
    }

    public GridSelectionConfig(boolean selected, int xInterval, int yInterval, int xOffset, int yOffset) {
        this.selected = selected;
        this.xInterval = xInterval;
        this.yInterval = yInterval;
        this.xOffset = xOffset;
        this.yOffset = yOffset;
    }

    @Override
    public void build(String input) {
        if (input.isEmpty()) {
            selected = false;
            xInterval = 1;
            yInterval = 1;
            xOffset = 0;
            yOffset = 0;
        } else {
            selected = input.startsWith("+");
            String[] split = input.substring(1).split("/");
            xInterval = Integer.parseInt(split[0]);
            yInterval = Integer.parseInt(split[1]);
            if (split.length > 2) {
                xOffset = Integer.parseInt(split[2]);
                yOffset = Integer.parseInt(split[3]);
            }
        }
    }

    @Override
    public String toConfig() {
        return (selected ? "+" : "-") + xInterval + "/" + yInterval + "/" + xOffset + "/" + yOffset;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public int getXInterval() {
        return xInterval;
    }

    public int getYInterval() {
        return yInterval;
    }

    public int getXOffset() {
        return xOffset;
    }

    public int getYOffset() {
        return yOffset;
    }

}
