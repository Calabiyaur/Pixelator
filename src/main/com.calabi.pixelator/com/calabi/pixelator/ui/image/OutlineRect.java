package com.calabi.pixelator.ui.image;

import java.util.Arrays;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.util.Duration;

public class OutlineRect extends ShapeStack {

    private final Timeline timeline = new Timeline();
    private final IntegerProperty x1 = new SimpleIntegerProperty();
    private final IntegerProperty y1 = new SimpleIntegerProperty();
    private final IntegerProperty x2 = new SimpleIntegerProperty();
    private final IntegerProperty y2 = new SimpleIntegerProperty();

    public OutlineRect(int pixelWidth, int pixelHeight) {
        super(pixelWidth, pixelHeight);

        Line topW = scalableLine(x1, y1, x2, y1, 0, 0, 1, 0);
        Line top = scalableLine(x1, y1, x2, y1, 0, 0, 1, 0);
        Line rightW = scalableLine(x2, y1, x2, y2, -1, 0, 0, 1);
        Line right = scalableLine(x2, y1, x2, y2, -1, 0, 0, 1);
        Line bottomW = scalableLine(x1, y2, x2, y2, 0, -1, 1, 0);
        Line bottom = scalableLine(x1, y2, x2, y2, 0, -1, 1, 0);
        Line leftW = scalableLine(x1, y1, x1, y2, 0, 0, 0, 1);
        Line left = scalableLine(x1, y1, x1, y2, 0, 0, 0, 1);
        getChildren().setAll(topW, rightW, bottomW, leftW, top, right, bottom, left);

        Arrays.asList(topW, rightW, bottomW, leftW).forEach(line -> line.setStroke(Color.WHITE));

        Arrays.asList(top, right, bottom, left).forEach(line -> {
            line.getStrokeDashArray().addAll(4d, 4d);
            timeline.getKeyFrames().addAll(
                    new KeyFrame(
                            Duration.ZERO,
                            new KeyValue(
                                    line.strokeDashOffsetProperty(),
                                    0,
                                    Interpolator.LINEAR
                            )
                    ),
                    new KeyFrame(
                            Duration.seconds(1),
                            new KeyValue(
                                    line.strokeDashOffsetProperty(),
                                    8,
                                    Interpolator.LINEAR
                            )
                    )
            );
        });

        timeline.setCycleCount(Timeline.INDEFINITE);
    }

    public void draw() {
    }

    public void setEdges(int x1, int y1, int x2, int y2) {
        this.x1.set(Math.min(x1, x2));
        this.y1.set(Math.min(y1, y2));
        this.x2.set(Math.max(x1, x2) + 1);
        this.y2.set(Math.max(y1, y2) + 1);
        setVisible(true);
    }

    public void clear() {
        setVisible(false);
    }

    public void playAnimation(boolean play) {
        if (play) {
            timeline.play();
        } else {
            timeline.pause();
        }
    }
}
