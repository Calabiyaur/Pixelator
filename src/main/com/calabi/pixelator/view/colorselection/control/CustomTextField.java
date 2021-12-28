package com.calabi.pixelator.view.colorselection.control;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;

import com.calabi.pixelator.ui.control.BasicDoubleField;

public class CustomTextField extends BasicDoubleField {

    private DoubleProperty target;
    private ObjectProperty<Double> targetAsObject;

    public CustomTextField() {
        super("Temp", 0.);
        setPrecision(0);
    }

    public DoubleProperty getTarget() {
        return target;
    }

    public void setTarget(DoubleProperty target) {
        this.target = target;
    }

    public ObjectProperty<Double> getTargetAsObject() {
        return targetAsObject;
    }

    public void setTargetAsObject(ObjectProperty<Double> targetAsObject) {
        this.targetAsObject = targetAsObject;
    }

}
