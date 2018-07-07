package main.java.standard.image;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.binding.NumberExpression;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Shape;

public abstract class ShapeStack extends StackPane {

    protected IntegerProperty pixelWidth;
    protected IntegerProperty pixelHeight;

    public ShapeStack(int pixelWidth, int pixelHeight) {
        super();
        setAlignment(Pos.TOP_LEFT);
        setPrefSize(pixelWidth, pixelHeight);
        this.pixelWidth = new SimpleIntegerProperty(pixelWidth);
        this.pixelHeight = new SimpleIntegerProperty(pixelHeight);
    }

    public abstract void draw();

    public void setColor(Color color) {
        getChildren().forEach(n -> ((Shape) n).setFill(color));
    }

    public void resize(int newWidth, int newHeight) {
        this.pixelWidth.setValue(newWidth);
        this.pixelHeight.setValue(newHeight);
        draw();
    }

    protected int getPixelWidth() {
        return pixelWidth.get();
    }

    public int getPixelHeight() {
        return pixelHeight.get();
    }

    protected DoubleBinding x(double i) {
        return prefWidthProperty().multiply(i / (double) getPixelWidth());
    }

    protected DoubleBinding y(double j) {
        return prefHeightProperty().multiply(j / (double) getPixelHeight());
    }

    protected DoubleBinding x(NumberExpression i) {
        return prefWidthProperty().multiply(i.divide((double) getPixelWidth()));
    }

    protected DoubleBinding y(NumberExpression j) {
        return prefHeightProperty().multiply(j.divide((double) getPixelHeight()));
    }

    protected Line scalableLine(IntegerProperty x1, IntegerProperty y1, IntegerProperty x2, IntegerProperty y2) {
        Line line = new Line();
        line.startXProperty().bind(x(x1));
        line.startYProperty().bind(y(y1));
        line.endXProperty().bind(x(x2));
        line.endYProperty().bind(y(y2));
        line.translateXProperty().bind(Bindings.min(line.startXProperty(), line.endXProperty()));
        line.translateYProperty().bind(Bindings.min(line.startYProperty(), line.endYProperty()));
        line.visibleProperty().bind(x1.isNotEqualTo(x2).or(y1.isNotEqualTo(y2)));
        return line;
    }
}
