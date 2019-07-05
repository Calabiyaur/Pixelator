package com.calabi.pixelator.view.editor;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

import org.apache.commons.lang3.tuple.Pair;

import com.calabi.pixelator.control.image.Crosshair;
import com.calabi.pixelator.control.image.Grid;
import com.calabi.pixelator.control.image.OutlineRect;
import com.calabi.pixelator.control.image.OutlineShape;
import com.calabi.pixelator.control.image.ScalableImageView;
import com.calabi.pixelator.files.ImageFile;
import com.calabi.pixelator.meta.Direction;
import com.calabi.pixelator.meta.PixelArray;
import com.calabi.pixelator.meta.Point;
import com.calabi.pixelator.meta.PointArray;
import com.calabi.pixelator.meta.QuadConsumer;
import com.calabi.pixelator.res.Config;
import com.calabi.pixelator.res.Images;
import com.calabi.pixelator.util.BackgroundUtil;
import com.calabi.pixelator.util.ColorUtil;
import com.calabi.pixelator.util.ImageUtil;
import com.calabi.pixelator.util.ShapeUtil;
import com.calabi.pixelator.view.ColorView;
import com.calabi.pixelator.view.InfoView;
import com.calabi.pixelator.view.ToolView;
import com.calabi.pixelator.view.palette.PaletteMaster;
import com.calabi.pixelator.view.tool.Tool;
import com.calabi.pixelator.view.tool.ToolManager;
import com.calabi.pixelator.view.undo.ImageChange;
import com.calabi.pixelator.view.undo.PixelChange;

public class ImageEditor extends Editor {

    private IntegerProperty width = new SimpleIntegerProperty();
    private IntegerProperty height = new SimpleIntegerProperty();
    private PixelReader reader;
    private PixelWriter writer;

    private PixelChange pixels;
    private Tool currentTool;

    private ToolLayer toolLayer;
    private SelectionLayer selectionLayer;
    private OutlineRect outlineRect;
    private OutlineShape outlineShape;
    private Grid grid;
    private Crosshair crosshair;
    private Pane background;

    private ObjectProperty<Point> mousePosition = new SimpleObjectProperty<>();

    public ImageEditor(ImageFile file, ScalableImageView imageView) {
        super(file, imageView);
        currentTool = ToolManager.getTool(ToolView.getCurrentTool());
        makeWritable();

        setMinSize(1, 1);
        prefWidthProperty().bind(imageView.scaleXProperty().multiply(imageView.widthProperty()));
        prefHeightProperty().bind(imageView.scaleYProperty().multiply(imageView.heightProperty()));

        background = new Pane();
        background.setBackground(BackgroundUtil.repeat(Images.CHECKERS));

        outlineRect = new OutlineRect(getImageWidth(), getImageHeight());
        outlineShape = new OutlineShape(getImageWidth(), getImageHeight());
        outlineShape.visibleProperty().bind(selectionLayer.activeProperty());
        selectionLayer.setOutlineRect(outlineRect);
        selectionLayer.setOutlineShape(outlineShape);

        grid = new Grid(width.get(), height.get());
        grid.draw();
        grid.prefWidthProperty().bind(imageView.scaleXProperty().multiply(width));
        grid.prefHeightProperty().bind(imageView.scaleYProperty().multiply(height));

        crosshair = new Crosshair(width.get(), height.get());
        crosshair.draw();
        crosshair.prefWidthProperty().bind(imageView.scaleXProperty().multiply(width));
        crosshair.prefHeightProperty().bind(imageView.scaleYProperty().multiply(height));

        outlineRect.prefWidthProperty().bind(imageView.scaleXProperty().multiply(width));
        outlineRect.prefHeightProperty().bind(imageView.scaleYProperty().multiply(height));
        outlineShape.prefWidthProperty().bind(imageView.scaleXProperty().multiply(width));
        outlineShape.prefHeightProperty().bind(imageView.scaleYProperty().multiply(height));

        getChildren().addAll(
                background, imageView, toolLayer, selectionLayer, crosshair, grid, outlineShape, outlineRect);

        setBorderColor("transparent"); //TODO: use parameters from preferences
        setShowGrid(false);
        setShowCrossHair(false);
        setShowBackground(false);

        setOnMousePressed(this::onMousePressed);
        setOnMouseMoved(this::onMouseMoved);
        setOnMouseDragged(this::onMouseDragged);
        setOnMouseReleased(this::onMouseReleased);
        setOnMouseEntered(e -> setCursor(currentTool.getCursor()));
        setOnMouseExited(e -> setCursor(Cursor.DEFAULT));

        imageView.imageProperty().addListener((ov, o, n) -> {
            WritableImage image = (WritableImage) n;
            width.set((int) n.getWidth());
            height.set((int) n.getHeight());
            reader = image.getPixelReader();
            writer = image.getPixelWriter();
            pixels = new PixelChange(writer);
            toolLayer.resize(width.get(), height.get(), reader);
            selectionLayer.resize(width.get(), height.get(), reader);
            grid.resize(width.get(), height.get());
            crosshair.resize(width.get(), height.get());
            updateColorCount();
        });

        ToolView.currentToolProperty().addListener((ov, o, n) -> {
            if (o == null || ToolManager.getTool(o).isSelectionTool() != ToolManager.getTool(n).isSelectionTool()) {
                currentTool.lockAndReset();
            }
            currentTool = ToolManager.getTool(n);
        });

        updateColorCount();
    }

