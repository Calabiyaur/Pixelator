package main.java.view.editor;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.paint.Color;

import main.java.control.image.OutlineRect;
import main.java.logging.Logger;
import main.java.meta.Point;
import main.java.meta.PointArray;
import main.java.util.ImageUtil;
import main.java.view.ToolView;
import main.java.view.undo.PixelChange;
import org.apache.commons.lang3.tuple.Pair;

public class SelectionLayer extends EditorLayer {

    private Point drag;
    private BooleanProperty active = new SimpleBooleanProperty(false);
    private OutlineRect outlineRect;

    public SelectionLayer(int width, int height, PixelReader reader) {
        super(width, height, reader);
        ToolView.replaceColorProperty().addListener((ov, o, n) -> {
            if (n) {
                setStyle("-fx-background-color: #DDDDDD"); //FIXME: This doesn't work.
            } else {
                setStyle("-fx-background-color: transparent");
            }
        });
    }

    @Override public void setStart(Point position) {
        super.setStart(position);
        outlineRect.resize(getImageWidth(), getImageHeight());
        active.set(true);
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
        return getPixels().contains(p.getX() - dX, p.getY() - dY);
    }

    public boolean isEmpty() {
        return getPixels().isEmpty();
    }

    public void definePixels(PointArray points) {
        Logger.log(getClass().getSimpleName(), "Set pixels");

        clearImage();
        getPixels().reset();

        for (int i = 0; i < points.size(); i++) {
            int x = points.getX(i);
            int y = points.getY(i);
            if (ImageUtil.outOfBounds(getImage(), x, y)) {
                continue;
            }
            Color color = getReader().getColor(x, y);
            getWriter().setColor(x, y, color);
            getPixels().addForcefully(x, y, color, color);
        }
        active.set(true);
    }

    public void defineImage(Image image) {
        clear();
        int x1 = Integer.MAX_VALUE;
        int y1 = Integer.MAX_VALUE;
        int x2 = 0;
        int y2 = 0;
        PixelReader reader = image.getPixelReader();
        for (int i = 0; i < image.getWidth(); i++) {
            for (int j = 0; j < image.getHeight(); j++) {
                if (ImageUtil.outOfBounds(getImage(), i, j)) {
                    continue;
                }
                x1 = Math.min(i, x1);
                y1 = Math.min(j, y1);
                x2 = Math.max(i, x2);
                y2 = Math.max(j, y2);
                Color color = reader.getColor(i, j);
                getWriter().setColor(i, j, color);
                getPixels().add(i, j, Color.TRANSPARENT, color);
            }
        }
        active.set(true);
        outlineRect.setEdges(x1, y1, x2, y2);
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
        Logger.log(getClass().getSimpleName(), "Retrieved pixels");
        return result;
    }

    /**
     * Get pixels, but update their position first.
     */
    public PixelChange getPixelsTransformed() {
        PixelChange result = new PixelChange(getWriter());
        int dX = (int) (getTranslateX() / getScaleX());
        int dY = (int) (getTranslateY() / getScaleY());
        for (int i = 0; i < getPixels().size(); i++) {
            int newX = getPixels().getX(i) + dX;
            int newY = getPixels().getY(i) + dY;
            result.addForcefully(newX, newY, getPixels().getPreviousColor(i), getPixels().getColor(i));
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
        setTranslateX(0);
        setTranslateY(0);

        //FIXME: outline rect should automatically turn invisible when active is false
        outlineRect.setEdges(-1, -1, -1, -1);

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

    public BooleanProperty activeProperty() {
        return active;
    }

    public void setOutlineRect(OutlineRect outlineRect) {
        this.outlineRect = outlineRect;
        outlineRect.translateXProperty().bind(translateXProperty());
        outlineRect.translateYProperty().bind(translateYProperty());
    }

    public Pair<Point, Point> getBoundaries() {
        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxX = 0;
        int maxY = 0;

        for (int i = 0; i < getPixels().size(); i++) {
            minX = Math.min(getPixels().getX(i), minX);
            minY = Math.min(getPixels().getY(i), minY);
            maxX = Math.max(getPixels().getX(i), maxX);
            maxY = Math.max(getPixels().getY(i), maxY);
        }

        return Pair.of(new Point(minX, minY), new Point(maxX, maxY));
    }

    public void playAnimation(boolean play) {
        outlineRect.playAnimation(play);
    }

}
