package com.calabi.pixelator.view.editor;

import java.util.List;
import java.util.Objects;
import java.util.Set;

import javafx.beans.InvalidationListener;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

import com.sun.javafx.tk.PlatformImage;
import org.apache.commons.lang3.tuple.Pair;

import com.calabi.pixelator.control.image.Crosshair;
import com.calabi.pixelator.control.image.Grid;
import com.calabi.pixelator.control.image.ImageBackground;
import com.calabi.pixelator.control.image.OutlineRect;
import com.calabi.pixelator.control.image.OutlineShape;
import com.calabi.pixelator.control.image.ScalableImageView;
import com.calabi.pixelator.control.image.SquareStack;
import com.calabi.pixelator.control.image.WritableImage;
import com.calabi.pixelator.files.PixelFile;
import com.calabi.pixelator.meta.Direction;
import com.calabi.pixelator.meta.PixelArray;
import com.calabi.pixelator.meta.Point;
import com.calabi.pixelator.meta.PointArray;
import com.calabi.pixelator.meta.QuadConsumer;
import com.calabi.pixelator.res.Config;
import com.calabi.pixelator.res.GridSelectionConfig;
import com.calabi.pixelator.util.Check;
import com.calabi.pixelator.util.ColorUtil;
import com.calabi.pixelator.util.Do;
import com.calabi.pixelator.util.ImageUtil;
import com.calabi.pixelator.util.shape.RectangleHelper;
import com.calabi.pixelator.util.shape.ShapeMaster;
import com.calabi.pixelator.view.ColorView;
import com.calabi.pixelator.view.InfoView;
import com.calabi.pixelator.view.ToolView;
import com.calabi.pixelator.view.editor.window.ImageWindow;
import com.calabi.pixelator.view.palette.PaletteMaster;
import com.calabi.pixelator.view.palette.SortMaster;
import com.calabi.pixelator.view.tool.Tool;
import com.calabi.pixelator.view.tool.ToolManager;
import com.calabi.pixelator.view.undo.FrameChange;
import com.calabi.pixelator.view.undo.ImageChange;
import com.calabi.pixelator.view.undo.IndexChange;
import com.calabi.pixelator.view.undo.PixelChange;

public class ImageEditor extends Editor {

    private IntegerProperty width = new SimpleIntegerProperty();
    private IntegerProperty height = new SimpleIntegerProperty();
    private PixelReader reader;
    private PixelWriter writer;
    private BooleanProperty imageAnimated = new SimpleBooleanProperty(false);
    private int animationStart;

    private PixelChange pixels;
    private Tool currentTool;
    private boolean updateCursor;

    private ToolLayer toolLayer;
    private SquareStack squareStack;
    private SelectionLayer selectionLayer;
    private OutlineRect outlineRect;
    private OutlineShape outlineShape;
    private Grid grid;
    private Crosshair crosshair;
    private Crosshair crosshair2;
    private ImageBackground background;

    private ObjectProperty<Point> mousePosition = new SimpleObjectProperty<>();
    private BooleanProperty showCrossHair = new SimpleBooleanProperty();

