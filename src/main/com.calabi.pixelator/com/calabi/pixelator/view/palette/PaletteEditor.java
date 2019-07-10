package com.calabi.pixelator.view.palette;

import java.io.File;

import javafx.beans.value.ChangeListener;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

import com.calabi.pixelator.control.image.PixelatedImageView;
import com.calabi.pixelator.files.PaletteFile;
import com.calabi.pixelator.meta.Point;
import com.calabi.pixelator.util.ImageUtil;
import com.calabi.pixelator.view.ColorView;
import com.calabi.pixelator.view.editor.Editor;
import com.calabi.pixelator.view.tool.Pick;

public class PaletteEditor extends Editor {

    public static int DEFAULT_WIDTH = 8;
    public static int DEFAULT_HEIGHT = 5;

    public PaletteEditor(PaletteFile file) {
        super(file, new PixelatedImageView(ImageUtil.makeWritableIfNot(file.getImage())));
        getFile().setImage(getImage());
        PixelatedImageView imageView = getImageView();
        undirty();

        imageView.setScaleX(10);
        imageView.setScaleY(10);
        ChangeListener<Image> imageChangeListener = (ov, o, n) -> {
            imageView.translateXProperty().bind(n.widthProperty()
                    .multiply(imageView.scaleXProperty()).divide(2).subtract(n.widthProperty().divide(2)));
            imageView.translateYProperty().bind(n.heightProperty()
                    .multiply(imageView.scaleYProperty()).divide(2).subtract(n.heightProperty().divide(2)));
        };
        imageView.imageProperty().addListener(imageChangeListener);
        imageChangeListener.changed(null, null, imageView.getImage());

        imageView.setPickOnBounds(true);
        imageView.setOnMouseEntered(e -> setCursor(Pick.getMe().getCursor()));
        imageView.setOnMousePressed(e -> onMousePressed(e));
        imageView.setOnMouseDragged(e -> onMouseDragged(e));
        imageView.setOnMouseExited(e -> setCursor(Cursor.DEFAULT));

        getChildren().addAll(imageView);
        StackPane.setAlignment(imageView, Pos.TOP_LEFT);
    }

    private void onMousePressed(MouseEvent event) {
        choose(event);
    }

    private void onMouseDragged(MouseEvent e) {
        choose(e);
    }

    private void choose(MouseEvent event) {
        Point mp = getMousePosition(event.getX(), event.getY());
        if (ImageUtil.outOfBounds(getImage(), mp)) {
            return;
        }
        takeColor(mp.getX(), mp.getY());
    }

    private void takeColor(int x, int y) {
        Color color = getImage().getPixelReader().getColor(x, y);
        ColorView.setColor(color);
    }

    @Override
    public void updateImage(Image image) {
        getImageView().setImage(image);
        getFile().setImage(image);
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

}