    public void setBorderColor(String color) {
        /*stackPane.*/
        setStyle(/*"-fx-border-color: " + color + "; "
                + */"-fx-background-color: #DDDDDD;");
    }

    public boolean isShowGrid() {
        return grid.isVisible();
    }

    public void setShowGrid(boolean showGrid) {
        grid.setVisible(showGrid);
    }

    public boolean isShowCrossHair() {
        return crosshair.isVisible();
    }

    public void setShowCrossHair(boolean showCrossHair) {
        crosshair.setVisible(showCrossHair);
    }

    public boolean isShowBackground() {
        return background.isVisible();
    }

    public void setShowBackground(boolean showBackground) {
        background.setVisible(showBackground);
    }

    private void makeWritable() {
        WritableImage image = ImageUtil.createWritableImage(getImage());
        reader = image.getPixelReader();
        writer = image.getPixelWriter();
        width.set((int) image.getWidth());
        height.set((int) image.getHeight());

        getImageView().setImage(image);
        pixels = new PixelChange(writer);

        toolLayer = new ToolLayer(width.get(), height.get(), reader);
        toolLayer.scaleXProperty().bind(getImageView().scaleXProperty());
        toolLayer.scaleYProperty().bind(getImageView().scaleYProperty());

        selectionLayer = new SelectionLayer(width.get(), height.get(), reader);
        selectionLayer.scaleXProperty().bind(getImageView().scaleXProperty());
        selectionLayer.scaleYProperty().bind(getImageView().scaleYProperty());

        setCleanImage(ImageUtil.createWritableImage(image));
    }

    private void onMousePressed(MouseEvent e) {
        for (Parent parent = getParent(); parent != null; parent = parent.getParent()) {
            if (parent instanceof ImageWindow) {
                ImageWindow imageWindow = (ImageWindow) parent;
                ((ImageWindowContainer) imageWindow.getParent()).setCurrentWindow(imageWindow);
                break;
            }
        }
        currentTool.press(e);
    }

    private void onMouseMoved(MouseEvent e) {
        currentTool.move(e);
        updateCrossHair();
    }

    private void onMouseDragged(MouseEvent e) {
        currentTool.drag(e);
        updateCrossHair();
    }

    private void onMouseReleased(MouseEvent e) {
        currentTool.release(e);
    }

    private void updateCrossHair() {
        if (crosshair.isVisible()) {
            Point mouse = Tool.getMouse();
            if (mouse.getX() < width.get() && mouse.getY() < height.get()) {
                crosshair.setPosition(mouse);
            }
        }
    }

    public void register() {
        register(pixels);
        pixels.reset();
        updateColorCount();
    }

    private void writeAndRegister(PixelChange change) {
        for (int i = 0; i < change.size(); i++) {
            writer.setColor(change.getX(i), change.getY(i), change.getColor(i));
        }
        this.pixels = change;
        register();
    }

    public void restore() {
        for (int i = 0; i < pixels.size(); i++) {
            writer.setColor(pixels.getX(i), pixels.getY(i), pixels.getPreviousColor(i));
        }
        pixels.reset();
    }

    public void registerToolLayer() {
        PixelChange change = toolLayer.retrievePixels();
        change.setWriter(writer);
        writeAndRegister(change);
    }

