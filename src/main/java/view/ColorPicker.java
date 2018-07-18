package main.java.view;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;

public class ColorPicker extends VBox {

    private final ObjectProperty<Color> colorProperty = new SimpleObjectProperty<>(Color.WHITE);
    private Pane colorRect;
    private Region colorRectIndicator;
    private DoubleProperty hue = new SimpleDoubleProperty(0.0); // in degrees: 0.0 - 360.0
    private DoubleProperty sat = new SimpleDoubleProperty(0.0);
    private DoubleProperty bright = new SimpleDoubleProperty(0.0);
    private DoubleProperty alpha = new SimpleDoubleProperty(1.0);

    public ColorPicker() {
        getStyleClass().add("my-custom-color");

        HBox box = new HBox();
        box.getStyleClass().add("color-rect-pane");

        colorRectIndicator = new Region();
        colorRectIndicator.setId("color-rect-indicator");
        colorRectIndicator.setManaged(false);
        colorRectIndicator.setMouseTransparent(true);
        colorRectIndicator.setCache(true);

        final Pane colorRectOpacityContainer = new StackPane();

        colorRect = new StackPane();
        colorRect.getStyleClass().addAll("color-rect", "transparent-pattern");

        Pane colorRectHue = new Pane();
        colorRectHue.backgroundProperty().bind(new ObjectBinding<Background>() {

            {
                bind(hue);
            }

            @Override protected Background computeValue() {
                return new Background(new BackgroundFill(
                        Color.hsb(hue.getValue(), 1.0, 1.0),
                        CornerRadii.EMPTY, Insets.EMPTY));

            }
        });

        Pane colorRectOverlayOne = new Pane();
        colorRectOverlayOne.getStyleClass().add("color-rect");
        colorRectOverlayOne.setBackground(new Background(new BackgroundFill(
                new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE,
                        new Stop(0, Color.rgb(255, 255, 255, 1)),
                        new Stop(1, Color.rgb(255, 255, 255, 0))),
                CornerRadii.EMPTY, Insets.EMPTY)));

        EventHandler<MouseEvent> rectMouseHandler = event -> {
            final double x = event.getX();
            final double y = event.getY();
            sat.set(clamp(x / colorRect.getWidth()));
            bright.set(1 - (clamp(y / colorRect.getHeight())));
        };

        Pane colorRectOverlayTwo = new Pane();
        colorRectOverlayTwo.getStyleClass().addAll("color-rect");
        colorRectOverlayTwo.setBackground(new Background(new BackgroundFill(
                new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
                        new Stop(0, Color.rgb(0, 0, 0, 0)), new Stop(1, Color.rgb(0, 0, 0, 1))),
                CornerRadii.EMPTY, Insets.EMPTY)));
        colorRectOverlayTwo.setOnMouseDragged(rectMouseHandler);
        colorRectOverlayTwo.setOnMousePressed(rectMouseHandler);

        Pane colorRectBlackBorder = new Pane();
        colorRectBlackBorder.setMouseTransparent(true);
        colorRectBlackBorder.getStyleClass().addAll("color-rect", "color-rect-border");

        Pane colorBar = new Pane();
        colorBar.getStyleClass().add("color-bar");
        colorBar.setBackground(new Background(new BackgroundFill(createHueGradient(),
                CornerRadii.EMPTY, Insets.EMPTY)));

        Region colorBarIndicator = new Region();
        colorBarIndicator.setId("color-bar-indicator");
        colorBarIndicator.setMouseTransparent(true);
        colorBarIndicator.setCache(true);

        colorRectIndicator.layoutXProperty().bind(
                sat.multiply(colorRect.widthProperty()));
        colorRectIndicator.layoutYProperty().bind(
                Bindings.subtract(1, bright).multiply(colorRect.heightProperty()));
        colorBarIndicator.layoutYProperty().bind(
                hue.divide(360).multiply(colorBar.heightProperty()));
        colorRectOpacityContainer.opacityProperty().bind(alpha);

        EventHandler<MouseEvent> barMouseHandler = event -> {
            final double y = event.getY();
            hue.set(clamp(y / colorBar.getHeight()) * 360);
        };

        colorBar.setOnMouseDragged(barMouseHandler);
        colorBar.setOnMousePressed(barMouseHandler);

        colorBar.getChildren().setAll(colorBarIndicator);
        colorRectOpacityContainer.getChildren().setAll(colorRectHue, colorRectOverlayOne, colorRectOverlayTwo);
        colorRect.getChildren().setAll(colorRectOpacityContainer, colorRectBlackBorder, colorRectIndicator);
        box.getChildren().addAll(colorBar, colorRect);

        getChildren().add(box);

        if (colorProperty.get() == null) {
            colorProperty.set(Color.TRANSPARENT);
        }
        updateValues();
    }

    private static double clamp(double value) {
        return value < 0 ? 0 : value > 1 ? 1 : value;
    }

    private static LinearGradient createHueGradient() {
        double offset;
        Stop[] stops = new Stop[255];
        for (int x = 0; x < 255; x++) {
            offset = (1.0 / 255) * x;
            int h = (int) ((x / 255.0) * 360);
            stops[x] = new Stop(offset, Color.hsb(h, 1.0, 1.0));
        }
        return new LinearGradient(0f, 0f, 0f, 1f, true, CycleMethod.NO_CYCLE, stops);
    }

    private void updateValues() {
        hue.set(getColor().getHue());
        sat.set(getColor().getSaturation());
        bright.set(getColor().getBrightness());
        alpha.set(getColor().getOpacity());
    }

    @Override
    protected void layoutChildren() {
        super.layoutChildren();
        colorRectIndicator.autosize();
    }

    public DoubleProperty hueProperty() {
        return hue;
    }

    public DoubleProperty satProperty() {
        return sat;
    }

    public DoubleProperty brightProperty() {
        return bright;
    }

    public DoubleProperty alphaProperty() {
        return alpha;
    }

    public Color getColor() {
        return colorProperty.get();
    }

    public void setColor(Color color) {
        this.colorProperty.set(color);
        updateValues();
    }

}
