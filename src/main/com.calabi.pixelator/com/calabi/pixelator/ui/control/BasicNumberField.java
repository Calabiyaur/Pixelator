package com.calabi.pixelator.ui.control;

import java.util.Iterator;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import javafx.util.StringConverter;

import com.sun.javafx.scene.control.behavior.TextFieldBehavior;
import com.sun.javafx.scene.control.inputmap.InputMap;
import com.sun.javafx.scene.control.inputmap.InputMap.Mapping;
import com.sun.javafx.scene.control.inputmap.KeyBinding;

import com.calabi.pixelator.res.Images;
import com.calabi.pixelator.util.ReflectionUtil;

public abstract class BasicNumberField<T extends Number> extends BasicControl<T> {

    private static final Duration SPINNER_INITIAL_DELAY = new Duration(300);
    private static final Duration SPINNER_REPEAT_DELAY = new Duration(60);

    TextField textField;
    ObjectProperty<StringConverter<T>> converter = new SimpleObjectProperty<>();
    T minValue;
    T maxValue;
    private final BooleanProperty showButtons = new SimpleBooleanProperty(true);
    private final SpinnerButton up;
    private final SpinnerButton down;
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
        up = new SpinnerButton(true);
        up.minHeightProperty().bind(halfHeight);
        up.maxHeightProperty().bind(halfHeight);
        down = new SpinnerButton(false);
        down.minHeightProperty().bind(halfHeight);
        down.maxHeightProperty().bind(halfHeight);

        VBox spinner = new VBox(up, down);
        addControl(spinner, 1);

        showButtons.addListener((ov, o, n) -> {
            if (n) {
                spinner.getChildren().setAll(up, down);
            } else {
                spinner.getChildren().clear();
            }
        });

        textField.skinProperty().addListener((ov, o, n) -> {
            TextFieldBehavior behavior = ReflectionUtil.getField(n, "behavior");
            ObservableList<Mapping<?>> mappings = behavior.getInputMap().getMappings();
            overrideKeyMapping(mappings, keyMapping(KeyCode.UP, e -> increment()));
            overrideKeyMapping(mappings, keyMapping(KeyCode.DOWN, e -> decrement()));
        });
    }

    private void overrideKeyMapping(ObservableList<Mapping<?>> mappings, InputMap.KeyMapping keyMapping) {
        for (Iterator<Mapping<?>> iterator = mappings.iterator(); iterator.hasNext(); ) {
            Mapping<?> existingMapping = iterator.next();
            if (existingMapping instanceof InputMap.KeyMapping) {
                if (keyMapping.getMappingKey().equals(existingMapping.getMappingKey())) {
                    iterator.remove();
                    break;
                }
            }
        }
        mappings.add(keyMapping);
    }

    private InputMap.KeyMapping keyMapping(KeyCode keyCode, final EventHandler<KeyEvent> eventHandler) {
        return keyMapping(new KeyBinding(keyCode), eventHandler);
    }

    private InputMap.KeyMapping keyMapping(KeyBinding keyBinding, final EventHandler<KeyEvent> eventHandler) {
        TextFieldBehavior behavior = ReflectionUtil.getField(textField.getSkin(), "behavior");
        return new InputMap.KeyMapping(keyBinding,
                e -> {
                    ReflectionUtil.invokeMethod(behavior, "setCaretAnimating", false);
                    eventHandler.handle(e);
                    ReflectionUtil.invokeMethod(behavior, "setCaretAnimating", true);
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

    private class SpinnerButton extends Button {

        private boolean increment;
        private Timeline timeline;

        private final EventHandler<ActionEvent> spinningKeyFrameEventHandler = event -> {
            if (increment) {
                increment();
            } else {
                decrement();
            }
        };

        public SpinnerButton(boolean increment) {
            super("", increment ? Images.SPINNER_UP.getImageView() : Images.SPINNER_DOWN.getImageView());
            setFocusTraversable(false);
            this.increment = increment;
            if (increment) {
                getStyleClass().add("increment-arrow-button");
            } else {
                getStyleClass().add("decrement-arrow-button");
            }

            setOnMousePressed(e -> startSpinning());
            setOnMouseReleased(e -> stopSpinning());
        }

        private void startSpinning() {
            if (timeline != null) {
                timeline.stop();
            }
            timeline = new Timeline();
            timeline.setCycleCount(Timeline.INDEFINITE);
            timeline.setDelay(SPINNER_INITIAL_DELAY);
            final KeyFrame start = new KeyFrame(Duration.ZERO, spinningKeyFrameEventHandler);
            final KeyFrame repeat = new KeyFrame(SPINNER_REPEAT_DELAY);
            timeline.getKeyFrames().setAll(start, repeat);
            timeline.playFromStart();

            spinningKeyFrameEventHandler.handle(null);
        }

        private void stopSpinning() {
            if (timeline != null) {
                timeline.stop();
            }
        }
    }

}
