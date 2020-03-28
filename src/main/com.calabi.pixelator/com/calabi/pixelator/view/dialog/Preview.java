package com.calabi.pixelator.view.dialog;

import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import org.apache.logging.log4j.util.TriConsumer;

import com.calabi.pixelator.control.basic.ImageButton;
import com.calabi.pixelator.control.basic.ToggleImageButton;
import com.calabi.pixelator.control.image.ScalableImageView;
import com.calabi.pixelator.control.image.WritableImage;
import com.calabi.pixelator.control.parent.BasicScrollPane;
import com.calabi.pixelator.control.region.BalloonRegion;
import com.calabi.pixelator.meta.Point;
import com.calabi.pixelator.res.Images;
import com.calabi.pixelator.util.Do;
import com.calabi.pixelator.view.editor.ImagePreview;
import com.calabi.pixelator.view.tool.Pick;

class Preview extends VBox {

    private final WritableImage original;
    private final BasicScrollPane scrollPane;
    private final ScalableImageView imageView;
    private final PixelReader reader;
    private final PixelWriter writer;
    private int animationStart = 0;
    private boolean enabled = false;
    private ToggleImageButton play;

    Preview(WritableImage image) {
        original = image;
        WritableImage writableImage = image.copy();
        imageView = new ScalableImageView(writableImage);
        scrollPane = new BasicScrollPane();
        scrollPane.setOnRawScroll(e -> imageView.scroll(e));
        reader = image.getPixelReader();
        writer = writableImage.getPixelWriter();

        ImagePreview imagePreview = new ImagePreview(imageView);
        imagePreview.setStyle("-fx-background-color: #DDDDDD");
        scrollPane.setContent(imagePreview);
        getChildren().add(scrollPane);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        if (image.isAnimated()) {
            ImageButton previousFrame = new ImageButton(Images.PREVIOUS_FRAME);
            previousFrame.setOnAction(e -> {
                stopAnimation();
                writableImage.previous();
            });
            ImageButton nextFrame = new ImageButton(Images.NEXT_FRAME);
            nextFrame.setOnAction(e -> {
                stopAnimation();
                writableImage.next();
            });
            play = new ToggleImageButton(Images.PLAY, Images.PAUSE);
            play.selectedProperty().addListener((ov, o, n) -> Do.when(n, () -> startAnimation(), () -> stopAnimation()));

            HBox buttons = new HBox(new BalloonRegion(), previousFrame, nextFrame, play, new BalloonRegion());
            buttons.setMinWidth(0);

            setSpacing(4);
            getChildren().add(buttons);
        }

        imagePreview.setOnMouseEntered(e -> {
            if (enabled) {
                setCursor(Pick.getMe().getCursor());
            }
        });
        imagePreview.setOnMouseExited(e -> setCursor(Cursor.DEFAULT));
    }

    void updateImage(TriConsumer<WritableImage, PixelReader, PixelWriter> action) {
        WritableImage image = (WritableImage) imageView.getImage();
        if (image.isAnimated()) {
            for (int i = 0; i < image.getFrameCount(); i++) {
                PixelReader frameReader = original.getPixelReader(i);
                PixelWriter frameWriter = image.getPixelWriter(i);
                action.accept(image, frameReader, frameWriter);
            }
        } else {
            action.accept(image, reader, writer);
        }
    }

    WritableImage getImage() {
        return (WritableImage) imageView.getImage();
    }

    ScalableImageView getImageView() {
        return imageView;
    }

    public void setOnAction(EventHandler<? super MouseEvent> event) {
        scrollPane.getContent().setOnMousePressed(e -> {
            if (enabled) {
                event.handle(e);
            }
        });
        scrollPane.getContent().setOnMouseDragged(e -> {
            if (enabled) {
                event.handle(e);
            }
        });
    }

    public Color getColor(MouseEvent event) {
        Point mp = getMousePosition(event.getX(), event.getY());
        try {
            PixelReader r;
            if (getImage().isAnimated()) {
                r = original.getPixelReader(getImage().getIndex());
            } else {
                r = reader;
            }
            return r.getColor(mp.getX(), mp.getY());
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

    private void startAnimation() {
        animationStart = getImage().getIndex();
        getImage().play();
    }

    private void stopAnimation() {
        if (getImage().stop()) {
            getImage().setIndex(animationStart);
            play.setSelected(false);
        }
    }

}
