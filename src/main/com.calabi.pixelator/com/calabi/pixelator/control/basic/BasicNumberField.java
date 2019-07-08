package com.calabi.pixelator.control.basic;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Control;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;

public abstract class BasicNumberField<T extends Number> extends BasicControl<T> {

    TextField textField;
    ObjectProperty<T> value;
    StringConverter<T> converter;

    public BasicNumberField(String title, String tail, T value) {
        super(title, tail, value);
    }

    @Override
    public final Control createControl() {
        textField = new TextField();

        value = new SimpleObjectProperty<>();
        converter = createConverter();
        textField.textProperty().bindBidirectional(value, converter);

        return textField;
    }

    @Override
    public final Property<T> valueProperty() {
        return value;
    }

    abstract StringConverter<T> createConverter();

}
