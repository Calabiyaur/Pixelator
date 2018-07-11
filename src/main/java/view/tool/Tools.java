package main.java.view.tool;

public enum Tools {

    DRAG, // drag the selection layer's pixels
    ELLIPSE, // draw an elliptic shape
    FILL, // fill an area of equal color
    FILL_COLOR, // change one color globally
    LASSO, // UNUSED
    LINE, // draw a line between two points
    PEN, // draw one point
    PICK, // pick a color
    RECTANGLE, // draw a rectangular shape
    SELECT, // select a rectangular-shaped area
    WAND; // select an area of equal color

    public String toString() {
        return name();
    }

}
