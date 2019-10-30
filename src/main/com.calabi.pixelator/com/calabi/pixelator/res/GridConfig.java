package com.calabi.pixelator.res;

import java.util.function.BiConsumer;

import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;

import com.calabi.pixelator.view.dialog.CustomGridDialog;

public class GridConfig extends ConfigObject {

    private final ContextMenu contextMenu = new ContextMenu();
    private final MenuItem custom;
    private BiConsumer<Integer, Integer> onSelection;

    public GridConfig() {
        custom = new MenuItem();
        custom.setText("Custom");
        custom.setOnAction(e -> {
            CustomGridDialog dialog = new CustomGridDialog();
            dialog.showAndFocus();
            dialog.setOnOk(ok -> {
                GridMenuItem newItem = createItem(dialog.getNewWidth(), dialog.getNewHeight());
                contextMenu.getItems().add(newItem);
                sort();
                Config.GRID_CONFIG.putObject(this);
                dialog.close();
            });
        });
        contextMenu.getItems().add(custom);
        contextMenu.getItems().add(new SeparatorMenuItem());
    }

    @Override
    public void build(String input) {
        if (!input.contains("+")) {
            input = "+" + input;
        }
        String[] stringItems = input.split(";");
        for (String stringItem : stringItems) {
            String[] split;
            boolean selected = false;
            if (stringItem.startsWith("+")) {
                split = stringItem.substring(1).split("/");
                selected = true;
            } else {
                split = stringItem.split("/");
            }
            int xInterval = Integer.parseInt(split[0]);
            int yInterval = Integer.parseInt(split[1]);
            GridMenuItem item = createItem(xInterval, yInterval);
            item.setSelected(selected);
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
                if (gridItem.isSelected()) {
                    sb.append("+");
                }
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
        item.setSelected(true);
        gridConfig.contextMenu.getItems().add(item);
        gridConfig.sort();
        return gridConfig;
    }

    public CheckMenuItem getSelected() {
        return contextMenu.getItems()
                .stream()
                .filter(i -> i instanceof CheckMenuItem)
                .map(i -> ((CheckMenuItem) i))
                .filter(i -> i.isSelected())
                .findFirst().orElse(null);
    }

    private GridMenuItem createItem(int xInterval, int yInterval) {
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
        if (item == custom) {
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
        Config.GRID_CONFIG.putObject(this);
    }

    public ContextMenu getContextMenu() {
        return contextMenu;
    }

    public void setOnSelection(BiConsumer<Integer, Integer> onSelection) {
        this.onSelection = onSelection;
    }

    private class GridMenuItem extends CheckMenuItem {

        private final int xInterval;
        private final int yInterval;

        public GridMenuItem(int xInterval, int yInterval) {
            this.xInterval = xInterval;
            this.yInterval = yInterval;
            setText(xInterval + " / " + yInterval);
            setOnAction(e -> {
                if (isSelected()) {
                    onSelection.accept(xInterval, yInterval);
                    updateMenuItems(this);
                } else {
                    setSelected(true);
                }
            });
        }

        public int getXInterval() {
            return xInterval;
        }

        public int getYInterval() {
            return yInterval;
        }
    }

}
