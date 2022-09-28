package com.calabi.pixelator.config;

import java.util.List;
import java.util.stream.Collectors;

import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;

import org.apache.logging.log4j.util.TriConsumer;

import com.calabi.pixelator.view.dialog.GridDialog;

/**
 * This config object stores the user-created grid dimensions that can be selected in the grid context menu.
 */
public class GridConfig extends ConfigObject {

    private final ContextMenu contextMenu = new ContextMenu();
    private final MenuItem configure;
    private TriConsumer<Boolean, Integer, Integer> onSelection;

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
            GridMenuItem item = createItem(xInterval, yInterval);
            contextMenu.getItems().add(item);
        }
        sort();
    }

    @Override
    public String toConfig() {
        StringBuilder sb = new StringBuilder();
        for (MenuItem item : contextMenu.getItems()) {
            if (item instanceof GridMenuItem) {
                GridMenuItem gridItem = (GridMenuItem) item;
                sb.append(gridItem.getXInterval());
                sb.append("/");
                sb.append(gridItem.getYInterval());
                sb.append(";");
            }
        }
        return sb.toString();
    }

    public static GridConfig getDefault() {
        GridConfig gridConfig = new GridConfig();
        GridMenuItem item = gridConfig.createItem(1, 1);
        gridConfig.contextMenu.getItems().add(item);
        gridConfig.sort();
        return gridConfig;
    }

    public GridMenuItem createItem(int xInterval, int yInterval) {
        return new GridMenuItem(xInterval, yInterval);
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
        } else if (item instanceof GridMenuItem) {
            GridMenuItem gridItem = (GridMenuItem) item;
            return gridItem.getXInterval() * 1024 + gridItem.getYInterval();
        }
        throw new IllegalStateException();
    }

    private void updateMenuItems(GridMenuItem selected) {
        for (MenuItem item : contextMenu.getItems()) {
            if (item instanceof GridMenuItem) {
                GridMenuItem gridItem = (GridMenuItem) item;
                gridItem.setSelected(gridItem == selected);
            }
        }
    }

    public ContextMenu getContextMenu() {
        return contextMenu;
    }

    public void setOnSelection(TriConsumer<Boolean, Integer, Integer> onSelection) {
        this.onSelection = onSelection;
    }

    public class GridMenuItem extends CheckMenuItem {

        private int xInterval;
        private int yInterval;

        public GridMenuItem(int xInterval, int yInterval) {
            this.xInterval = xInterval;
            this.yInterval = yInterval;
            setText(xInterval + " / " + yInterval);
            setOnAction(e -> {
                if (isSelected()) {
                    onSelection.accept(true, this.xInterval, this.yInterval);
                    updateMenuItems(this);
                } else {
                    onSelection.accept(false, this.xInterval, this.yInterval);
                }
            });
        }

        public int getXInterval() {
            return xInterval;
        }

        public void setXInterval(int xInterval) {
            this.xInterval = xInterval;
            setText(xInterval + " / " + yInterval);
        }

        public int getYInterval() {
            return yInterval;
        }

        public void setYInterval(int yInterval) {
            this.yInterval = yInterval;
            setText(xInterval + " / " + yInterval);
        }
    }

}
