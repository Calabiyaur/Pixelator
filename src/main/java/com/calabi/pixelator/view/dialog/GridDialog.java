package com.calabi.pixelator.view.dialog;

import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;

import com.calabi.pixelator.config.GridConfig;
import com.calabi.pixelator.config.GridConfig.GridMenuItem;
import com.calabi.pixelator.ui.control.BasicIntegerField;

public class GridDialog extends BasicDialog {

    private final GridConfig gridConfig;

    private final ListView<GridMenuItem> list;
    private final Button add;
    private final Button remove;
    private final BasicIntegerField xInterval;
    private final BasicIntegerField yInterval;

    public GridDialog(GridConfig gridConfig, List<GridMenuItem> items) {
        super(400, 300);
        setTitle("Configure custom grid intervals");
        setOkText("Save");

        this.gridConfig = gridConfig;
        list = new ListView<>(FXCollections.observableArrayList(items));
        add = new Button("Add");
        remove = new Button("Remove");
        xInterval = new BasicIntegerField("X-Interval");
        xInterval.setMinValue(1);
        xInterval.setMaxValue(1024);
        yInterval = new BasicIntegerField("Y-Interval");
        yInterval.setMinValue(1);
        yInterval.setMaxValue(1024);

        addContent(list, 0, 0, 2, 3);
        addContent(add, 0, 3);
        addContent(remove, 1, 3);
        addContent(xInterval, 2, 0);
        addContent(yInterval, 2, 1);

        initLayout();
        initBehavior();

        list.getSelectionModel().select(0);
    }

    public List<GridMenuItem> getItems() {
        return list.getItems();
    }

    @Override
    public void focus() {
        xInterval.focus();
    }

    private void initLayout() {
        list.setMaxWidth(120);
        list.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(GridMenuItem item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    textProperty().unbind();
                    setText(null);
                } else {
                    textProperty().bind(item.textProperty());
                }
            }
        });
        xInterval.setMinWidth(100);
        yInterval.setMinWidth(100);
    }

    private void initBehavior() {
        list.getSelectionModel().selectedItemProperty().addListener((ov, o, n) -> {
            xInterval.setValue(n.getXInterval());
            yInterval.setValue(n.getYInterval());
        });

        add.setOnAction(e -> {
            GridMenuItem newItem = gridConfig.createItem(1, 1);
            list.getItems().add(newItem);
            list.getSelectionModel().select(newItem);
        });
        remove.setOnAction(e -> {
            GridMenuItem selectedItem = list.getSelectionModel().getSelectedItem();
            list.getItems().remove(selectedItem);
        });
        list.getItems().addListener((ListChangeListener<GridMenuItem>) c -> remove.setDisable(c.getList().size() <= 1));

        xInterval.valueProperty().addListener((ov, o, n) -> {
            GridMenuItem selectedItem = list.getSelectionModel().getSelectedItem();
            selectedItem.setXInterval(n);
        });
        yInterval.valueProperty().addListener((ov, o, n) -> {
            GridMenuItem selectedItem = list.getSelectionModel().getSelectedItem();
            selectedItem.setYInterval(n);
        });
    }

}
