package com.calabi.pixelator.ui.control;

import java.io.File;

import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.DirectoryChooser;

import com.calabi.pixelator.main.Pixelator;

public class BasicDirectoryField extends BasicControl<File> {

    private TextField textField;

    public BasicDirectoryField(String title) {
        this(title, new File("."));
    }

    public BasicDirectoryField(String title, File value) {
        this(title, null, value);
    }

    public BasicDirectoryField(String title, String tail, File value) {
        super(title, tail, value);

        Button chooseButton = new Button("Choose...");
        chooseButton.setOnAction(e -> {
            DirectoryChooser dialog = new DirectoryChooser();
            if (getValue().exists() && getValue().isDirectory()) {
                dialog.setInitialDirectory(getValue());
            }
            File file = dialog.showDialog(Pixelator.getPrimaryStage());
            if (file != null) {
                setValue(file);
            }
        });
        addControl(chooseButton, 1);

        GridPane.setHgrow(getControlWrapper(), Priority.ALWAYS);
        HBox.setHgrow(textField, Priority.ALWAYS);

        textField.setText(value == null ? "" : value.getAbsolutePath());
        valueProperty().addListener((ov, o, n) -> textField.setText(n.getAbsolutePath()));

        textField.disableProperty().unbind();
        textField.setDisable(true);
    }

    @Override
    public Control createControl() {
        textField = new TextField();
        return textField;
    }

}
