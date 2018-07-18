package main.java.view.dialog;

import javafx.beans.value.ChangeListener;

import main.java.control.basic.BiasButton;
import main.java.control.basic.BasicCheckBox;
import main.java.control.basic.BasicTextField;

public class ResizeComponents {

    private double initialWidth;
    private double initialHeight;
    private BasicTextField wPercentField;
    private ChangeListener<String> wPercentChangeListener;
    private BasicTextField widthField;
    private ChangeListener<String> widthChangeListener;
    private BasicTextField hPercentField;
    private ChangeListener<String> hPercentChangeListener;
    private BasicTextField heightField;
    private ChangeListener<String> heightChangeListener;
    private BasicCheckBox keepRatio;
    private ChangeListener<Boolean> keepRatioChangeListener;
    private BiasButton biasButton;
    private boolean widthWasChangedLast = true;

    public ResizeComponents(int initialWidth, int initialHeight) {
        this.initialWidth = initialWidth;
        this.initialHeight = initialHeight;

        wPercentField = new BasicTextField("Width", "%", 100);
        widthField = new BasicTextField(null, "pixels", initialWidth);
        hPercentField = new BasicTextField("Height", "%", 100);
        heightField = new BasicTextField(null, "pixels", initialHeight);
        keepRatio = new BasicCheckBox("Keep ratio", false);
        biasButton = new BiasButton();

        wPercentField.valueProperty().addListener(getWPercentChangeListener());
        widthField.valueProperty().addListener(getWidthChangeListener());
        hPercentField.valueProperty().addListener(getHPercentChangeListener());
        heightField.valueProperty().addListener(getHeightChangeListener());
        keepRatio.valueProperty().addListener(getKeepRatioChangeListener());
    }

    private ChangeListener<String> getWPercentChangeListener() {
        if (wPercentChangeListener == null) {
            wPercentChangeListener = (ov, o, n) -> {
                Integer newPercentage = wPercentField.getIntValue();
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

    private ChangeListener<String> getWidthChangeListener() {
        if (widthChangeListener == null) {
            widthChangeListener = (ov, o, n) -> {
                Integer newValue = widthField.getIntValue();
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

    private ChangeListener<String> getHPercentChangeListener() {
        if (hPercentChangeListener == null) {
            hPercentChangeListener = (ov, o, n) -> {
                Integer newPercentage = hPercentField.getIntValue();
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

    private ChangeListener<String> getHeightChangeListener() {
        if (heightChangeListener == null) {
            heightChangeListener = (ov, o, n) -> {
                Integer newValue = heightField.getIntValue();
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
                        Integer newPercentage = wPercentField.getIntValue();
                        setHPercent(newPercentage);
                        setHeight(initialHeight * newPercentage.doubleValue() / 100d);
                    } else {
                        Integer newPercentage = hPercentField.getIntValue();
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
        widthField.setValue(String.valueOf((int) newValue));
        widthField.valueProperty().addListener(widthChangeListener);
    }

    private void setWPercent(double newValue) {
        wPercentField.valueProperty().removeListener(wPercentChangeListener);
        wPercentField.setValue(String.valueOf((int) newValue));
        wPercentField.valueProperty().addListener(wPercentChangeListener);
    }

    private void setHeight(double newValue) {
        heightField.valueProperty().removeListener(heightChangeListener);
        heightField.setValue(String.valueOf((int) newValue));
        heightField.valueProperty().addListener(heightChangeListener);
    }

    private void setHPercent(double newValue) {
        hPercentField.valueProperty().removeListener(hPercentChangeListener);
        hPercentField.setValue(String.valueOf((int) newValue));
        hPercentField.valueProperty().addListener(hPercentChangeListener);
    }

    public BasicTextField getWPercentField() {
        return wPercentField;
    }

    public BasicTextField getWidthField() {
        return widthField;
    }

    public BasicTextField getHPercentField() {
        return hPercentField;
    }

    public BasicTextField getHeightField() {
        return heightField;
    }

    public BasicCheckBox getKeepRatio() {
        return keepRatio;
    }

    public BiasButton getBiasButton() {
        return biasButton;
    }

}
