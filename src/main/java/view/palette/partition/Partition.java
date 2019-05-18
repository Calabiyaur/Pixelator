package main.java.view.palette.partition;

import javafx.scene.paint.Color;

public interface Partition {

    void add(Color color);

    Mapping createMapping();
}
