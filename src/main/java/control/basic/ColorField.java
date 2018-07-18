package main.java.control.basic;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Button;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;

public class ColorField extends Button {

    private ObjectProperty<Color> color = new SimpleObjectProperty<>();

    public ColorField(Color color) {
        setPrefWidth(200);

        this.color.addListener((ov, o, n) -> setStyle("-fx-background-color: " + n.toString().replace("0x", "#")));
        this.color.set(color);
        this.setBorder(new Border(new BorderStroke(
                Color.BLACK,
                BorderStrokeStyle.SOLID,
                new CornerRadii(2),
                BorderWidths.DEFAULT)));
    }

    public ColorField() {
        this(Color.BLACK);
    }

    public Color getColor() {
        return color.get();
    }

    public void setColor(Color color) {
        this.color.set(color);
    }

    public ObjectProperty<Color> colorProperty() {
        return color;
    }
}
