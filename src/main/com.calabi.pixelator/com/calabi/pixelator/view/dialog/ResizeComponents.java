package com.calabi.pixelator.view.dialog;

import javafx.beans.value.ChangeListener;

import com.calabi.pixelator.ui.control.BasicCheckBox;
import com.calabi.pixelator.ui.control.BasicIntegerField;
import com.calabi.pixelator.ui.control.BiasButton;

public class ResizeComponents {

    private double initialWidth;
    private double initialHeight;
    private BasicIntegerField wPercentField;
    private ChangeListener<Integer> wPercentChangeListener;
    private BasicIntegerField widthField;
    private ChangeListener<Integer> widthChangeListener;
    private BasicIntegerField hPercentField;
    private ChangeListener<Integer> hPercentChangeListener;
    private BasicIntegerField heightField;
    private ChangeListener<Integer> heightChangeListener;
    private BasicCheckBox keepRatio;
    private ChangeListener<Boolean> keepRatioChangeListener;
    private BiasButton biasButton;
    private boolean widthWasChangedLast = true;

    public ResizeComponents(int initialWidth, int initialHeight) {
        this.initialWidth = initialWidth;
        this.initialHeight = initialHeight;

        wPercentField = new BasicIntegerField("Width", "%", 100);
        widthField = new BasicIntegerField(null, "pixels", initialWidth);
        hPercentField = new BasicIntegerField("Height", "%", 100);
        heightField = new BasicIntegerField(null, "pixels", initialHeight);
        keepRatio = new BasicCheckBox("Keep ratio", false);
        biasButton = new BiasButton();

        wPercentField.valueProperty().addListener(getWPercentChangeListener());
        widthField.valueProperty().addListener(getWidthChangeListener());
        hPercentField.valueProperty().addListener(getHPercentChangeListener());
        heightField.valueProperty().addListener(getHeightChangeListener());
        keepRatio.valueProperty().addListener(getKeepRatioChangeListener());
    }

    private ChangeListener<Integer> getWPercentChangeListener() {
        if (wPercentChangeListener == null) {
            wPercentChangeListener = (ov, o, n) -> {
                Integer newPercentage = wPercentField.getValue();
                if (newPercentage != null) {
                    setWidth(initialWidth * newPercentage.doubleValue() / 100d);
                    if (keepRatio.getValue()) {
                        setHPercent(newPercentage);
                        setHeight(initialHeight * newPercentage.doubleValue() / 100d);
                    }
                }
                widthWasChangedLast = true;
            };
        }
        return wPercentChangeListener;
    }

    private ChangeListener<Integer> getWidthChangeListener() {
        if (widthChangeListener == null) {
            widthChangeListener = (ov, o, n) -> {
                Integer newValue = widthField.getValue();
                if (newValue != null) {
                    double newPercentage = newValue.doubleValue() / initialWidth * 100;
                    setWPercent(newPercentage);
                    if (keepRatio.getValue()) {
                        setHPercent(newPercentage);
                        setHeight(initialHeight * newPercentage / 100d);
                    }
                }
                widthWasChangedLast = true;
            };
        }
        return widthChangeListener;
    }

    private ChangeListener<Integer> getHPercentChangeListener() {
        if (hPercentChangeListener == null) {
            hPercentChangeListener = (ov, o, n) -> {
                Integer newPercentage = hPercentField.getValue();
                if (newPercentage != null) {
                    setHeight(initialHeight * newPercentage.doubleValue() / 100d);
                    if (keepRatio.getValue()) {
                        setWPercent(newPercentage);
                        setWidth(initialWidth * newPercentage.doubleValue() / 100d);
                    }
                }
                widthWasChangedLast = false;
            };
        }
        return hPercentChangeListener;
    }

    private ChangeListener<Integer> getHeightChangeListener() {
        if (heightChangeListener == null) {
            heightChangeListener = (ov, o, n) -> {
                Integer newValue = heightField.getValue();
                if (newValue != null) {
                    double newPercentage = newValue.doubleValue() / initialHeight * 100;
                    setHPercent(newPercentage);
                    if (keepRatio.getValue()) {
                        setWPercent(newPercentage);
                        setWidth(initialWidth * newPercentage / 100d);
                    }
                }
                widthWasChangedLast = false;
            };
        }
        return heightChangeListener;
    }

    private ChangeListener<Boolean> getKeepRatioChangeListener() {
        if (keepRatioChangeListener == null) {
            keepRatioChangeListener = (ov, o, n) -> {
                if (n) {
                    if (widthWasChangedLast) {
                        Integer newPercentage = wPercentField.getValue();
                        setHPercent(newPercentage);
                        setHeight(initialHeight * newPercentage.doubleValue() / 100d);
                    } else {
                        Integer newPercentage = hPercentField.getValue();
                        setWPercent(newPercentage);
                        setWidth(initialWidth * newPercentage.doubleValue() / 100d);
                    }
                }
            };
        }
        return keepRatioChangeListener;
    }

    private void setWidth(double newValue) {
        widthField.valueProperty().removeListener(widthChangeListener);
        widthField.setValue((int) newValue);
        widthField.valueProperty().addListener(widthChangeListener);
    }

    private void setWPercent(double newValue) {
        wPercentField.valueProperty().removeListener(wPercentChangeListener);
        wPercentField.setValue((int) newValue);
        wPercentField.valueProperty().addListener(wPercentChangeListener);
    }

    private void setHeight(double newValue) {
        heightField.valueProperty().removeListener(heightChangeListener);
        heightField.setValue((int) newValue);
        heightField.valueProperty().addListener(heightChangeListener);
    }

    private void setHPercent(double newValue) {
        hPercentField.valueProperty().removeListener(hPercentChangeListener);
        hPercentField.setValue((int) newValue);
        hPercentField.valueProperty().addListener(hPercentChangeListener);
    }

    public BasicIntegerField getWPercentField() {
        return wPercentField;
    }

    public BasicIntegerField getWidthField() {
        return widthField;
    }

    public BasicIntegerField getHPercentField() {
        return hPercentField;
    }

    public BasicIntegerField getHeightField() {
        return heightField;
    }

    public BasicCheckBox getKeepRatio() {
        return keepRatio;
    }

    public BiasButton getBiasButton() {
        return biasButton;
    }

}
