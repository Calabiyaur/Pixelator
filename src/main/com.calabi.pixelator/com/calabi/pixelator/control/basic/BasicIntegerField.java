package com.calabi.pixelator.control.basic;

import java.util.function.UnaryOperator;

import javafx.scene.control.TextFormatter;
import javafx.util.StringConverter;

public class BasicIntegerField extends BasicNumberField<Integer> {

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

            if (!text.matches("[0-9]*")) {
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

}