    public boolean lockSelection() {
        if (!selectionLayer.isEmpty()) {
            pixels.add(addToImage(selectionLayer.retrievePixels()));
            writeAndRegister(pixels);
            return true;
        }
        return false;
    }

    public void escape() {
        currentTool.escape();
    }

    public void moveSelection(int right, int down) {
        if (selectionActiveProperty().get()) {
            removeSelection();
            selectionLayer.drag(right, down);
        }
    }

    public Point updateMousePosition(MouseEvent e) {
        return updateMousePosition(e.getX(), e.getY());
    }

    public Point updateMousePosition(double x, double y) {
        mousePosition.set(getMousePosition(x, y));
        return mousePosition.get();
    }

    public Point getMousePosition(double x, double y) {
        int imageOriginX = (int) Math.round((getWidth() - width.get() * getImageView().getScaleX()) / 2);
        int imageOriginY = (int) Math.round((getHeight() - height.get() * getImageView().getScaleY()) / 2);
        int imageX = (int) Math.floor((x - imageOriginX) / getImageView().getScaleX());
        int imageY = (int) Math.floor((y - imageOriginY) / getImageView().getScaleY());
        return new Point(imageX, imageY);
    }

    private void paintPixel(int x, int y, Color color, boolean replace) {
        if (ImageUtil.outOfBounds(getImage(), x, y)) {
            return;
        }
        Color previousColor = reader.getColor(x, y);
        Color newColor = replace ? color : ColorUtil.addColors(previousColor, color);

        writer.setColor(x, y, newColor);
        ColorView.addRecentColor(color);
        pixels.add(x, y, previousColor, newColor);
    }

    private void paintPoints(PointArray points, Color color, boolean replace) {
        for (int i = 0; i < points.size(); i++) {
            paintPixel(points.getX(i), points.getY(i), color, replace);
        }
    }

    public void paintPoint(int x, int y) {
        paintPixel(x, y, ColorView.getColor(), ToolView.isReplaceColor());
    }

    public void paintPoint(Point point) {
        paintPixel(point.getX(), point.getY(), ColorView.getColor(), ToolView.isReplaceColor());
    }

    public void paintPoints(PointArray points) {
        for (int i = 0; i < points.size(); i++) {
            paintPoint(points.getX(i), points.getY(i));
        }
    }

    public void paintPixels(PixelArray pixels) {
        for (int i = 0; i < pixels.size(); i++) {
            paintPixel(pixels.getX(i), pixels.getY(i), pixels.getColor(i), ToolView.isReplaceColor());
        }
    }

    public void paintLine(Point p1, Point p2) {
        paintPoints(ShapeUtil.getLinePoints(p1, p2));
    }

    public void paintFill(Point point) {
        if (ImageUtil.outOfBounds(getImage(), point)) {
            return;
        }
        Color c = reader.getColor(point.getX(), point.getY());
        paintPoints(ShapeUtil.getFillPoints(point, c, reader, width.get(), height.get()));
    }

    public void fillColor(Point point) {
        if (ImageUtil.outOfBounds(getImage(), point)) {
            return;
        }
        Color color = reader.getColor(point.getX(), point.getY());
        paintPoints(ShapeUtil.getPointsOfColor(color, reader, width.get(), height.get()));
    }

    public void selectFill(Point point) {
        if (ImageUtil.outOfBounds(getImage(), point)) {
            return;
        }
        selectionLayer.definePixels(ShapeUtil.getFillPoints(point,
                reader.getColor(point.getX(), point.getY()), reader, width.get(), height.get()));
        removePixels(selectionLayer.getPixels());
    }

    public void selectFillSelect(Point point) {
        if (ImageUtil.outOfBounds(getImage(), point)) {
            return;
        }
        Color color = reader.getColor(point.getX(), point.getY());
        selectionLayer.definePixels(ShapeUtil.getPointsOfColor(color, reader, width.get(), height.get()));
    }

    public void pickColor(Point p) {
        if (!ImageUtil.outOfBounds(getImage(), p)) {
            ColorView.setColor(reader.getColor(p.getX(), p.getY()));
        }
    }

    private PixelChange addToImage(PixelChange add) {
        PixelChange result = new PixelChange(writer);
        for (int i = 0; i < add.size(); i++) {
            int x = add.getX(i);
            int y = add.getY(i);
            if (!ImageUtil.outOfBounds(getImage(), x, y)) {
                Color previousColor = reader.getColor(x, y);
                Color color =
                        ToolView.isReplaceColor() ? add.getColor(i) :
                                ColorUtil.addColors(previousColor, add.getColor(i));
                result.add(x, y, previousColor, color);
            }
        }
        return result;
    }

