package main.java.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

import main.java.meta.Point;

public class InfoView extends GridPane {

    private static InfoView instance;
    private static Label mousePosition;
    private static Label colorCount;

    public static InfoView getInstance() {
        if (instance == null) {
            instance = new InfoView();
            instance.setStyle("-fx-background-color: #f4f4f4");
            instance.setHgap(6);
            instance.setPadding(new Insets(2, 2, 1, 2));
            instance.setAlignment(Pos.TOP_LEFT);

            mousePosition = new Label();
            colorCount = new Label();
            instance.addRow(0, mousePosition, colorCount);
            GridPane.setHgrow(mousePosition, Priority.ALWAYS);
        }
        return instance;
    }

    public static void setMousePosition(Point position) {
        mousePosition.setText(position == null ? null : "Mouse: " + position.toString());
    }

    public static void setColorCount(Integer count) {
        colorCount.setText(count == null ? null : count + " color" + (count > 1 ? "s" : ""));
    }

}
