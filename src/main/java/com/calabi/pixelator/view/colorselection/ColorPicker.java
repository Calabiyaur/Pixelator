package com.calabi.pixelator.view.colorselection;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;

import com.calabi.pixelator.res.Config;
import com.calabi.pixelator.res.Images;
import com.calabi.pixelator.util.BackgroundBuilder;
import com.calabi.pixelator.util.ColorUtil;
import com.calabi.pixelator.util.NumberUtil;

class ColorPicker extends StackPane {

    private final ObjectProperty<Color> colorProperty = new SimpleObjectProperty<>();
    private final DoubleProperty hue = new SimpleDoubleProperty(0.0); // in degrees: 0.0 - 360.0
    private final DoubleProperty sat = new SimpleDoubleProperty(0.0);
    private final DoubleProperty bright = new SimpleDoubleProperty(0.0);
    private final DoubleProperty alpha = new SimpleDoubleProperty(1.0);

    public ColorPicker() {
        // Color rectangle
        final Pane colorRectOpacityContainer = createColorRect();

        // Color rectangle indicator
        Circle indicator = new Circle(ColorSelectionModel.INDICATOR_RADIUS, Color.TRANSPARENT);
        indicator.setStrokeWidth(ColorSelectionModel.INDICATOR_STROKE_WIDTH);
        indicator.setStroke(Color.WHITE);
        indicator.setMouseTransparent(true);
        indicator.setEffect(new DropShadow(BlurType.THREE_PASS_BOX, Color.BLACK, 2, 0, 0, 1));

        indicator.translateXProperty().bind(sat.multiply(widthProperty())
                .subtract((ColorSelectionModel.INDICATOR_RADIUS + 4 * ColorSelectionModel.INDICATOR_STROKE_WIDTH) / 2d));
        indicator.translateYProperty().bind(Bindings.subtract(1, bright).multiply(heightProperty())
                .subtract((ColorSelectionModel.INDICATOR_RADIUS + 4 * ColorSelectionModel.INDICATOR_STROKE_WIDTH) / 2d));

        getChildren().setAll(colorRectOpacityContainer, indicator);
        StackPane.setAlignment(indicator, Pos.TOP_LEFT);

        if (colorProperty.get() == null) {
            Config.COLOR.setDef(ColorUtil.getRandomPleasant().toString());
            colorProperty.set(Color.valueOf(Config.COLOR.getString()));
        }
        updateValues();
    }

    private Pane createColorRect() {
        final Pane colorRectOpacityContainer = new StackPane();

        Pane transparentBackground = new Pane();
        transparentBackground.setBackground(BackgroundBuilder.repeat(Images.CHECKERS.getImage()).build());

        Pane colorRectHue = new Pane();
        colorRectHue.backgroundProperty().bind(new ObjectBinding<Background>() {

            {
                bind(hue);
            }

            @Override
            protected Background computeValue() {
                return new Background(new BackgroundFill(
                        Color.hsb(hue.getValue(), 1.0, 1.0), CornerRadii.EMPTY, Insets.EMPTY));
            }
        });

        Pane colorRectOverlayOne = new Pane();
        colorRectOverlayOne.setBackground(new Background(new BackgroundFill(
                new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE,
                        new Stop(0, Color.rgb(255, 255, 255, 1)),
                        new Stop(1, Color.rgb(255, 255, 255, 0))),
                CornerRadii.EMPTY, Insets.EMPTY)));

        EventHandler<MouseEvent> rectMouseHandler = event -> {
            final double x = event.getX();
            final double y = event.getY();
            sat.set(NumberUtil.clamp(x / getWidth()));
            bright.set(1 - (NumberUtil.clamp(y / getHeight())));
        };

        Pane colorRectOverlayTwo = new Pane();
        colorRectOverlayTwo.setBackground(new Background(new BackgroundFill(
                new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
                        new Stop(0, Color.rgb(0, 0, 0, 0)),
                        new Stop(1, Color.rgb(0, 0, 0, 1))),
                CornerRadii.EMPTY, Insets.EMPTY)));
        colorRectOverlayTwo.setOnMouseDragged(rectMouseHandler);
        colorRectOverlayTwo.setOnMousePressed(rectMouseHandler);

        colorRectOpacityContainer.getChildren().setAll(
                transparentBackground, colorRectHue, colorRectOverlayOne, colorRectOverlayTwo);
        colorRectHue.opacityProperty().bind(alpha);
        colorRectOverlayOne.opacityProperty().bind(alpha);
        colorRectOverlayTwo.opacityProperty().bind(alpha);
        return colorRectOpacityContainer;
    }

    private void updateValues() {
        hue.set(getColor().getHue());
        sat.set(getColor().getSaturation());
        bright.set(getColor().getBrightness());
        alpha.set(getColor().getOpacity());
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