    public void removePixels(PointArray toRemove) {
        paintPoints(toRemove, Color.rgb(1, 1, 1, 0), true);
    }

    public void flipHorizontally() {
        currentTool.lockAndReset();

        for (int i = 0; i < width.get(); i++) {
            int ni = width.get() - i - 1;
            for (int j = 0; j < height.get(); j++) {
                pixels.add(i, j, reader.getColor(i, j), reader.getColor(ni, j));
            }
        }
        writeAndRegister(pixels);
    }

    public void flipVertically() {
        currentTool.lockAndReset();

        for (int j = 0; j < height.get(); j++) {
            int nj = height.get() - j - 1;
            for (int i = 0; i < width.get(); i++) {
                pixels.add(i, j, reader.getColor(i, j), reader.getColor(i, nj));
            }
        }
        writeAndRegister(pixels);
    }

    @SuppressWarnings("Duplicates")
    public void rotateClockwise() {
        changeImage(getImageView().getHeight(), getImage().getWidth(), (o, n, reader, writer) -> {
            for (int j = 0; j < height.get(); j++) {
                int nj = height.get() - j - 1;
                for (int i = 0; i < width.get(); i++) {
                    writer.setColor(j, i, reader.getColor(i, nj));
                }
            }
        });
    }

    @SuppressWarnings("Duplicates")
    public void rotateCounterClockwise() {
        changeImage(getImageView().getHeight(), getImage().getWidth(), (o, n, reader, writer) -> {
            for (int i = 0; i < width.get(); i++) {
                int ni = width.get() - i - 1;
                for (int j = 0; j < height.get(); j++) {
                    writer.setColor(j, i, reader.getColor(ni, j));
                }
            }
        });
    }

    public void moveImage(int h, int v) {
        currentTool.lockAndReset();

        for (int i = 0; i < width.get(); i++) {
            int di = (i + h) % width.get();
            for (int j = 0; j < height.get(); j++) {
                int dj = (j + v) % height.get();
                pixels.add(di, dj, reader.getColor(di, dj), reader.getColor(i, j));
            }
        }
        writeAndRegister(pixels);
    }

    public void stretchImage(int w, int h) {
        changeImage(w, h, (o, n, reader, writer) -> {
            for (int i = 0; i < w; i++) {
                int oldI = (int) o.getWidth() * i / w;
                for (int j = 0; j < h; j++) {
                    int oldJ = (int) o.getHeight() * j / h;
                    writer.setColor(i, j, reader.getColor(oldI, oldJ));
                }
            }
        });
    }

    public void resizeCanvas(int w, int h, Direction bias) {
        Image oldImage = getImage();
        int xDiff = w - (int) oldImage.getWidth();
        int yDiff = h - (int) oldImage.getHeight();

        int xBias = bias.isEast() ? xDiff : bias.isWest() ? 0 : xDiff / 2;
        int yBias = bias.isSouth() ? yDiff : bias.isNorth() ? 0 : yDiff / 2;

        resize(w, h, xBias, yBias);
    }

    private void resize(int w, int h, int xBias, int yBias) {
        changeImage(w, h, (o, n, reader, writer) -> {
            for (int i = 0; i < o.getWidth(); i++) {
                int x = i + xBias;
                if (x < 0 || x >= w) {
                    continue;
                }
                for (int j = 0; j < o.getHeight(); j++) {
                    int y = j + yBias;
                    if (y < 0 || y >= h) {
                        continue;
                    }
                    writer.setColor(x, y, reader.getColor(i, j));
                }
            }
        });
    }

    private void changeImage(double width, double height,
            QuadConsumer<Image, Image, PixelReader, PixelWriter> consumer) {
        currentTool.lockAndReset();

        WritableImage newImage = new WritableImage((int) width, (int) height);
        Image oldImage = getImage();
        consumer.accept(oldImage, newImage, oldImage.getPixelReader(), newImage.getPixelWriter());

        ImageChange imageChange = new ImageChange(getImageView(), oldImage, newImage);
        getImageView().setImage(imageChange.getImage());

        register(imageChange);
    }

