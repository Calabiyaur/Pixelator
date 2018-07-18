package main.java.view.editor;

import javafx.scene.image.PixelReader;
import javafx.scene.paint.Color;

import main.java.meta.PointArray;
import main.java.util.ColorUtil;
import main.java.util.ImageUtil;
import main.java.view.ColorView;
import main.java.view.ToolView;
import main.java.view.undo.PixelChange;

public class ToolLayer extends EditorLayer {

    public ToolLayer(int width, int height, PixelReader reader) {
        super(width, height, reader);
    }

    public void setPixels(PointArray points) {
        clear();
        for (int i = 0; i < points.size(); i++) {
            int x = points.getX(i);
            int y = points.getY(i);
            if (ImageUtil.outOfBounds(getImage(), x, y)) {
                continue;
            }
            Color previousColor = getReader().getColor(x, y);
            Color color = ToolView.isReplaceColor() ?
                    ColorUtil.addColors(Color.LIGHTGREY, ColorView.getColor()) :
                    ColorUtil.addColors(previousColor, ColorView.getColor());
            getWriter().setColor(x, y, color);
            getPixels().add(x, y, previousColor, color);
        }
    }

    /**
     * Return the tool layer's pixels AND clear.
     */
    public PixelChange retrievePixels() {
        PixelChange result;
        if (ToolView.isReplaceColor()) {
            for (int i = 0; i < getPixels().size(); i++) {
                getPixels().setColor(i, ColorView.getColor());
            }
        }
        result = getPixels().clone();
        clear();
        return result;
    }

    public void clear() {
        if (isEmpty()) {
            return;
        }
        clearImage();
        getPixels().reset();
    }

}
