package main.java.view.palette;

import java.io.File;

import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import main.java.control.image.PixelatedImageView;
import main.java.files.PaletteFile;
import main.java.meta.Point;
import main.java.util.ImageUtil;
import main.java.view.ColorView;
import main.java.view.dialog.ColorDialog;
import main.java.view.editor.Editor;
import main.java.view.tool.Pick;
import main.java.view.undo.PixelChange;

public class PaletteEditor extends Editor {

    public static int DEFAULT_WIDTH = 8;
    public static int DEFAULT_HEIGHT = 5;
    private Point selected;
    private Rectangle selection;
    private boolean dragging = false;
    private boolean locked = true;

    public PaletteEditor(PaletteFile file) {
        super(file, new PixelatedImageView(ImageUtil.makeWritableIfNot(file.getImage())));
        getFile().setImage(getImage());
        PixelatedImageView imageView = getImageView();
        Image image = imageView.getImage();
        undirty();

        imageView.setScaleX(20);
        imageView.setScaleY(20);
        imageView.translateXProperty().bind(image.widthProperty()
                .multiply(imageView.scaleXProperty()).divide(2).subtract(image.widthProperty().divide(2)));
        imageView.translateYProperty().bind(image.heightProperty()
                .multiply(imageView.scaleYProperty()).divide(2).subtract(image.heightProperty().divide(2)));

        imageView.setPickOnBounds(true);
        imageView.setOnMousePressed(e -> onMousePressed(e));
        imageView.setOnMouseDragged(e -> onMouseDragged(e));
        imageView.setOnMouseReleased(e -> onMouseReleased(e));
        imageView.setOnMouseMoved(e -> onMouseMoved(e));
        imageView.setOnMouseExited(e -> setCursor(Cursor.DEFAULT));

        selection = new Rectangle();
        selection.widthProperty().bind(imageView.scaleXProperty().add(2));
        selection.heightProperty().bind(imageView.scaleYProperty().add(2));
        selection.setFill(Color.TRANSPARENT);
        selection.setStroke(Color.BLACK);
        selection.setStrokeWidth(2);
        selection.setDisable(true);

        select(new Point(0, 0));

        getChildren().addAll(imageView/*, selection*/);
        StackPane.setAlignment(imageView, Pos.TOP_LEFT);
        StackPane.setAlignment(selection, Pos.TOP_LEFT);
    }

    private void onMousePressed(MouseEvent event) {
        if (event.getClickCount() == 2) {
            ColorDialog.chooseColor(ColorView.getColor(), color -> setColor(color));
        } else if (!locked && getMousePosition(event.getX(), event.getY()).equals(selected)) {
            startDragging();
        } else {
            choose(event);
        }
    }

    private void onMouseDragged(MouseEvent e) {
        if (dragging) {
            selection.setTranslateX((getImageView().getScaleX() * (e.getX() - 0.5)) - 2);
            selection.setTranslateY((getImageView().getScaleY() * (e.getY() - 0.5)) - 2);
        } else if (!locked) {
            startDragging();
        } else {
            choose(e);
        }
    }

    private void startDragging() {
        setCursor(Cursor.CLOSED_HAND);
        selection.translateXProperty().unbind();
        selection.translateYProperty().unbind();
        selection.setFill(getImage().getPixelReader().getColor(selected.getX(), selected.getY()));
        dragging = true;
    }

    private void onMouseReleased(MouseEvent e) {
        if (dragging) {
            Point mp = getMousePosition(e.getX(), e.getY());

            if (isInBounds(mp)) {
                PixelReader reader = getImage().getPixelReader();
                PixelWriter writer = ((WritableImage) getImage()).getPixelWriter();

                Color color = reader.getColor(selected.getX(), selected.getY());
                Color otherColor = reader.getColor(mp.getX(), mp.getY());

                if (!e.isControlDown()) {
                    writer.setColor(selected.getX(), selected.getY(), otherColor);
                }
                writer.setColor(mp.getX(), mp.getY(), color);

                PixelChange change = new PixelChange(writer);
                change.add(selected.getX(), selected.getY(), color, otherColor);
                change.add(mp.getX(), mp.getY(), otherColor, color);
                register(change);
                updateDirty();
            }

            dragging = false;
            setCursor(Cursor.OPEN_HAND);
            selection.setFill(Color.TRANSPARENT);

            choose(e);
        }
    }

    private boolean isInBounds(Point position) {
        return 0 <= position.getX() && position.getX() < getImage().getWidth()
                && 0 <= position.getY() && position.getY() < getImage().getHeight();
    }

    private void onMouseMoved(MouseEvent e) {
        if (!locked && getMousePosition(e.getX(), e.getY()).equals(selected)) {
            setCursor(Cursor.OPEN_HAND);
        } else {
            setCursor(Pick.getMe().getCursor());
        }
    }

    private void choose(MouseEvent event) {
        Point mp = getMousePosition(event.getX(), event.getY());
        if (ImageUtil.outOfBounds(getImage(), mp)) {
            return;
        }
        select(mp);
        if (event.isControlDown()) {
            setColor(ColorView.getColor());
        } else {
            takeColor();
        }
    }

    private void takeColor() {
        Color color = getImage().getPixelReader().getColor(selected.getX(), selected.getY());
        ColorView.setColor(color);
    }

    void select(Point position) {
        selected = position;
        selection.translateXProperty().bind(getImageView().scaleXProperty().multiply(position.getX()).subtract(2));
        selection.translateYProperty().bind(getImageView().scaleYProperty().multiply(position.getY()).subtract(2));
    }

    public void setColor(Color color) {
        WritableImage image = (WritableImage) getImageView().getImage();
        Color previousColor = image.getPixelReader().getColor(selected.getX(), selected.getY());
        image.getPixelWriter().setColor(selected.getX(), selected.getY(), color);

        PixelChange change = new PixelChange(image.getPixelWriter());
        change.add(selected.getX(), selected.getY(), previousColor, color);
        register(change);

        ColorView.setColor(color);
    }

    public void moveSelection(int right, int down) {
        Point point = new Point(selected.getX() + right, selected.getY() + down);
        if (!ImageUtil.outOfBounds(getImage(), point)) {
            select(point);
            takeColor();
        }
    }

    private Point getMousePosition(double x, double y) {
        return new Point((int) Math.floor(x), (int) Math.floor(y));
    }

    public PaletteFile getFile() {
        return (PaletteFile) getPixelFile();
    }

    public void setFile(File file) {
        this.getPixelFile().setFile(file);
    }

    public Point getSelected() {
        return selected;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }
}
