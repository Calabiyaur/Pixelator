package com.calabi.pixelator.view.editor;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.paint.Color;

import org.apache.commons.lang3.tuple.Pair;

import com.calabi.pixelator.meta.Pixel;
import com.calabi.pixelator.meta.Point;
import com.calabi.pixelator.meta.PointArray;
import com.calabi.pixelator.ui.image.OutlineRect;
import com.calabi.pixelator.ui.image.OutlineShape;
import com.calabi.pixelator.util.ImageUtil;
import com.calabi.pixelator.view.InfoView;
import com.calabi.pixelator.view.ToolView;
import com.calabi.pixelator.view.undo.PixelChange;

public class SelectionLayer extends EditorLayer {

    private final BooleanProperty active = new SimpleBooleanProperty(false);

    private Point drag;
    private boolean pasted = false;
    private OutlineRect outlineRect;
    private OutlineShape outlineShape;

    public SelectionLayer(int width, int height, PixelReader reader) {
        super(width, height, reader);
        ToolView.get().replaceColorProperty().addListener((ov, o, n) -> {
            if (n) {
                setStyle("-fx-background-color: #DDDDDD"); //FIXME: This doesn't work.
            } else {
                setStyle("-fx-background-color: transparent");
            }
        });
    }

    @Override
    public void setStart(Point position) {
        super.setStart(position);
        if (position != null) {
            active.set(true);
        } else {
            outlineRect.clear();
        }
    }

    @Override
    void resize(int width, int height, PixelReader reader) {
        super.resize(width, height, reader);
        outlineRect.resize(width, height);
        outlineShape.resize(width, height);
    }

    public boolean contains(Point p) {
        if (p == null) {
            return false;
        }
        if (!active.get()) {
            return false;
        }
        int dX = (int) (getTranslateX() / getScaleX());
        int dY = (int) (getTranslateY() / getScaleY());
        return p.getX() - dX >= 0
                && p.getY() - dY >= 0
                && getPixels().contains(p.getX() - dX, p.getY() - dY);
    }

    public boolean isEmpty() {
        return getPixels().isEmpty();
    }

    public boolean isPasted() {
        return pasted;
    }

    public void definePixels(PointArray points) {
        if (points.getPoints().isEmpty()) {
            clear();
            return;
        }
        clearImage();
        getPixels().reset();

        PointArray effectivePoints = new PointArray();
        int xMin = Integer.MAX_VALUE, yMin = Integer.MAX_VALUE, xMax = 0, yMax = 0;
        for (Point point : points.getPoints()) {
            int x = point.getX();
            int y = point.getY();
            if (ImageUtil.outOfBounds(getImage(), x, y)) {
                continue;
            }
            effectivePoints.add(x, y);
            xMin = Math.min(xMin, x);
            yMin = Math.min(yMin, y);
            xMax = Math.max(xMax, x);
            yMax = Math.max(yMax, y);
            Color color = getReader().getColor(x, y);
            getWriter().setColor(x, y, color);
            getPixels().add(x, y, null, color);
        }
        active.set(true);
        outlineShape.define(effectivePoints);
        outlineRect.clear();
        InfoView.setSelectionSize(new Point(xMin, yMin), new Point(xMax, yMax));
    }

    public void defineShape(PointArray points) {
        outlineShape.define(points);
    }

    public void defineImage(Image image, boolean pasted) {
        clear();
        PointArray points = new PointArray();

        PixelReader reader = image.getPixelReader();
        int xMin = Integer.MAX_VALUE, yMin = Integer.MAX_VALUE, xMax = 0, yMax = 0;
        for (int i = 0; i < image.getWidth(); i++) {
            for (int j = 0; j < image.getHeight(); j++) {
                if (ImageUtil.outOfBounds(getImage(), i, j)) {
                    continue;
                }
                Color color = reader.getColor(i, j);
                getWriter().setColor(i, j, color);
                getPixels().add(i, j, Color.TRANSPARENT, color);
                if (!Color.TRANSPARENT.equals(color)) {
                    points.add(i, j);
                    xMin = Math.min(xMin, i);
                    yMin = Math.min(yMin, j);
                    xMax = Math.max(xMax, i);
                    yMax = Math.max(yMax, j);
                }
            }
        }
        active.set(true);
        this.pasted = pasted;
        outlineShape.define(points);
        InfoView.setSelectionSize(new Point(xMin, yMin), new Point(xMax, yMax));
    }

