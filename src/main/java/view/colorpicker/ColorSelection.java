package main.java.view.colorpicker;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;

import main.java.control.basic.BasicTextField;
import main.java.res.Config;

public class ColorSelection extends BorderPane {

    private final ColorPickerComponents components;
    private final ColorPicker colorPicker;
    private final ColorPreview colorPreview;

    public ColorSelection() {
        components = new ColorPickerComponents();
        HuePicker huePicker = components.getHuePicker();
        colorPicker = components.getColorPicker();
        colorPreview = components.getPreview();
        TabButtons tabButtons = components.getTabButtons();
        BasicTextField redField = components.getRedField();
        BasicTextField greenField = components.getGreenField();
        BasicTextField blueField = components.getBlueField();
        BasicTextField alphaField = components.getAlphaField();
        Slider redSlider = components.getRedSlider();
        Slider greenSlider = components.getGreenSlider();
        Slider blueSlider = components.getBlueSlider();
        Slider alphaSlider = components.getAlphaSlider();

        colorPicker.setColor(Color.valueOf(Config.getString(Config.COLOR, "#000000")));
        components.updatePreview(getColor());

        Label title = new Label("COLOR");

        setTop(title);
        GridPane gridPane = new GridPane();
        gridPane.addRow(0, huePicker, colorPicker, colorPreview);
        gridPane.add(tabButtons, 0, 1, 1, 4);
        gridPane.addRow(1, null, redField, redSlider);
        gridPane.addRow(1, null, greenField, greenSlider);
        gridPane.addRow(1, null, blueField, blueSlider);
        gridPane.addRow(1, null, alphaField, alphaSlider);
        GridPane.setHgrow(colorPreview, Priority.ALWAYS);
        GridPane.setHgrow(redSlider, Priority.ALWAYS);
        gridPane.setHgap(6);
        gridPane.setPadding(new Insets(6, 0, 6, 0));
        setCenter(gridPane);
    }

    public Color getColor() {
        return components.getColor();
    }

    public void setColor(Color color) {
        components.setColor(color);
    }

}