    public void selectAll() {
        currentTool.lockAndReset();
        selectionLayer.definePixels(ShapeUtil.getRectanglePoints(
                new Point(0, 0),
                new Point(width.get() - 1, height.get() - 1),
                true));
    }

    public void crop() {
        if (selectionActiveProperty().get()) {
            cropToSelection();
        } else {
            cropToBorder();
        }
    }

    private void cropToSelection() {
        Pair<Point, Point> boundaries = selectionLayer.getBoundaries();
        int minX = boundaries.getLeft().getX();
        int minY = boundaries.getLeft().getY();
        int maxX = boundaries.getRight().getX();
        int maxY = boundaries.getRight().getY();

        // Don't crop if selection is empty or full
        if (minX >= maxX || minY >= maxY || (minX == 0 && minY == 0 && maxX == width.get() && maxY == height.get())) {
            return;
        }

        resize(maxX - minX + 1, maxY - minY + 1, -minX, -minY);
    }

    private void cropToBorder() {
        // Find out the image's boundaries
        int minX = width.get();
        int minY = height.get();
        int maxX = 0;
        int maxY = 0;
        boolean updateMinX;
        boolean updateMinY;
        boolean updateMaxX;
        boolean updateMaxY;
        for (int i = 0; i < width.get(); i++) {
            for (int j = 0; j < height.get(); j++) {
                updateMinX = minX > i;
                updateMinY = minY > j;
                updateMaxX = maxX < i;
                updateMaxY = maxY < j;
                if (updateMinX || updateMinY || updateMaxX || updateMaxY) {
                    boolean transparent = Color.TRANSPARENT.equals(reader.getColor(i, j));
                    if (!transparent) {
                        if (updateMinX) {
                            minX = i;
                        }
                        if (updateMinY) {
                            minY = j;
                        }
                        if (updateMaxX) {
                            maxX = i;
                        }
                        if (updateMaxY) {
                            maxY = j;
                        }
                    }
                }
            }
        }

        // Don't crop if image is empty or full
        if (minX >= maxX || minY >= maxY || (minX == 0 && minY == 0 && maxX == width.get() && maxY == height.get())) {
            return;
        }

        resize(maxX - minX + 1, maxY - minY + 1, -minX, -minY);
    }

    public void cut() {
        copyImage();
        removeSelectionAndRegister();
    }

    public void copyImage() {
        Image image = ImageUtil.get(selectionLayer.getPixels());
        ClipboardContent content = new ClipboardContent();
        content.putImage(image);
        Clipboard.getSystemClipboard().setContent(content);
    }

    public void paste() {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        if (clipboard.hasImage()) {
            Image image = clipboard.getImage();
            lockSelection();
            selectionLayer.defineImage(image);
        }
    }

    public void removeSelectionAndRegister() {
        removeSelection();
        selectionLayer.clear();
        register();
    }

    public void removeSelection() {
        if (!selectionLayer.isDragging()) {
            removePixels(getSelectionLayer().getPixels());
        }
    }

    @Override
    public void updateImage(Image image) {
        currentTool.lockAndReset();
        PixelReader r = image.getPixelReader();
        for (int i = 0; i < width.get(); i++) {
            for (int j = 0; j < height.get(); j++) {
                paintPixel(i, j, r.getColor(i, j), true);
            }
        }
        register();
    }

    public void updateColorCount() {
        InfoView.setColorCount(ImageUtil.countColors(getImage()));
        //TODO: Update default palette only if colors changed, default palette is visible
        ColorView.getPaletteSelection().getDefaultEditor().updateImage(PaletteMaster.extractPalette(getImage(),
                Config.PALETTE_MAX_COLORS.getInt()));
    }

    public ToolLayer getToolLayer() {
        return toolLayer;
    }

    public SelectionLayer getSelectionLayer() {
        return selectionLayer;
    }

    public int getImageWidth() {
        return width.get();
    }

    public int getImageHeight() {
        return height.get();
    }

    public void undo() {
        currentTool.lockAndReset();
        super.undo();
        updateColorCount();
    }

    public void redo() {
        currentTool.escape();
        super.redo();
        updateColorCount();
    }

    public Image getToolImage() {
        return toolLayer.getImage();
    }

    public Image getSelectionImage() {
        return selectionLayer.getImage();
    }

    public BooleanProperty selectionActiveProperty() {
        return selectionLayer.activeProperty();
    }

    public ObjectProperty<Point> mousePositionProperty() {
        return mousePosition;
    }
}
