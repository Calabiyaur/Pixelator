package com.calabi.pixelator.res;

/**
 * This config object stores whether or not a grid is selected, and if so, the selected grid.
 */
public class GridSelectionConfig extends ConfigObject {

    private boolean selected;
    private int xInterval;
    private int yInterval;

    public static GridSelectionConfig unselected(int xInterval, int yInterval) {
        GridSelectionConfig config = new GridSelectionConfig();
        config.selected = false;
        config.xInterval = xInterval;
        config.yInterval = yInterval;
        return config;
    }

    public static GridSelectionConfig selected(int xInterval, int yInterval) {
        GridSelectionConfig config = new GridSelectionConfig();
        config.selected = true;
        config.xInterval = xInterval;
        config.yInterval = yInterval;
        return config;
    }

    @Override
    public void build(String input) {
        if (input.isEmpty()) {
            selected = false;
            xInterval = 1;
            yInterval = 1;
        } else {
            selected = input.startsWith("+");
            String[] split = input.substring(1).split("/");
            xInterval = Integer.parseInt(split[0]);
            yInterval = Integer.parseInt(split[1]);
        }
    }

    @Override
    public String toConfig() {
        return (selected ? "+" : "-") + xInterval + "/" + yInterval;
    }

    public boolean isSelected() {
        return selected;
    }

    public int getXInterval() {
        return xInterval;
    }

    public int getYInterval() {
        return yInterval;
    }
}
