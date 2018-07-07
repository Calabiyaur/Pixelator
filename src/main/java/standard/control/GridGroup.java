package main.java.standard.control;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.RowConstraints;

import main.java.util.Check;

/**
 * Class for handling multiple similar grid panes, i.e. {@link GridPart}s.
 */
public class GridGroup {

    private ObservableSet<GridPart> gridParts;
    private ObservableList<ColumnConstraints> columnConstraints = FXCollections.observableArrayList();
    private ObservableList<RowConstraints> rowConstraints = FXCollections.observableArrayList();

    public GridGroup() {
        this.gridParts = FXCollections.observableSet();
        init();
    }

    public GridGroup(GridPart... gridParts) {
        this.gridParts = FXCollections.observableSet(gridParts);
        init();
    }

    private void init() {
        columnConstraints.addListener((ListChangeListener<ColumnConstraints>) c -> gridParts.forEach(
                part -> part.getColumnConstraints().setAll(columnConstraints)
        ));
        rowConstraints.addListener((ListChangeListener<RowConstraints>) r -> gridParts.forEach(
                part -> part.getRowConstraints().setAll(rowConstraints)
        ));
        gridParts.addListener((SetChangeListener<GridPart>) p -> updateConstraints());
        updateConstraints();
    }

    private void updateConstraints() {
        List<ColumnQuality> columnQualities = new ArrayList<>();

        for (GridPart part : gridParts) {
            List<ColumnQuality> partQualities = part.getColumnQualities();

            for (int i = 0; i < partQualities.size(); i++) {
                ColumnQuality partQuality = partQualities.get(i);
                ColumnQuality groupQuality;
                if (columnQualities.size() - 1 < i) {
                    Check.ensure(columnQualities.size() == i);
                    groupQuality = new ColumnQuality();
                    columnQualities.add(groupQuality);
                } else {
                    groupQuality = columnQualities.get(i);
                }

                groupQuality.combineWith(i, partQuality);
            }
        }

        columnConstraints.setAll(columnQualities.stream().map(q -> q.build()).collect(Collectors.toSet()));
    }

    public ObservableSet<GridPart> getGridParts() {
        return gridParts;
    }

}
