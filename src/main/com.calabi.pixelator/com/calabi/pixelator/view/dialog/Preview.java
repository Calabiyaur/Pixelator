package com.calabi.pixelator.view.dialog;

import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

import org.apache.logging.log4j.util.TriConsumer;

import com.calabi.pixelator.control.image.ScalableImageView;
import com.calabi.pixelator.control.parent.BasicScrollPane;
import com.calabi.pixelator.meta.Point;
import com.calabi.pixelator.util.ImageUtil;
import com.calabi.pixelator.view.editor.ImagePreview;
import com.calabi.pixelator.view.tool.Pick;

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

        ImagePreview content = new ImagePreview(imageView);
        content.setStyle("-fx-background-color: #DDDDDD");
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

    ScalableImageView getImageView() {
        return imageView;
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