    public void setEdges(Point start, Point end) {
        outlineRect.setEdges(start.getX(), start.getY(), end.getX(), end.getY());
    }

    /**
     * Returns the selection layer's pixels AND clears them.
     */
    PixelChange retrievePixels() {
        PixelChange result = getPixelsTransformed();
        clear();
        setStart(null);
        return result;
    }

    /**
     * Get pixels, but update their position first.
     */
    public PixelChange getPixelsTransformed() {
        PixelChange result = new PixelChange(getWriter());

        int dX = (int) (getTranslateX() / getScaleX());
        int dY = (int) (getTranslateY() / getScaleY());

        for (Pixel pixel : getPixels().getPoints()) {

            int newX = pixel.getX() + dX;
            int newY = pixel.getY() + dY;

            if (newX >= 0 && newX < getImageWidth() && newY >= 0 && newY < getImageHeight()) {
                result.add(newX, newY, pixel.getPreviousColor(), pixel.getColor());
            }
        }

        return result;
    }

    /**
     * Clear image and pixels
     */
    public void clear() {
        if (!active.get()) {
            return;
        }
        clearImage();
        getPixels().reset();
        active.set(false);
        pasted = false;
        outlineRect.clear();
        outlineShape.clear();
        setTranslateX(0);
        setTranslateY(0);
        InfoView.setSelectionSize(null, null);

        drag = null;
    }

    /**
     * Remember the pixel that was pressed at the start
     * of the drag.
     */
    public void setDragStart(Point p) {
        drag = new Point(
                p.getX() - (int) (getTranslateX() / getScaleX()),
                p.getY() - (int) (getTranslateY() / getScaleY()));
    }

    public boolean isDragging() {
        return drag != null;
    }

    /**
     * Move the 'drag' pixel to the new position,
     * and all other pixels accordingly.
     */
    public void dragTo(Point p) {
        int dX = p.getX() - drag.getX();
        int dY = p.getY() - drag.getY();
        setTranslateX(dX * getScaleX());
        setTranslateY(dY * getScaleY());
    }

    public void drag(int dX, int dY) {
        setDragStart(new Point(0, 0));
        dragTo(new Point(dX, dY));
    }

    public boolean isActive() {
        return active.get();
    }

    public BooleanProperty activeProperty() {
        return active;
    }

    public void setOutlineRect(OutlineRect outlineRect) {
        this.outlineRect = outlineRect;
        outlineRect.translateXProperty().bind(translateXProperty());
        outlineRect.translateYProperty().bind(translateYProperty());
    }

    public void setOutlineShape(OutlineShape outlineShape) {
        this.outlineShape = outlineShape;
        outlineShape.translateXProperty().bind(translateXProperty());
        outlineShape.translateYProperty().bind(translateYProperty());
    }

    public Pair<Point, Point> getBoundaries() {
        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxX = 0;
        int maxY = 0;

        for (Pixel pixel : getPixels().getPoints()) {
            minX = Math.min(pixel.getX(), minX);
            minY = Math.min(pixel.getY(), minY);
            maxX = Math.max(pixel.getX(), maxX);
            maxY = Math.max(pixel.getY(), maxY);
        }

        return Pair.of(new Point(minX, minY), new Point(maxX, maxY));
    }

    public void playBorder(boolean play) {
        outlineShape.playAnimation(play);
    }

    public void playRect(boolean play) {
        outlineRect.playAnimation(play);
    }

}
