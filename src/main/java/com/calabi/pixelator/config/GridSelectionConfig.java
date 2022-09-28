package com.calabi.pixelator.config;

/**
 * This config object stores whether or not a grid is selected, and if so, the selected grid.
 */
public class GridSelectionConfig extends ConfigObject {

    private int xInterval;
    private int yInterval;

    public GridSelectionConfig() {
    }

    public GridSelectionConfig(int xInterval, int yInterval) {
        this.xInterval = xInterval;
        this.yInterval = yInterval;
    }

    @Override
    public void build(String input) {
        String[] split = input.substring(1).split("/");
        xInterval = Integer.parseInt(split[0]);
        yInterval = Integer.parseInt(split[1]);
    }

    @Override
    public String toConfig() {
        return xInterval + "/" + yInterval;
    }

    public int getXInterval() {
        return xInterval;
    }

    public int getYInterval() {
        return yInterval;
    }
}
