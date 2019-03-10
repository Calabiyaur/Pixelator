package main.java.view.colorselection;

import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;

import main.java.control.basic.BasicTextField;
import main.java.res.Config;

public class ColorSelection extends BorderPane {

    private final ColorSelectionModel model;

    public ColorSelection() {
        model = new ColorSelectionModel();
        HuePicker huePicker = model.getHuePicker();
        ColorPicker colorPicker = model.getColorPicker();
        ColorPreview colorPreview = model.getPreview();
        TabButtons tabButtons = model.getTabButtons();
        colorPicker.setMinSize(125, 125);

        BasicTextField redField = model.getRedField();
        BasicTextField greenField = model.getGreenField();
        BasicTextField blueField = model.getBlueField();
        BasicTextField alphaField = model.getAlphaField();
        Slider redSlider = model.getRedSlider();
        Slider greenSlider = model.getGreenSlider();
        Slider blueSlider = model.getBlueSlider();
        Slider alphaSlider = model.getAlphaSlider();

        colorPicker.setColor(Color.valueOf(Config.getString(Config.COLOR, "#000000")));
        model.updatePreview(getColor());

        Label title = new Label("COLOR");

        setTop(title);
        GridPane gridPane = new GridPane();
        gridPane.add(huePicker, 0, 0);
        gridPane.add(tabButtons, 0, 1, 1, 4);
        gridPane.addColumn(1, colorPicker, redField, greenField, blueField, alphaField);
        gridPane.addColumn(2, colorPreview, redSlider, greenSlider, blueSlider, alphaSlider);

        GridPane.setHgrow(colorPreview, Priority.ALWAYS);
        GridPane.setHgrow(redSlider, Priority.ALWAYS);
        GridPane.setMargin(huePicker, new Insets(0, 0, 6, 0));
        GridPane.setMargin(colorPicker, new Insets(0, 0, 6, 0));
        GridPane.setMargin(colorPreview, new Insets(0, 0, 6, 0));
        GridPane.setValignment(tabButtons, VPos.TOP);
        gridPane.setHgap(6);
        gridPane.setPadding(new Insets(6, 0, 0, 0));
        setCenter(gridPane);
    }

    public Color getColor() {
        return model.getColor();
    }

    public void setColor(Color color) {
        model.setColor(color);
    }

}
