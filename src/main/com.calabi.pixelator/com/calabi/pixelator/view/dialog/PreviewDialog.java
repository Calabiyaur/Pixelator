package com.calabi.pixelator.view.dialog;

import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;

import org.apache.logging.log4j.util.TriConsumer;

import com.calabi.pixelator.control.basic.BasicControl;
import com.calabi.pixelator.control.image.WritableImage;

public abstract class PreviewDialog extends BasicDialog {

    private GridPane leftContent;
    private Preview preview;

    public PreviewDialog(WritableImage image) {
        setPrefSize(480, 320);

        leftContent = new GridPane();
        leftContent.setHgap(6);
        leftContent.setVgap(6);
        preview = new Preview(image);

        super.addContent(leftContent, 0, 0);
        super.addContent(preview, 1, 0);
        GridPane.setMargin(preview, new Insets(0, 0, 0, 10));
        GridPane.setHgrow(preview, Priority.ALWAYS);
        GridPane.setVgrow(preview, Priority.ALWAYS);
    }

    public void updateImage(TriConsumer<WritableImage, PixelReader, PixelWriter> action) {
        preview.updateImage(action);
    }

    protected final void listenToUpdate(BasicControl<?>... controls) {
        for (BasicControl<?> control : controls) {
            control.valueProperty().addListener(o -> Platform.runLater(() -> updateImage()));
            //TODO: Double performance of swap button (outline dialog) by buffering updates
        }
    }

    protected final void listenToUpdate(Observable... observables) {
        for (Observable observable : observables) {
            observable.addListener(o -> Platform.runLater(() -> updateImage()));
        }
    }

    @SafeVarargs
    protected final void addColorControl(BasicControl<Color>... controls) {
        for (BasicControl<Color> control : controls) {
            control.getControl().focusedProperty().addListener((ov, o, n) -> {
                preview.setEnabled(n);
                if (n) {
                    preview.setOnAction(e -> {
                        Color color = preview.getColor(e);
                        if (color != null) {
                            control.setValue(color);
                        }
                    });
                }
            });
        }
    }

    protected abstract void updateImage();

    @Override
    public void addContent(Node node, int columnIndex, int rowIndex) {
        leftContent.add(node, columnIndex, rowIndex);
    }

    @Override
    public void addContent(Node node, int columnIndex, int rowIndex, int colSpan, int rowSpan) {
        leftContent.add(node, columnIndex, rowIndex, colSpan, rowSpan);
    }

    public Preview getPreview() {
        return preview;
    }

    public WritableImage getImage() {
        return preview.getImage();
    }

}
