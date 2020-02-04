package com.calabi.pixelator.control.basic;

import java.util.function.UnaryOperator;

import javafx.scene.control.TextFormatter;
import javafx.util.StringConverter;

public class BasicDoubleField extends BasicNumberField<Double> {

    private double factor = 1;

    private int precision = 2;

    public BasicDoubleField(String title) {
        this(title, null);
    }

    public BasicDoubleField(String title, Double value) {
        this(title, null, value);
    }

    public BasicDoubleField(String title, String tail, Double value) {
        super(title, tail, value);

        TextFormatter<Object> textFormatter = createFormatter();
        textField.setTextFormatter(textFormatter);

        setConverter(new StringConverter<>() {
            @Override
            public String toString(Double object) {
                if (object == null) {
                    return "";
                }
                String longText = Double.toString(object * factor);
                String[] parts = longText.split("\\.");
                String beforeComma = parts[0];
                if (parts.length == 1 || precision == 0) {
                    return beforeComma;
                } else {
                    String afterComma = parts[1];
                    String afterCommaReduced;
                    if (afterComma.length() <= precision) {
                        afterCommaReduced = afterComma;
                    } else {
                        afterCommaReduced = afterComma.substring(0, precision);
                    }
                    return beforeComma + "." + afterCommaReduced;
                }
            }

            @Override
            public Double fromString(String string) {
                try {
                    return Double.parseDouble(string) / factor;
                } catch (NumberFormatException e) {
                    return 0.;
                }
            }
        });
    }

    private TextFormatter<Object> createFormatter() {
        UnaryOperator<TextFormatter.Change> filter = change -> {
            String text = change.getText();
            String fullText = change.getControlNewText();

            if (!text.matches("[0-9.]*")) {
                return null;
            }

            Double newValue = getConverter().fromString(fullText);

            if (minValue != null && newValue < minValue / factor) {
                int caretPosition = change.getCaretPosition();
                textField.setText(getConverter().toString(minValue / factor));
                textField.positionCaret(caretPosition);
                return null;
            }

            if (maxValue != null && newValue > maxValue / factor) {
                int caretPosition = change.getCaretPosition();
                textField.setText(getConverter().toString(maxValue / factor));
                textField.positionCaret(caretPosition);
                return null;
            }

            return change;
        };
        return new TextFormatter<>(filter);
    }

    @Override
    protected void increment() {
        setValue(Math.min(maxValue == null ? Double.MAX_VALUE : maxValue / factor, getValue() + getStep()));
    }

    @Override
    protected void decrement() {
        setValue(Math.max(minValue == null ? Double.MIN_VALUE : minValue / factor, getValue() - getStep()));
    }

    @Override
    protected Double getDefaultStep() {
        return 1e-2;
    }

    public double getConversionFactor() {
        return factor;
    }

    public void setConversionFactor(double factor) {
        this.factor = factor;
        refresh();
    }

    public int getPrecision() {
        return precision;
    }

    public void setPrecision(int precision) {
        this.precision = precision;
        refresh();
    }
}
