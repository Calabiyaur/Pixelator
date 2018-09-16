package main.java.view.editor;

import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.BooleanProperty;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.layout.StackPane;

import main.java.control.image.OutlineRect;
import main.java.meta.PixelArray;
import main.java.meta.Point;
import main.java.meta.PointArray;
import main.java.view.undo.PixelChange;
import org.apache.commons.lang3.tuple.Pair;

public class SelectionLayer extends StackPane {

    private final SelectionImageLayer selectionImageLayer;
    private final List<OutlineRect> selectedAreas = new ArrayList<>();

    public SelectionLayer(int width, int height, PixelReader reader) {
        selectionImageLayer = new SelectionImageLayer(width, height, reader);
        getChildren().add(selectionImageLayer);
        //ToolView.replaceColorProperty().addListener((ov, o, n) -> {
        //    if (n) {
        //        setStyle("-fx-background-color: #DDDDDD");
        //    } else {
        //        setStyle("-fx-background-color: transparent");
        //    }
        //});
    }

    public BooleanProperty activeProperty() {
        return selectionImageLayer.activeProperty();
    }

    public void setOutlineRect(OutlineRect outlineRect) {
        selectionImageLayer.setOutlineRect(outlineRect);
    }

    public void resize(int width, int height, PixelReader reader) {
        selectionImageLayer.resize(width, height, reader);
    }

    public boolean isEmpty() {
        return selectionImageLayer.isEmpty();
    }

    public PixelChange retrievePixels() {
        return selectionImageLayer.retrievePixels();
    }

    public void drag(int dX, int dY) {
        selectionImageLayer.drag(dX, dY);
    }

    public void definePixels(PointArray points) {
        selectionImageLayer.definePixels(points);
    }

    public PixelChange getPixels() {
        return selectionImageLayer.getPixels();
    }

    public Pair<Point, Point> getBoundaries() {
        return selectionImageLayer.getBoundaries();
    }

    public void defineImage(Image image) {
        selectionImageLayer.defineImage(image);
    }

    public void clear() {
        selectionImageLayer.clear();
    }

    public boolean isDragging() {
        return selectionImageLayer.isDragging();
    }

    public Image getImage() {
        return selectionImageLayer.getImage();
    }

    public PixelArray getPixelsTransformed() {
        return selectionImageLayer.getPixelsTransformed();
    }

    public void setDragStart(Point point) {
        selectionImageLayer.setDragStart(point);
    }

    public void playAnimation(boolean play) {
        selectionImageLayer.playAnimation(play);
    }

    public void dragTo(Point point) {
        selectionImageLayer.dragTo(point);
    }

    public boolean contains(Point point) {
        return selectionImageLayer.contains(point);
    }

    public void setStart(Point point) {
        selectionImageLayer.setStart(point);
    }

    public Point getStart() {
        return selectionImageLayer.getStart();
    }

    public void setEdges(Point start, Point end) {
        selectionImageLayer.setEdges(start, end);
    }
}
