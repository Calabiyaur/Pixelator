package main.java.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

import main.java.meta.Point;

public class InfoView extends GridPane {

    private static InfoView instance;
    private static Label mousePosition;

    public static InfoView getInstance() {
        if (instance == null) {
            instance = new InfoView();
            instance.setStyle("-fx-background-color: #f4f4f4");
            instance.setHgap(6);
            instance.setPadding(new Insets(2, 0, 6, 2));
            instance.setAlignment(Pos.TOP_LEFT);

            mousePosition = new Label();
            instance.add(mousePosition, 0, 0);
        }
        return instance;
    }

    public static void setMousePosition(Point position) {
        mousePosition.setText(position == null ? null : position.toString());
    }

}
