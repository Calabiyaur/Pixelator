package main.java.view.dialog;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;

import main.java.control.basic.BasicControl;
import org.apache.logging.log4j.util.TriConsumer;

public abstract class PreviewDialog extends BasicDialog {

    private GridPane leftContent;
    private Preview preview;

    public PreviewDialog(Image image) {
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

    protected final void listenToUpdate(BasicControl... controls) {
        for (BasicControl control : controls) {
            control.valueProperty().addListener((ov, o, n) -> Platform.runLater(() -> updateImage()));
        }
    }

    @SafeVarargs
    protected final void addColorControl(BasicControl<Color>... controls) {
        for (BasicControl<Color> control : controls) {
            control.getControl().focusedProperty().addListener((ov, o, n) -> {
                preview.setEnabled(n);
                if (n) {
                    preview.setOnAction(e -> control.setValue(preview.getColor(e)));
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

    public Image getImage() {
        return preview.getImage();
    }

}
