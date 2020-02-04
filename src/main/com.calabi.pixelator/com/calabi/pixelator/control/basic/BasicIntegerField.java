package com.calabi.pixelator.control.basic;

import java.util.function.UnaryOperator;

import javafx.scene.control.TextFormatter;
import javafx.util.StringConverter;

import com.calabi.pixelator.control.basic.number.IntPattern;

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
                    return 0;
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

            if (minValue != null && getConverter().fromString(fullText) < minValue) {
                int caretPosition = change.getCaretPosition();
                textField.setText(getConverter().toString(minValue));
                textField.positionCaret(caretPosition);
                return null;
            }

            if (maxValue != null && getConverter().fromString(fullText) > maxValue) {
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
        setValue(Math.min(maxValue == null ? Integer.MAX_VALUE : maxValue, getValue() + getStep()));
    }

    @Override
    protected void decrement() {
        setValue(Math.max(minValue == null ? Integer.MIN_VALUE : minValue, getValue() - getStep()));
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
