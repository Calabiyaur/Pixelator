package com.calabi.pixelator.control.basic;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Control;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;

public abstract class BasicNumberField<T extends Number> extends BasicControl<T> {

    TextField textField;
    ObjectProperty<StringConverter<T>> converter = new SimpleObjectProperty<>();
    T minValue;
    T maxValue;
    boolean allowNegativeValues = false;

    public BasicNumberField(String title, String tail, T value) {
        super(title, tail, value);

        converter.addListener((ov, o, n) -> {
            if (n != null) {
                textField.textProperty().unbindBidirectional(this.valueProperty());
                textField.textProperty().bindBidirectional(this.valueProperty(), n);
            }
        });
    }

    @Override
    public final Control createControl() {
        textField = new TextField();
        return textField;
    }

    public void refresh() {
        textField.setText(getConverter().toString(getValue()));
    }

    public StringConverter<T> getConverter() {
        return converter.get();
    }

    public void setConverter(StringConverter<T> converter) {
        this.converter.set(converter);
    }

    public T getMinValue() {
        return minValue;
    }

    public void setMinValue(T minValue) {
        this.minValue = minValue;
    }

    public T getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(T maxValue) {
        this.maxValue = maxValue;
    }

}
