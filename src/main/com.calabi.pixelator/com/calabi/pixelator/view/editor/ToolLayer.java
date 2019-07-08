package com.calabi.pixelator.view.editor;

import javafx.scene.image.PixelReader;
import javafx.scene.paint.Color;

import com.calabi.pixelator.meta.PointArray;
import com.calabi.pixelator.util.ColorUtil;
import com.calabi.pixelator.util.ImageUtil;
import com.calabi.pixelator.view.ColorView;
import com.calabi.pixelator.view.ToolView;
import com.calabi.pixelator.view.undo.PixelChange;

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
            Color color = ToolView.getInstance().isReplaceColor() ?
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
        if (ToolView.getInstance().isReplaceColor()) {
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
