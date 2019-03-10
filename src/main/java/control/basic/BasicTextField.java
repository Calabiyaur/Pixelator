package main.java.control.basic;

import javafx.beans.property.Property;
import javafx.scene.control.Control;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;

public class BasicTextField extends BasicControl<String> {

    private TextField textField;
    private TextFormatter<String> formatter;
    //TODO: private UnaryOperator<TextFormatter.Change> minValueFilter;
    //TODO: private UnaryOperator<TextFormatter.Change> maxValueFilter;
    //TODO: private UnaryOperator<TextFormatter.Change> minLengthFilter;
    //TODO: private UnaryOperator<TextFormatter.Change> maxLengthFilter;
    //TODO: private UnaryOperator<TextFormatter.Change> numbersOnlyFilter;

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

    public void setMinValue(final int minValue) {
        //TODO
    }

    public void setMaxValue(final int maxValue) {
        //TODO: UnaryOperator<TextFormatter.Change> newFilter = change -> {
        //TODO:     if (change.isDeleted()) {
        //TODO:         return change;
        //TODO:     }
        //TODO:     String text = change.getControlNewText();
        //TODO:     try {
        //TODO:         int value = Integer.parseInt(text);
        //TODO:         return value <= maxValue ? change : null;
        //TODO:     } catch (NumberFormatException e) {
        //TODO:         return null;
        //TODO:     }
        //TODO: };
        //TODO:
        //TODO: if (formatter == null) {
        //TODO:     formatter = new TextFormatter<>(newFilter);
        //TODO: } else {
        //TODO:     UnaryOperator<TextFormatter.Change> oldFilter = formatter.getFilter();
        //TODO:     formatter = new TextFormatter<>(change -> oldFilter.andThen(newFilter).apply(change));
        //TODO: }
        //TODO:
        //TODO: textField.setTextFormatter(formatter);
    }

}
