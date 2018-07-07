package main.java.standard.control.basic;

import javafx.beans.property.Property;
import javafx.scene.control.Control;
import javafx.scene.control.TextField;

public class BasicTextField extends BasicControl<String> {

    private TextField textField;
    private int maxValue;

    public BasicTextField(String title, String tail, String value) {
        super(title, tail, value);
    }

    public BasicTextField(String title, String tail, Integer value) {
        super(title, tail, value.toString());
    }

    public BasicTextField(String title, String value) {
        super(title, value);
    }

    public BasicTextField(String title, Integer value) {
        super(title, value.toString());
    }

    @Override
    public Control createControl() {
        textField = new TextField();
        return textField;
    }

    @Override
    public String getValue() {
        return textField.getText();
    }

    @Override
    public void setValue(String value) {
        textField.setText(value);
    }

    @Override public Property<String> valueProperty() {
        return textField.textProperty();
    }

    public Integer getIntValue() {
        try {
            return Integer.parseInt(getValue());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public void setValue(int value) {
        setValue(Integer.toString(value));
    }

    public int getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(int maxValue) { //TODO: Does not work twice
        this.maxValue = maxValue;
        textField.textProperty().addListener((ov, o, n) -> {
            Integer intValue = getIntValue();
            if (intValue != null && intValue > maxValue) {
                setValue(maxValue);
            }
        });
    }

}
