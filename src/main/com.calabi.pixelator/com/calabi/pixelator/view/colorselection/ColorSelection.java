package com.calabi.pixelator.view.colorselection;

import java.util.Arrays;

import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;

import com.calabi.pixelator.control.basic.BasicNumberField;

public class ColorSelection extends BorderPane {

    private final ColorSelectionModel model;

    public ColorSelection() {
        model = new ColorSelectionModel();
        HuePicker huePicker = model.getHuePicker();
        ColorPicker colorPicker = model.getColorPicker();
        ColorPreview colorPreview = model.getPreview();
        ColorTabButtons tabButtons = model.getTabButtons();
        colorPicker.setMinSize(125, 125);
        colorPreview.setMinWidth(100);

        BasicNumberField<?> redField = model.getRedField();
        BasicNumberField<?> greenField = model.getGreenField();
        BasicNumberField<?> blueField = model.getBlueField();
        BasicNumberField<?> alphaField = model.getAlphaField();
        for (BasicNumberField<? extends Number> field : Arrays.asList(redField, greenField, blueField, alphaField)) {
            field.getControlWrapper().setMaxWidth(60);
        }
        Slider redSlider = model.getRedSlider();
        Slider greenSlider = model.getGreenSlider();
        Slider blueSlider = model.getBlueSlider();
        Slider alphaSlider = model.getAlphaSlider();

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

    public void addRecentColor(Color color) {
        model.getPreview().addRecentColor(color);
    }

}
