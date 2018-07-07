package main.java.view.editor;

import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

import main.java.standard.Point;
import main.java.standard.image.PixelatedImageView;
import main.java.view.undo.PixelChange;

public abstract class EditorLayer extends PixelatedImageView {

    private WritableImage image;
    private PixelReader reader;
    private PixelWriter writer;
    private Point start;
    private PixelChange pixels;

    public EditorLayer(int width, int height, PixelReader reader) {
        super(new WritableImage(width, height));
        initiate(reader);
    }

    private void initiate(PixelReader reader) {
        this.image = ((WritableImage) getImage());
        this.reader = reader;
        this.writer = image.getPixelWriter();
        this.pixels = new PixelChange(writer);
    }

    void resize(int width, int height, PixelReader reader) {
        clear();
        setImage(new WritableImage(width, height));
        initiate(reader);
    }

    public abstract void clear();

    protected void clearImage() {
        for (int i = 0; i < getImage().getWidth(); i++) {
            for (int j = 0; j < getImage().getHeight(); j++) {
                getWriter().setColor(i, j, Color.color(1, 1, 1, 0));
            }
        }
    }

    public boolean isEmpty() {
        return pixels.isEmpty();
    }

    public Point getStart() {
        return start;
    }

    public void setStart(Point position) {
        this.start = position;
    }

    public PixelReader getReader() {
        return reader;
    }

    public void setReader(PixelReader reader) {
        this.reader = reader;
    }

    public PixelWriter getWriter() {
        return writer;
    }

    public void setWriter(PixelWriter writer) {
        this.writer = writer;
    }

    public PixelChange getPixels() {
        return pixels;
    }

    public void setPixels(PixelChange pixels) {
        this.pixels = pixels;
    }

    public int getImageWidth() {
        return ((int) image.getWidth());
    }

    public int getImageHeight() {
        return ((int) image.getHeight());
    }
}
