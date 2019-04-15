package main.java.view.palette.partition;

import java.util.Map;

import javafx.scene.paint.Color;

import main.java.meta.Point;

public interface Partition {

    void add(Color color);

    Map<Color, Point> createMapping();
}
