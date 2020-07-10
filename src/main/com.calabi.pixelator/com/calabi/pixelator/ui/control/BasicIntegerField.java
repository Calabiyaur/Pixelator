package com.calabi.pixelator.ui.control;

import java.util.function.UnaryOperator;

import javafx.scene.control.TextFormatter;
import javafx.util.StringConverter;

import com.calabi.pixelator.ui.control.number.IntPattern;

public class BasicIntegerField extends BasicNumberField<Integer> {

    private IntPattern pattern = IntPattern.POSITIVE;

    public BasicIntegerField(String title) {
        this(title, null);
    }

    public BasicIntegerField(String title, Integer value) {
        this(title, null, value);
    }

    public BasicIntegerField(String title, Integer value, IntPattern pattern) {
        this(title, null, value, pattern);
    }

    public BasicIntegerField(String title, String tail, Integer value, IntPattern pattern) {
        this(title, tail, value);
        this.pattern = pattern;
    }

    public BasicIntegerField(String title, String tail, Integer value) {
        super(title, tail, value);

        TextFormatter<Object> textFormatter = createFormatter();
        textField.setTextFormatter(textFormatter);

        setConverter(new StringConverter<>() {
            @Override
            public String toString(Integer object) {
                if (object == null) {
                    return "";
                }
                return Integer.toString(object);
            }

            @Override
            public Integer fromString(String string) {
                try {
                    return Integer.parseInt(string);
                } catch (NumberFormatException e) {
                    return null;
                }
            }
        });
    }

    private TextFormatter<Object> createFormatter() {
        UnaryOperator<TextFormatter.Change> filter = change -> {
            String text = change.getText();
            String fullText = change.getControlNewText();

            if (!text.matches(pattern.getRegex())) {
                return null;
            }

            Integer newValue = getConverter().fromString(fullText);
            if (newValue == null) {
                return change;
            }

            if (minValue != null && newValue < minValue) {
                int caretPosition = change.getCaretPosition();
                textField.setText(getConverter().toString(minValue));
                textField.positionCaret(caretPosition);
                return null;
            }

            if (maxValue != null && newValue > maxValue) {
                int caretPosition = change.getCaretPosition();
                textField.setText(getConverter().toString(maxValue));
                textField.positionCaret(caretPosition);
                return null;
            }

            return change;
        };
        return new TextFormatter<>(filter);
    }

    @Override
    protected void increment() {
        Integer value = getValue() == null ? 0 : getValue();
        setValue(Math.min(maxValue == null ? Integer.MAX_VALUE : maxValue, value + getStep()));
    }

    @Override
    protected void decrement() {
        Integer value = getValue() == null ? 0 : getValue();
        setValue(Math.max(minValue == null ? Integer.MIN_VALUE : minValue, value - getStep()));
    }

    @Override
    protected Integer getDefaultStep() {
        return 1;
    }

    public IntPattern getPattern() {
        return pattern;
    }

    public void setPattern(IntPattern pattern) {
        this.pattern = pattern;
    }
}
