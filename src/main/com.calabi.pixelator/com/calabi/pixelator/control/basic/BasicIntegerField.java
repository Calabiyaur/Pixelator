package com.calabi.pixelator.control.basic;

import java.util.function.UnaryOperator;

import javafx.scene.control.TextFormatter;
import javafx.util.StringConverter;

public class BasicIntegerField extends BasicNumberField<Integer> {

    private Integer maxValue;

    public BasicIntegerField(String title) {
        this(title, null);
    }

    public BasicIntegerField(String title, Integer value) {
        this(title, null, value);
    }

    public BasicIntegerField(String title, String tail, Integer value) {
        super(title, tail, value);

        TextFormatter<Object> textFormatter = createFormatter();
        textField.setTextFormatter(textFormatter);
    }

    @Override
    public StringConverter<Integer> createConverter() {
        return new StringConverter<>() {
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
        };
    }

    public Integer getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(Integer maxValue) {
        this.maxValue = maxValue;
    }

    private TextFormatter<Object> createFormatter() {
        UnaryOperator<TextFormatter.Change> filter = change -> {
            String text = change.getText();
            String fullText = change.getControlNewText();

            if (!text.matches("[0-9]*")) {
                return null;
            }

            if (maxValue != null && converter.fromString(fullText) > maxValue) {
                int caretPosition = change.getCaretPosition();
                textField.setText(converter.toString(maxValue));
                textField.positionCaret(caretPosition);
                return null;
            }

            return change;
        };
        return new TextFormatter<>(filter);
    }

}
