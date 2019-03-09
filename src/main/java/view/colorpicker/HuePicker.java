package main.java.view.colorpicker;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
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
import javafx.scene.shape.Rectangle;

import main.java.util.NumberUtil;

import static main.java.view.colorpicker.ColorPickerComponents.INDICATOR_HEIGHT;
import static main.java.view.colorpicker.ColorPickerComponents.INDICATOR_STROKE_WIDTH;
import static main.java.view.colorpicker.ColorPickerComponents.INDICATOR_WIDTH;

class HuePicker extends StackPane {

    private final Pane colorBar = new Pane();
    private final DoubleProperty hue = new SimpleDoubleProperty(0.0); // in degrees: 0.0 - 360.0

    public HuePicker() {
        // Color bar
        colorBar.setBackground(new Background(new BackgroundFill(createHueGradient(), CornerRadii.EMPTY, Insets.EMPTY)));
        colorBar.setMaxWidth(INDICATOR_WIDTH - 2 * INDICATOR_STROKE_WIDTH);

        EventHandler<MouseEvent> barMouseHandler = event -> {
            final double y = event.getY();
            hue.set(NumberUtil.clamp(y / colorBar.getHeight()) * 360);
        };
        colorBar.setOnMouseDragged(barMouseHandler);
        colorBar.setOnMousePressed(barMouseHandler);

        // Color bar indicator
        Rectangle indicator = new Rectangle(INDICATOR_WIDTH, INDICATOR_HEIGHT, Color.TRANSPARENT);
        indicator.setArcHeight(INDICATOR_HEIGHT);
        indicator.setArcWidth(INDICATOR_HEIGHT);
        indicator.setStrokeWidth(INDICATOR_STROKE_WIDTH);
        indicator.setStroke(Color.WHITE);
        indicator.setMouseTransparent(true);
        indicator.setEffect(new DropShadow(BlurType.THREE_PASS_BOX, Color.BLACK, 2, 0, 0, 1));

        indicator.translateYProperty().bind(hue.divide(360).multiply(colorBar.heightProperty())
                .subtract((INDICATOR_HEIGHT + INDICATOR_STROKE_WIDTH) / 2d));

        getChildren().setAll(colorBar, indicator);
        StackPane.setAlignment(indicator, Pos.TOP_LEFT);
    }

    public DoubleProperty hueProperty() {
        return hue;
    }

    private LinearGradient createHueGradient() {
        double offset;
        Stop[] stops = new Stop[255];
        for (int x = 0; x < 255; x++) {
            offset = (1.0 / 255) * x;
            int h = (int) ((x / 255.0) * 360);
            stops[x] = new Stop(offset, Color.hsb(h, 1.0, 1.0));
        }
        return new LinearGradient(0f, 0f, 0f, 1f, true, CycleMethod.NO_CYCLE, stops);
    }

}
