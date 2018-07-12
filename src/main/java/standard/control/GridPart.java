package main.java.standard.control;

import java.util.ArrayList;
import java.util.List;

import javafx.collections.ObservableList;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.RowConstraints;

import main.java.util.Check;

/**
 * Class that shares its column and row constraints with
 * other parts of the same {@link GridGroup}.
 */
public class GridPart extends Pane {

    private GridPane grid;
    List<ColumnQuality> columnQualities = new ArrayList<>();

    public GridPart() {
        grid = new GridPane();
        getChildren().add(grid);
    }

    public void setGridGroup(GridGroup gg) {
        gg.getGridParts().add(this);
    }

    public void setHgap(double value) {
        grid.setHgap(value);
    }

    public ObservableList<ColumnConstraints> getColumnConstraints() {
        return grid.getColumnConstraints();
    }

    public ObservableList<RowConstraints> getRowConstraints() {
        return grid.getRowConstraints();
    }

    public List<ColumnQuality> getColumnQualities() {
        return columnQualities;
    }

    public void add(Region child, int columnIndex, int rowIndex) {
        grid.add(child, columnIndex, rowIndex);

        if (columnQualities.size() - 1 < columnIndex) {
            Check.ensure(columnQualities.size() == columnIndex);
            ColumnQuality cq = new ColumnQuality();
            columnQualities.add(cq);
        }

        child.widthProperty().addListener((ov, o, n) -> {
            ColumnQuality cq = columnQualities.get(columnIndex);
            cq.setPrefWidth(Math.min(n.doubleValue(), cq.getMaxWidth()));
        });
    }

    public void addRow(int rowIndex, Region... children) {
        for (int i = 0; i < children.length; i++) {
            Region child = children[i];
            add(child, i, rowIndex);
        }
    }
}
