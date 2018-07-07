package main.java.standard.control;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.layout.ColumnConstraints;

public class ColumnQuality {

    private DoubleProperty maxWidth = new SimpleDoubleProperty(-1);
    private DoubleProperty minWidth = new SimpleDoubleProperty(-1);
    private DoubleProperty prefWidth = new SimpleDoubleProperty(-1);

    public ColumnConstraints build() {
        ColumnConstraints cc = new ColumnConstraints();
        cc.setMaxWidth(getMaxWidth());
        cc.setMinWidth(getMinWidth());
        cc.setPrefWidth(getPrefWidth());
        return cc;
    }

    public void combineWith(int size, ColumnQuality other) {
        if (getMaxWidth() == -1) {
            setMaxWidth(other.getMaxWidth());
        } else if (other.getMaxWidth() != -1) {
            setMaxWidth(Math.max(getMaxWidth(), other.getMaxWidth()));
        }
        if (getMinWidth() == -1) {
            setMinWidth(other.getMinWidth());
        } else if (other.getMinWidth() != -1) {
            setMinWidth(Math.min(getMinWidth(), other.getMinWidth()));
        }
        if (getPrefWidth() == -1) {
            setPrefWidth(other.getPrefWidth());
        } else if (other.getPrefWidth() != -1) {
            setPrefWidth((size * getPrefWidth() + other.getPrefWidth()) / (size + 1));
        }
    }

    public double getMaxWidth() {
        return maxWidth.get();
    }

    public void setMaxWidth(double maxWidth) {
        this.maxWidth.set(maxWidth);
    }

    public DoubleProperty maxWidthProperty() {
        return maxWidth;
    }

    public double getMinWidth() {
        return minWidth.get();
    }

    public void setMinWidth(double minWidth) {
        this.minWidth.set(minWidth);
    }

    public DoubleProperty minWidthProperty() {
        return minWidth;
    }

    public double getPrefWidth() {
        return prefWidth.get();
    }

    public void setPrefWidth(double prefWidth) {
        this.prefWidth.set(prefWidth);
    }

    public DoubleProperty prefWidthProperty() {
        return prefWidth;
    }

}