    public ImageEditor(PixelFile file, ScalableImageView imageView) {
        super(file, imageView);
        currentTool = ToolManager.getTool(ToolView.get().getCurrentTool());
        makeWritable();

        setMinSize(1, 1);
        prefWidthProperty().bind(imageView.scaleXProperty().multiply(imageView.widthProperty()));
        prefHeightProperty().bind(imageView.scaleYProperty().multiply(imageView.heightProperty()));

        background = new ImageBackground();

        //TODO: Refactor this. ImageEditor does not need to know about square stack / outline shape, rect
        squareStack = new SquareStack(getImageWidth(), getImageHeight());
        toolLayer.setSquareStack(squareStack);

        outlineRect = new OutlineRect(getImageWidth(), getImageHeight());
        outlineShape = new OutlineShape(getImageWidth(), getImageHeight());
        outlineShape.visibleProperty().bind(selectionLayer.activeProperty());
        selectionLayer.setOutlineRect(outlineRect);
        selectionLayer.setOutlineShape(outlineShape);

        GridSelectionConfig gridSelectionConfig = Config.GRID_SELECTION.getObject();
        grid = new Grid(width.get(), height.get());
        grid.setXInterval(gridSelectionConfig.getXInterval());
        grid.setYInterval(gridSelectionConfig.getYInterval());
        grid.draw();
        grid.prefWidthProperty().bind(imageView.scaleXProperty().multiply(width));
        grid.prefHeightProperty().bind(imageView.scaleYProperty().multiply(height));

        crosshair = new Crosshair(width.get(), height.get());
        crosshair.draw();
        crosshair.prefWidthProperty().bind(imageView.scaleXProperty().multiply(width));
        crosshair.prefHeightProperty().bind(imageView.scaleYProperty().multiply(height));
        crosshair.visibleProperty().bind(showCrossHair.and(InfoView.mousePositionVisibleProperty()));

        crosshair2 = new Crosshair(width.get(), height.get());
        crosshair2.draw();
        crosshair2.prefWidthProperty().bind(imageView.scaleXProperty().multiply(width));
        crosshair2.prefHeightProperty().bind(imageView.scaleYProperty().multiply(height));
        crosshair2.setVisible(false);

        squareStack.prefWidthProperty().bind(imageView.scaleXProperty().multiply(width));
        squareStack.prefHeightProperty().bind(imageView.scaleYProperty().multiply(height));
        outlineRect.prefWidthProperty().bind(imageView.scaleXProperty().multiply(width));
        outlineRect.prefHeightProperty().bind(imageView.scaleYProperty().multiply(height));
        outlineShape.prefWidthProperty().bind(imageView.scaleXProperty().multiply(width));
        outlineShape.prefHeightProperty().bind(imageView.scaleYProperty().multiply(height));

        getChildren().addAll(
                background,
                imageView,
                toolLayer,
                squareStack,
                selectionLayer,
                crosshair,
                crosshair2,
                grid,
                outlineShape,
                outlineRect
        );

        setShowGrid(gridSelectionConfig.isSelected());
        setShowCrossHair(false);
        setShowBackground(false);

        setOnMousePressed(this::onMousePressed);
        setOnMouseMoved(this::onMouseMoved);
        setOnMouseDragged(this::onMouseDragged);
        setOnMouseReleased(this::onMouseReleased);
        setOnMouseEntered(e -> {
            updateCursor = true;
            updateCursor();
        });
        setOnMouseExited(e -> {
            updateCursor = false;
            updateCursor();
        });

        imageView.imageProperty().addListener((ov, o, n) -> {
            WritableImage image = (WritableImage) n;
            width.set((int) n.getWidth());
            height.set((int) n.getHeight());
            reader = image.getPixelReader();
            writer = image.getPixelWriter();
            imageAnimated.bind(image.animatedProperty());
            pixels = new PixelChange(writer);
            toolLayer.resize(width.get(), height.get(), reader);
            selectionLayer.resize(width.get(), height.get(), reader);
            grid.resize(width.get(), height.get());
            crosshair.resize(width.get(), height.get());
            crosshair2.resize(width.get(), height.get());

            image.playingProperty().addListener((pov, po, pn) -> Do.when(!pn, () -> stop()));
            updateColorCount();
        });

        InvalidationListener cursorChangeListener = (ov) -> updateCursor();
        ToolView.get().currentToolProperty().addListener((ov, o, n) -> {
            currentTool.cursorProperty().removeListener(cursorChangeListener);
            currentTool = ToolManager.getTool(n);
            currentTool.cursorProperty().addListener(cursorChangeListener);
        });

        getImage().playingProperty().addListener((pov, po, pn) -> Do.when(!pn, () -> stop()));
        updateColorCount();
    }

    public boolean isShowGrid() {
        return grid.isVisible();
    }

    public void setShowGrid(boolean showGrid) {
        grid.setVisible(showGrid);
    }

    public boolean isShowCrossHair() {
        return showCrossHair.get();
    }

    public void setShowCrossHair(boolean showCrossHair) {
        this.showCrossHair.set(showCrossHair);
    }

