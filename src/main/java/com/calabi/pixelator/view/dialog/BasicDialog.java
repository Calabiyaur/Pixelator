package com.calabi.pixelator.view.dialog;

import java.util.function.Consumer;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

import com.calabi.pixelator.main.MainScene;
import com.calabi.pixelator.main.Pixelator;

public abstract class BasicDialog extends Dialog<Button> {

    private final BorderPane borderPane = new BorderPane();
    private final GridPane grid = new GridPane();
    protected Button ok = new Button("OK");
    protected Button no = new Button("No");
    protected Button cancel = new Button("Cancel");

    public BasicDialog() {
        this(280, 200);
    }

    public BasicDialog(int minWidth, int minHeight) {
        Pixelator.getStages().forEach(s -> s.setAlwaysOnTop(false));

        initOwner(Pixelator.getPrimaryStage());

        Stage window = (Stage) getDialogPane().getScene().getWindow();
        window.setMinWidth(minWidth);
        window.setMinHeight(minHeight);
        window.setOnCloseRequest(event -> window.hide());

        getDialogPane().getStylesheets().addAll(MainScene.getStyle());

        cancel.setOnAction(e -> close());
        setResult(ok);

        setResizable(true);

        setButtons(ok, cancel);
        getDialogPane().setContent(borderPane);
        borderPane.setStyle("-fx-background-color: -px_empty_area");

        grid.setPrefSize(minWidth - 36, minHeight - 87);
        grid.setHgap(6);
        grid.setVgap(6);
        setDialogContent(grid);

        getDialogPane().setOnKeyPressed(key -> {
            if (KeyCode.ESCAPE.equals(key.getCode())) {
                cancel.fire();
            } else if (KeyCode.ENTER.equals(key.getCode())) {
                ok.fire();
            }
        });
    }

    private void setButtons(Button... buttons) {
        HBox buttonBox = new HBox(buttons);
        buttonBox.setAlignment(Pos.BOTTOM_RIGHT);
        buttonBox.setSpacing(6);
        buttonBox.setPadding(new Insets(6, 0, 0, 0));
        borderPane.setBottom(buttonBox);
    }

    public BasicDialog(Consumer<Button> consumer) {
        this();
        setOnOk(consumer);
    }

    public abstract void focus();

    public void setDialogContent(Region node) {
        borderPane.setCenter(node);
    }

    public void addContent(Node node, int columnIndex, int rowIndex) {
        grid.add(node, columnIndex, rowIndex);
    }

    public void addContent(Node node, int columnIndex, int rowIndex, int colSpan, int rowSpan) {
        grid.add(node, columnIndex, rowIndex, colSpan, rowSpan);
    }

    public void setOkText(String text) {
        ok.setText(text);
    }

    public void addNoButton(String text, Consumer<Button> consumer) {
        no.setText(text);
        no.setOnAction(e -> {
            consumer.accept(no);
            Pixelator.getStages().forEach(s -> s.setAlwaysOnTop(true));
        });
        setButtons(ok, no, cancel);
    }

    public void setOnOk(Consumer<Button> consumer) {
        ok.setOnAction(e -> {
            consumer.accept(ok);
            Pixelator.getStages().forEach(s -> s.setAlwaysOnTop(true));
        });
    }

    public void setOnCancel(Consumer<Button> consumer) {
        cancel.setOnAction(e -> {
            consumer.accept(cancel);
            Pixelator.getStages().forEach(s -> s.setAlwaysOnTop(true));
        });
    }

    public void showAndFocus() {
        focus();
        show();
    }

    public void setPrefSize(double width, double height) {
        setPrefWidth(width);
        setPrefHeight(height);
    }

    public void setPrefWidth(double width) {
        getDialogPane().setPrefWidth(width);
    }

    public void setPrefHeight(double height) {
        getDialogPane().setPrefHeight(height);
    }

    public void setMinSize(double width, double height) {
        setMinWidth(width);
        setMinHeight(height);
    }

    public void setMinWidth(double width) {
        getDialogPane().setMinWidth(width);
    }

    public void setMinHeight(double height) {
        getDialogPane().setMinHeight(height);
    }

}
