package com.calabi.pixelator.control.basic;

import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

import com.calabi.pixelator.res.Images;

public abstract class BasicNumberField<T extends Number> extends BasicControl<T> {

    TextField textField;
    ObjectProperty<StringConverter<T>> converter = new SimpleObjectProperty<>();
    T minValue;
    T maxValue;
    private BooleanProperty showButtons = new SimpleBooleanProperty(true);
    private Button up;
    private Button down;
    private T step;

    public BasicNumberField(String title, String tail, T value) {
        super(title, tail, value);

        converter.addListener((ov, o, n) -> {
            if (n != null) {
                textField.textProperty().unbindBidirectional(this.valueProperty());
                textField.textProperty().bindBidirectional(this.valueProperty(), n);
            }
        });

        DoubleBinding halfHeight = textField.heightProperty().divide(2);
        up = new Button("", Images.SPINNER_UP.getImageView());
        up.getStyleClass().add("increment-arrow-button");
        up.minHeightProperty().bind(halfHeight);
        up.maxHeightProperty().bind(halfHeight);
        down = new Button("", Images.SPINNER_DOWN.getImageView());
        down.getStyleClass().add("decrement-arrow-button");
        down.minHeightProperty().bind(halfHeight);
        down.maxHeightProperty().bind(halfHeight);

        up.setOnAction(e -> increment());
        down.setOnAction(e -> decrement());

        VBox spinner = new VBox(up, down);
        addControl(spinner, 1);

        showButtons.addListener((ov, o, n) -> {
            if (n) {
                spinner.getChildren().setAll(up, down);
            } else {
                spinner.getChildren().clear();
            }
        });
    }

    @Override
    public final Control createControl() {
        textField = new TextField();
        return textField;
    }

    protected abstract void increment();

    protected abstract void decrement();

    protected abstract T getDefaultStep();

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

    public T getStep() {
        if (step == null) {
            step = getDefaultStep();
        }
        return step;
    }

    public void setStep(T step) {
        this.step = step;
    }

    public boolean isShowButtons() {
        return showButtons.get();
    }

    public BooleanProperty showButtonsProperty() {
        return showButtons;
    }

    public void setShowButtons(boolean showButtons) {
        this.showButtons.set(showButtons);
    }

}