    public boolean isShowBackground() {
        return ImageBackground.FillType.CHECKERS.equals(background.getType());
    }

    public void setShowBackground(boolean showBackground) {
        background.setType(showBackground ? ImageBackground.FillType.CHECKERS : ImageBackground.FillType.SINGLE_COLOR);
    }

    public ImageBackground getImageBackground() {
        return background;
    }

    public void setGridInterval(int xInterval, int yInterval) {
        grid.setXInterval(xInterval);
        grid.setYInterval(yInterval);
        grid.draw();
        IWC.get().setShowGrid(true);
    }

    private void makeWritable() {
        WritableImage image = getImage();
        reader = image.getPixelReader();
        writer = image.getPixelWriter();
        width.set((int) image.getWidth());
        height.set((int) image.getHeight());
        imageAnimated.bind(image.animatedProperty());

        getImageView().setImage(image);
        pixels = new PixelChange(writer);

        toolLayer = new ToolLayer(width.get(), height.get(), reader);
        toolLayer.scaleXProperty().bind(getImageView().scaleXProperty());
        toolLayer.scaleYProperty().bind(getImageView().scaleYProperty());

        selectionLayer = new SelectionLayer(width.get(), height.get(), reader);
        selectionLayer.scaleXProperty().bind(getImageView().scaleXProperty());
        selectionLayer.scaleYProperty().bind(getImageView().scaleYProperty());

        setCleanImage(image.copy());
    }

    private void onMousePressed(MouseEvent e) {
        requestFocus();
        ImageWindow imageWindow = getParentWindow();
        ((IWC) imageWindow.getParent()).setCurrentWindow(imageWindow);
        currentTool.press(e);
    }

    private void onMouseMoved(MouseEvent e) {
        currentTool.move(e);
        updateCrossHair();
        updateCursor();
    }

    private void onMouseDragged(MouseEvent e) {
        currentTool.drag(e);
        updateCrossHair();
        updateCursor();
    }

    private void onMouseReleased(MouseEvent e) {
        crosshair2.setVisible(false);
        currentTool.release(e);
    }

    public void onKeyPressed(KeyEvent e) {
        currentTool.keyPress(e);
    }

    public void onKeyReleased(KeyEvent e) {
        currentTool.keyRelease(e);
    }

    private ImageWindow getParentWindow() {
        for (Parent parent = getParent(); parent != null; parent = parent.getParent()) {
            if (parent instanceof ImageWindow) {
                return  (ImageWindow) parent;
            }
        }
        throw new IllegalStateException();
    }

    private void updateCrossHair() {
        if (isShowCrossHair()) {
            Point mouse = Tool.getMouse();
            if (mouse.getX() >= 0 && mouse.getX() < width.get() && mouse.getY() >= 0 && mouse.getY() < height.get()) {
                crosshair.setPosition(mouse);
            }
            Point start = selectionActiveProperty().get() ? selectionLayer.getStart() : toolLayer.getStart();
            if (Tool.actingToolProperty().get().isSecondaryCrosshairEnabled() && !Objects.equals(mouse, start)) {
                if (!crosshair2.isVisible()) {
                    crosshair2.setVisible(true);
                    crosshair2.setPosition(start);
                }
            } else {
                crosshair2.setVisible(false);
            }
        } else {
            crosshair2.setVisible(false);
        }
    }

    private void updateCursor() {
        if (updateCursor) {
            setCursor(currentTool.getCursor());
        } else {
            setCursor(Cursor.DEFAULT);
        }
    }

    public void register() {
        register(pixels);
        pixels.reset();
        refreshFrame();
        updateColorCount();
    }

    private void refreshFrame() {
        if (getImage().isAnimated()) {
            getParentWindow().refreshLayout();
        }
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
        pixels.add(addToImage(toolLayer.retrievePixels()));
        writeAndRegister(pixels);
    }

    public void lockSelection() {
        if (!selectionLayer.isEmpty()) {
            pixels.add(addToImage(selectionLayer.retrievePixels()));
            writeAndRegister(pixels);
        }
    }

