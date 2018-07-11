package main.java.view.palette;

import java.io.File;

import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import main.java.files.PaletteFile;
import main.java.standard.Point;
import main.java.standard.image.PixelatedImageView;
import main.java.util.ImageUtil;
import main.java.view.ColorView;
import main.java.view.dialog.ColorDialog;
import main.java.view.editor.Editor;
import main.java.view.tool.Pick;
import main.java.view.undo.PixelChange;

public class PaletteEditor extends Editor {

    public static int DEFAULT_WIDTH = 6;
    public static int DEFAULT_HEIGHT = 4;
    private Point selected;
    private Rectangle selection;
    private PaletteFile file;

    public PaletteEditor(PaletteFile file) {
        super(new PixelatedImageView(ImageUtil.makeWritableIfNot(file.getImage())));
        PixelatedImageView imageView = getImageView();
        setCleanImage(ImageUtil.createWritableImage(getImage()));

        imageView.setPickOnBounds(true);
        imageView.setOnMousePressed(e -> onMouseClicked(e));
        imageView.setOnMouseDragged(e -> choose(e));
        imageView.setOnMouseEntered(e -> setCursor(Pick.getMe().getCursor()));
        imageView.setOnMouseExited(e -> setCursor(Cursor.DEFAULT));

        this.file = file;

        selection = new Rectangle();
        selection.widthProperty().bind(imageView.scaleXProperty().add(2));
        selection.heightProperty().bind(imageView.scaleYProperty().add(2));
        selection.setFill(Color.TRANSPARENT);
        selection.setStroke(Color.BLACK);
        selection.setStrokeWidth(2);
        selection.setDisable(true);

        select(new Point(0, 0)); //TODO: Use values from palette's properties file

        getChildren().addAll(imageView, selection);
        StackPane.setAlignment(imageView, Pos.TOP_LEFT);
        StackPane.setAlignment(selection, Pos.TOP_LEFT);
        init(imageView);
    }

    private void init(PixelatedImageView imageView) {
        prefHeightProperty().bind(imageView.heightProperty().multiply(imageView.scaleYProperty()));
    }

    private void onMouseClicked(MouseEvent event) {
        if (event.getClickCount() == 2) {
            ColorDialog.chooseColor(ColorView.getColor(), color -> setColor(color));
        } else {
            choose(event);
        }
    }

    private void choose(MouseEvent event) {
        Point mp = getMousePosition(event.getX(), event.getY());
        if (ImageUtil.outOfBounds(getImage(), mp)) {
            return;
        }
        select(mp);
        takeColor();
    }

    private void takeColor() {
        Color color = getImage().getPixelReader().getColor(selected.getX(), selected.getY());
        ColorView.setColor(color);
    }

    private void select(Point position) {
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

    private Point getMousePosition(double x, double y) {
        return new Point((int) Math.floor(x), (int) Math.floor(y));
    }

    public PaletteFile getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file.setFile(file);
    }

    public double getActualHeight() {
        return getImageView().getHeight() * getImageView().getScaleY();
    }
}
