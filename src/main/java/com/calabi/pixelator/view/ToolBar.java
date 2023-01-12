package com.calabi.pixelator.view;

import javafx.geometry.Orientation;
import javafx.scene.Cursor;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Separator;
import javafx.scene.control.ToggleButton;

import com.calabi.pixelator.config.Config;
import com.calabi.pixelator.config.GridConfig;
import com.calabi.pixelator.config.GridSelectionConfig;
import com.calabi.pixelator.config.Images;
import com.calabi.pixelator.main.BasicToolBar;
import com.calabi.pixelator.view.editor.IWC;
import com.calabi.pixelator.view.editor.ImageEditor;

import static com.calabi.pixelator.config.Action.*;

public class ToolBar extends BasicToolBar {

    private static ToolBar instance;

    private ToggleButton gridToggle;

    public ToolBar() {
        setOnMouseEntered(e -> setCursor(Cursor.DEFAULT));

        addButton(NEW);
        addButton(OPEN);
        addButton(SAVE);
        getItems().add(new Separator(Orientation.VERTICAL));
        addButton(UNDO);
        addButton(REDO);
        getItems().add(new Separator(Orientation.VERTICAL));
        addButton(CUT);
        addButton(COPY);
        addButton(PASTE);
        getItems().add(new Separator(Orientation.VERTICAL));

        addGridToggle();
        ToggleButton crosshair = addToggle(CROSSHAIR, Images.CROSSHAIR);
        IWC.get().showCrosshairProperty().addListener((ov, o, n) -> crosshair.setSelected(n));
        ToggleButton background = addToggle(BACKGROUND, Images.BACKGROUND);
        IWC.get().showBackgroundProperty().addListener((ov, o, n) -> background.setSelected(n));

        getItems().add(new Separator(Orientation.VERTICAL));
        addButton(ZOOM_IN);
        addButton(ZOOM_ZERO);
        addButton(ZOOM_OUT);
    }

    public static ToolBar get() {
        if (instance == null) {
            instance = new ToolBar();
        }
        return instance;
    }

    public void reload() {
        initGridToggle();
    }

    public void reloadGridMenu() {
        ImageEditor editor = IWC.get().getEditor();
        if (editor != null) {
            GridSelectionConfig config = Config.GRID_SELECTION.getObject(editor.getPixelFile());
            for (MenuItem item : gridToggle.getContextMenu().getItems()) {
                if (item instanceof GridConfig.GridMenuItem gridMenuItem) {
                    if (config.getXInterval() == gridMenuItem.getXInterval()
                            && config.getYInterval() == gridMenuItem.getYInterval()
                            && config.getXOffset() == gridMenuItem.getXOffset()
                            && config.getYOffset() == gridMenuItem.getYOffset()) {
                        gridMenuItem.setSelected(IWC.get().showGridProperty().get());
                    } else {
                        gridMenuItem.setSelected(false);
                    }
                }
            }
            config.setSelected(IWC.get().showGridProperty().get());
            Config.GRID_SELECTION.putObject(editor.getPixelFile(), config);
        }
    }

    private void addGridToggle() {
        gridToggle = addToggle(GRID, Images.GRID);
        IWC.get().showGridProperty().addListener((ov, o, n) -> gridToggle.setSelected(n));
        initGridToggle();
        IWC.get().showGridProperty().addListener((ov, o, n) -> reloadGridMenu());
    }

    private void initGridToggle() {
        GridConfig gridConfig = Config.GRID_CONFIG.getObject();
        gridToggle.setContextMenu(gridConfig.getContextMenu());
        gridConfig.setOnSelection(item -> {
            ImageEditor editor = IWC.get().getEditor();
            if (editor != null) {
                if (item.isSelected()) {
                    editor.setGridInterval(item.getXInterval(), item.getYInterval(), item.getXOffset(), item.getYOffset());
                }
                IWC.get().setShowGrid(item.isSelected());
                GridSelectionConfig config = new GridSelectionConfig(item.isSelected(),
                        item.getXInterval(), item.getYInterval(), item.getXOffset(), item.getYOffset());
                Config.GRID_SELECTION.putObject(editor.getPixelFile(), config);
            }
        });
    }

}