    public void escape() {
        currentTool.lockAndReset();
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

    /**
     * Central method for painting a pixel.
     */
    private void paintPixel(int x, int y, Color color, boolean replace) {
        if (ImageUtil.outOfBounds(getImage(), x, y)) {
            return;
        }
        Color previousColor = reader.getColor(x, y);
        Color newColor = ColorUtil.addColors(previousColor, color, replace, ToolView.get().isAlphaOnly());

        if (!newColor.equals(previousColor)) {
            writer.setColor(x, y, newColor);
            ColorView.addRecentColor(color);
            pixels.add(x, y, previousColor, newColor);
        }
    }

    private void paintPoints(PointArray points, Color color, boolean replace) {
        for (int i = 0; i < points.size(); i++) {
            paintPixel(points.getX(i), points.getY(i), color, replace);
        }
    }

    public void paintPixel(int x, int y) {
        paintPixel(x, y, ColorView.getColor(), ToolView.get().isReplaceColor());
    }

    public void paintPoint(Point point) {
        paintPoints(RectangleHelper.getCirclePoints(point.getX(), point.getY(), ToolView.get().getThickness()));
    }

    public void paintPoints(PointArray points) {
        for (int i = 0; i < points.size(); i++) {
            paintPixel(points.getX(i), points.getY(i));
        }
    }

    public void paintPixels(PixelArray pixels) {
        for (int i = 0; i < pixels.size(); i++) {
            paintPixel(pixels.getX(i), pixels.getY(i), pixels.getColor(i), ToolView.get().isReplaceColor());
        }
    }

    public void paintLine(Point p1, Point p2) {
        paintPoints(ShapeMaster.getLinePoints(p1, p2, ToolView.get().getSettings()));
    }

    public void paintFill(Point point) {
        if (ImageUtil.outOfBounds(getImage(), point)) {
            return;
        }
        Color c = reader.getColor(point.getX(), point.getY());
        paintPoints(ShapeMaster.getFillPoints(point, c, reader, width.get(), height.get()));
    }

    public void fillColor(Point point) {
        if (ImageUtil.outOfBounds(getImage(), point)) {
            return;
        }
        Color color = reader.getColor(point.getX(), point.getY());
        paintPoints(ShapeMaster.getPointsOfColor(color, reader, width.get(), height.get()));
    }

    public PointArray getSelectFill(Point point) {
        if (ImageUtil.outOfBounds(getImage(), point)) {
            return null;
        }
        return ShapeMaster.getFillPoints(
                point, reader.getColor(point.getX(), point.getY()), reader, width.get(), height.get());
    }

    public PointArray getSelectColor(Point point) {
        if (ImageUtil.outOfBounds(getImage(), point)) {
            return null;
        }
        Color color = reader.getColor(point.getX(), point.getY());
        return ShapeMaster.getPointsOfColor(color, reader, width.get(), height.get());
    }

    public void pickColor(Point p) {
        if (!ImageUtil.outOfBounds(getImage(), p)) {
            ColorView.setColor(reader.getColor(p.getX(), p.getY()));
        }
    }

    private PixelChange addToImage(PixelArray add) {
        PixelChange result = new PixelChange(writer);
        for (int i = 0; i < add.size(); i++) {
            int x = add.getX(i);
            int y = add.getY(i);
            if (!ImageUtil.outOfBounds(getImage(), x, y)) {
                Color previousColor = reader.getColor(x, y);
                boolean replaceColor = ToolView.get().isReplaceColor();
                boolean alphaOnly = ToolView.get().isAlphaOnly();
                Color color = ColorUtil.addColors(previousColor, add.getColor(i), replaceColor, alphaOnly);
                result.add(x, y, previousColor, color);
            }
        }
        return result;
    }

    public void removePixels(PointArray toRemove) {
        paintPoints(toRemove, Color.rgb(1, 1, 1, 0), true);
    }

    public void flipHorizontally() {
        changeImage(width.get(), height.get(), (o, n, reader, writer) -> {
            for (int i = 0; i < width.get(); i++) {
                int ni = width.get() - i - 1;
                for (int j = 0; j < height.get(); j++) {
                    writer.setColor(i, j, reader.getColor(ni, j));
                }
            }
        });
    }

    public void flipVertically() {
        changeImage(width.get(), height.get(), (o, n, reader, writer) -> {
            for (int j = 0; j < height.get(); j++) {
                int nj = height.get() - j - 1;
                for (int i = 0; i < width.get(); i++) {
                    writer.setColor(i, j, reader.getColor(i, nj));
                }
            }
        });
    }

    public void rotateClockwise() {
        changeImage(height.get(), width.get(), (o, n, reader, writer) -> {
            for (int j = 0; j < height.get(); j++) {
                int nj = height.get() - j - 1;
                for (int i = 0; i < width.get(); i++) {
                    writer.setColor(j, i, reader.getColor(i, nj));
                }
            }
        });
    }

    public void rotateCounterClockwise() {
        changeImage(height.get(), width.get(), (o, n, reader, writer) -> {
            for (int i = 0; i < width.get(); i++) {
                int ni = width.get() - i - 1;
                for (int j = 0; j < height.get(); j++) {
                    writer.setColor(j, i, reader.getColor(ni, j));
                }
            }
        });
    }

    public void moveImage(int h, int v) {

        int posH = Math.floorMod(h, width.get());
        int posV = Math.floorMod(v, height.get());

        changeImage(width.get(), height.get(), (o, n, reader, writer) -> {
            for (int i = 0; i < width.get(); i++) {
                int di = (i + posH) % width.get();
                for (int j = 0; j < height.get(); j++) {
                    int dj = (j + posV) % height.get();
                    pixels.add(di, dj, reader.getColor(di, dj), reader.getColor(i, j));
                }
            }
        });
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

    private void changeImage(double width, double height, QuadConsumer<Image, Image, PixelReader, PixelWriter> consumer) {
        currentTool.lockAndReset();

        WritableImage newImage = new WritableImage((int) width, (int) height);
        WritableImage oldImage = getImage();

        if (oldImage.isAnimated()) {

            boolean running = oldImage.stop();

            int frameCount = oldImage.getFrameCount();
            int index = oldImage.getIndex();
            newImage.initAnimation(frameCount, oldImage.getDelay());

            for (int i = 0; i < frameCount; i++) {
                consumer.accept(oldImage, newImage, oldImage.getPixelReader(i), newImage.getPixelWriter(i));
            }
            newImage.setIndex(index);
            if (running) {
                newImage.play();
            }

        } else {
            consumer.accept(oldImage, newImage, oldImage.getPixelReader(), newImage.getPixelWriter());
        }

        ImageChange imageChange = new ImageChange(getImageView(), oldImage, newImage);
        getImageView().setImage(imageChange.getImage());

        register(imageChange);
    }

    public void selectAll() {
        currentTool.lockAndReset();
        selectionLayer.definePixels(RectangleHelper.getRectanglePoints(
                new Point(0, 0),
                new Point(width.get() - 1, height.get() - 1),
                true));
    }

    public void invertSelection() {
        PixelChange current = selectionLayer.getPixels();
        PointArray inverted = current.invert(getImageWidth(), getImageHeight());
        currentTool.lockAndReset();
        selectionLayer.definePixels(inverted);
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

    public void invert() {
        if (selectionActiveProperty().get()) {
            PixelChange selectedPixels = selectionLayer.getPixels().copy();
            currentTool.lockAndReset();

            selectedPixels.forEach((x, y, prev, color) -> {
                pixels.add(x, y, color, ColorUtil.invert(color));
            });
        } else {
            currentTool.lockAndReset();

            for (int i = 0; i < width.get(); i++) {
                for (int j = 0; j < height.get(); j++) {
                    Color color = reader.getColor(i, j);
                    pixels.add(i, j, color, ColorUtil.invert(color));
                }
            }
        }
        writeAndRegister(pixels);
    }

    public void invertWithinPalette() {
        Set<Color> colorSet = PaletteMaster.extractColors(getImage());
        List<Color> colors = SortMaster.sortByValues(colorSet);

        if (selectionActiveProperty().get()) {
            PixelChange selectedPixels = selectionLayer.getPixels().copy();
            currentTool.lockAndReset();

            selectedPixels.forEach((x, y, prev, color) -> invertPixel(colors, x, y, color));
        } else {
            currentTool.lockAndReset();

            for (int i = 0; i < width.get(); i++) {
                for (int j = 0; j < height.get(); j++) {
                    invertPixel(colors, i, j, reader.getColor(i, j));
                }
            }
        }
        writeAndRegister(pixels);
    }

    private void invertPixel(List<Color> palette, int x, int y, Color color) {
        int index = palette.indexOf(color);
        if (index != -1) {
            Color temp = palette.get(palette.size() - index - 1);
            Color inverted = Color.color(temp.getRed(), temp.getGreen(), temp.getBlue(), color.getOpacity());
            pixels.add(x, y, color, inverted);
        }
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
            selectionLayer.defineImage(image, true);
        }
    }

    public void reverse() { //TODO: Make undoable, use WritableImage API so that WritableImage.frameList gets updated
        Check.ensure(getImage().isAnimated());

        PlatformImage[] frames = getImage().getFrames();
        for (int i = 0; i < frames.length / 2; i++) {
            PlatformImage temp = frames[i];
            frames[i] = frames[frames.length - i - 1];
            frames[frames.length - i - 1] = temp;
        }
    }

    public void addFrame() {
        FrameChange change = FrameChange.add(getImage(), getImage().getIndex() + 1);
        register(change);
    }

    public void removeFrame() {
        FrameChange change = FrameChange.remove(getImage(), getImage().getIndex());
        register(change);
    }

    public void moveFrameForward() {
        FrameChange change = FrameChange.permute(getImage(), getImage().getIndex(), getImage().getIndex() + 1);
        register(change);
    }

    public void moveFrameBackward() {
        FrameChange change = FrameChange.permute(getImage(), getImage().getIndex(), getImage().getIndex() - 1);
        register(change);
    }

    public void changeDelay(int delay) { //TODO: Make undoable
        getImage().setDelay(delay);
    }

    public void removeSelectionAndRegister() {
        removeSelection();
        selectionLayer.clear();
        register();
    }

    public void removeSelection() {
        if (!selectionLayer.isDragging() && !selectionLayer.isPasted()) {
            removePixels(getSelectionLayer().getPixels());
        }
    }

    @Override
    public void updateImage(WritableImage image) {
        currentTool.lockAndReset();

        if (getImage().isAnimated()) {
            image.setIndex(getImage().getIndex());
        }

        ImageChange change = new ImageChange(getImageView(), getImage(), image);
        getImageView().setImage(image);
        register(change);
    }

    public void updateColorCount() {
        InfoView.setColorCount(ImageUtil.countColors(getImage()));
        //TODO: Update default palette only if colors changed && default palette is visible
        ColorView.getPaletteSelection().getDefaultEditor()
                .updateImage(PaletteMaster.extractPalette(getImage(), Config.PALETTE_MAX_COLORS.getInt()));
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

    public BooleanProperty imageAnimatedProperty() {
        return imageAnimated;
    }

    public void undo() {
        currentTool.lockAndReset();
        super.undo();
        refreshFrame();
        updateColorCount();
    }

    public void redo() {
        currentTool.escape();
        super.redo();
        refreshFrame();
        updateColorCount();
    }

    public void nextFrame() {
        currentTool.lockAndReset();
        int currentIndex = getImage().getIndex();
        int after = (currentIndex + 1) % getImage().getFrameCount();
        register(new IndexChange(getImage(), currentIndex, after));
        getImage().next();
    }

    public void previousFrame() {
        currentTool.lockAndReset();
        int currentIndex = getImage().getIndex();
        int after = Math.floorMod(currentIndex - 1, getImage().getFrameCount());
        register(new IndexChange(getImage(), currentIndex, after));
        getImage().previous();
    }

    public void setFrameIndex(int index) {
        currentTool.lockAndReset();
        register(new IndexChange(getImage(), getImage().getIndex(), index));
        getImage().setIndex(index);
    }

    public void play() {
        animationStart = getImage().getIndex();
        getImage().play();
    }

    public void stop() {
        if (getImage().stop()) {
            getImage().setIndex(animationStart);
        }
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
