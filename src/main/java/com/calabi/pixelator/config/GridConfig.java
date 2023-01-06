package com.calabi.pixelator.config;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;

import com.calabi.pixelator.view.dialog.GridDialog;

/**
 * This config object stores the user-created grid dimensions that can be selected in the grid context menu.
 */
public class GridConfig extends ConfigObject {

    private final ContextMenu contextMenu = new ContextMenu();
    private final MenuItem configure;
    private Consumer<GridMenuItem> onSelection;

    public GridConfig() {
        configure = new MenuItem();
        configure.setText("Configure...");
        configure.setOnAction(e -> {
            List<GridMenuItem> gridItems = contextMenu.getItems().stream()
                    .filter(item -> item instanceof GridMenuItem)
                    .map(item -> ((GridMenuItem) item)).collect(Collectors.toList());
            GridDialog dialog = new GridDialog(this, gridItems);
            dialog.showAndFocus();
            dialog.setOnOk(ok -> {
                contextMenu.getItems().removeIf(item -> item instanceof GridMenuItem);
                contextMenu.getItems().addAll(dialog.getItems());
                sort();

                Config.GRID_CONFIG.putObject(this);
                dialog.close();
            });
        });
        contextMenu.getItems().add(configure);
        contextMenu.getItems().add(new SeparatorMenuItem());
    }

    @Override
    public void build(String input) {
        String[] stringItems = input.split(";");
        for (String stringItem : stringItems) {
            String[] split;
            split = stringItem.split("/");
            int xInterval = Integer.parseInt(split[0]);
            int yInterval = Integer.parseInt(split[1]);
            int xOffset = 0;
            int yOffset = 0;
            if (split.length > 2) {
                xOffset = Integer.parseInt(split[2]);
                yOffset = Integer.parseInt(split[3]);
            }
            GridMenuItem item = createItem(xInterval, yInterval, xOffset, yOffset);
            contextMenu.getItems().add(item);
        }
        sort();
    }

    @Override
    public String toConfig() {
        StringBuilder sb = new StringBuilder();
        for (MenuItem item : contextMenu.getItems()) {
            if (item instanceof GridMenuItem gridItem) {
                sb.append(gridItem.getXInterval());
                sb.append("/");
                sb.append(gridItem.getYInterval());
                if (gridItem.getXOffset() != 0 || gridItem.getYOffset() != 0) {
                    sb.append("/");
                    sb.append(gridItem.getXOffset());
                    sb.append("/");
                    sb.append(gridItem.getYOffset());
                }
                sb.append(";");
            }
        }
        return sb.toString();
    }

    public static GridConfig getDefault() {
        GridConfig gridConfig = new GridConfig();
        GridMenuItem item = gridConfig.createItem(1, 1, 0, 0);
        gridConfig.contextMenu.getItems().add(item);
        gridConfig.sort();
        return gridConfig;
    }

    public GridMenuItem createItem(int xInterval, int yInterval, int xOffset, int yOffset) {
        return new GridMenuItem(xInterval, yInterval, xOffset, yOffset);
    }

    private void sort() {
        contextMenu.getItems().sort((o1, o2) -> {
            if (o1 == null || o2 == null) {
                return 0;
            }
            return valueOf(o1).compareTo(valueOf(o2));
        });
    }

    private Integer valueOf(MenuItem item) {
        if (item == configure) {
            return Integer.MAX_VALUE;
        } else if (item instanceof SeparatorMenuItem) {
            return Integer.MAX_VALUE - 1;
        } else if (item instanceof GridMenuItem gridItem) {
            return gridItem.getXInterval() * 1024 + gridItem.getYInterval();
        }
        throw new IllegalStateException();
    }

    private void updateMenuItems(GridMenuItem selected) {
        for (MenuItem item : contextMenu.getItems()) {
            if (item instanceof GridMenuItem gridItem) {
                gridItem.setSelected(gridItem == selected);
            }
        }
    }

    public ContextMenu getContextMenu() {
        return contextMenu;
    }

    public void setOnSelection(Consumer<GridMenuItem> onSelection) {
        this.onSelection = onSelection;
    }

    public class GridMenuItem extends CheckMenuItem {

        private int xInterval;
        private int yInterval;
        private int xOffset;
        private int yOffset;

        public GridMenuItem(int xInterval, int yInterval, int xOffset, int yOffset) {
            this.xInterval = xInterval;
            this.yInterval = yInterval;
            this.xOffset = xOffset;
            this.yOffset = yOffset;

            updateText();

            setOnAction(e -> {
                onSelection.accept(this);
                if (isSelected()) {
                    updateMenuItems(this);
                }
            });
        }

        private void updateText() {
            String text = xInterval + " / " + yInterval;
            if (xOffset != 0 || yOffset != 0) {
                text += " + " + xOffset + " / " + yOffset;
            }
            setText(text);
        }

        public int getXInterval() {
            return xInterval;
        }

        public void setXInterval(int xInterval) {
            this.xInterval = xInterval;
            updateText();
        }

        public int getYInterval() {
            return yInterval;
        }

        public void setYInterval(int yInterval) {
            this.yInterval = yInterval;
            updateText();
        }

        public int getXOffset() {
            return xOffset;
        }

        public void setXOffset(int xOffset) {
            this.xOffset = xOffset;
            updateText();
        }

        public int getYOffset() {
            return yOffset;
        }

        public void setYOffset(int yOffset) {
            this.yOffset = yOffset;
            updateText();
        }
    }

}
