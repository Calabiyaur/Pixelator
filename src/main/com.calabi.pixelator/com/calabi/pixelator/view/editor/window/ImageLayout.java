package com.calabi.pixelator.view.editor.window;

import javafx.scene.Node;
import javafx.scene.layout.Region;

public class ImageLayout extends Layout {

    public ImageLayout(ImageWindow view) {
        super(view);
    }

    @Override
    public Node createGraphic() {
        return null;
    }

    @Override
    public Region createLowerContent() {
        return null;
    }

    @Override
    public double getExtraHeight() {
        return 0;
    }

}
