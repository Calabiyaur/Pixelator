package main.java.standard.control.basic;

import javafx.beans.property.BooleanProperty;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Control;

public class BasicCheckBox extends BasicControl<Boolean> {

    private CheckBox checkBox;

    public BasicCheckBox(String title, String tail, boolean value) {
        super(title, tail, value);
    }

    public BasicCheckBox(String title, boolean value) {
        super(title, value);
    }

    @Override
    public Control createControl() {
        checkBox = new CheckBox();
        return checkBox;
    }

    @Override public BooleanProperty valueProperty() {
        return checkBox.selectedProperty();
    }
}
