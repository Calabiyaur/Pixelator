package main.java.control.basic;

import java.util.ArrayList;
import java.util.List;

import javafx.application.Platform;
import javafx.beans.property.Property;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

public abstract class BasicControl<T> extends GridPane {

    private Label titleText;
    private Control control;
    private Label tailText;

    public BasicControl(String title, String tail, T value) {
        List<Control> children = new ArrayList<>();
        if (title != null) {
            titleText = new Label(title + ": ");
            children.add(titleText);
        }
        control = createControl();
        children.add(control);
        if (tail != null) {
            tailText = new Label(" " + tail);
            children.add(tailText);
        }
        addRow(0, children.toArray(new Control[] {}));

        GridPane.setHalignment(control, HPos.RIGHT);
        adjustWidth();
        setAlignment(Pos.CENTER_RIGHT);

        setValue(value);
    }

    private void adjustWidth() {
        setMinWidth(120);
        control.setPrefWidth(120 / getChildren().size());
    }

    public BasicControl(String title, T value) {
        this(title, null, value);
    }

    public final void focus() {
        Platform.runLater(() -> control.requestFocus());
    }

    protected abstract Control createControl();

    public Control getControl() {
        return control;
    }

    public Label getFrontLabel() {
        return titleText;
    }

    public Label getBackLabel() {
        return tailText;
    }

    protected final void addControl(Control control, int columnIndex) {
        getChildren().forEach(c -> {
            int oldIndex = getColumnIndex(c);
            if (oldIndex >= columnIndex) {
                setColumnIndex(c, oldIndex + 1);
            }
        });
        getChildren().add(control);
        setConstraints(control, columnIndex, 0);
        adjustWidth();
    }

    public T getValue() {
        return valueProperty().getValue();
    }

    public void setValue(T value) {
        valueProperty().setValue(value);
    }

    public abstract Property<T> valueProperty();

    public final String getTitle() {
        return titleText.getText();
    }

    public final void setTitle(String title) {
        this.titleText.setText(title);
    }

    public final String getTail() {
        return tailText.getText();
    }

    public final void setTail(String tail) {
        this.tailText.setText(tail);
    }
}
