package main.java.view.dialog;

import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

import main.java.standard.Point;
import main.java.standard.control.basic.BasicScrollPane;
import main.java.standard.image.ScalableImageView;
import main.java.util.ImageUtil;
import main.java.view.editor.ImagePreview;
import main.java.view.tool.Pick;
import org.apache.logging.log4j.util.TriConsumer;

class Preview extends BasicScrollPane {

    private ScalableImageView imageView;
    private PixelReader reader;
    private PixelWriter writer;
    private boolean enabled = false;

    Preview(Image image) {
        WritableImage writableImage = ImageUtil.createWritableImage(image);
        imageView = new ScalableImageView(writableImage);
        setOnRawScroll(e -> imageView.scroll(e));
        reader = image.getPixelReader();
        writer = writableImage.getPixelWriter();
        setStyle("-fx-background-color: #F4F4F4");

        ImagePreview content = new ImagePreview(imageView);
        setContent(content);

        content.setOnMouseEntered(e -> {
            if (enabled) {
                setCursor(Pick.getMe().getCursor());
            }
        });
        content.setOnMouseExited(e -> setCursor(Cursor.DEFAULT));
    }

    void updateImage(TriConsumer<WritableImage, PixelReader, PixelWriter> action) {
        action.accept((WritableImage) imageView.getImage(), reader, writer);
    }

    WritableImage getImage() {
        return (WritableImage) imageView.getImage();
    }

    public void setOnAction(EventHandler<? super MouseEvent> event) {
        getContent().setOnMousePressed(e -> {
            if (enabled) {
                event.handle(e);
            }
        });
        getContent().setOnMouseDragged(e -> {
            if (enabled) {
                event.handle(e);
            }
        });
    }

    public Color getColor(MouseEvent event) {
        Point mp = getMousePosition(event.getX(), event.getY());
        try {
            return reader.getColor(mp.getX(), mp.getY());
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    public Point getMousePosition(double x, double y) {
        return new Point((int) Math.floor(x / imageView.getScaleX()), (int) Math.floor(y / imageView.getScaleY()));
    }

    public void enable() {
        this.enabled = true;
    }

    public void disable() {
        this.enabled = false;
    }

    public void setEnabled(boolean value) {
        if (value) {
            enable();
        } else {
            disable();
        }
    }
}
