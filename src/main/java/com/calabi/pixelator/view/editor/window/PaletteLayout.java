package com.calabi.pixelator.view.editor.window;

import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;

import com.calabi.pixelator.files.PaletteFile;
import com.calabi.pixelator.res.Images;
import com.calabi.pixelator.view.ColorView;

public class PaletteLayout extends Layout {

    public PaletteLayout(ImageWindow view) {
        super(view);
    }

    @Override
    public Node createGraphic() {
        Image previewImage = ((PaletteFile) file).getPreview();
        ImageView graphic = previewImage != null ? new ImageView(previewImage) : new ImageView(Images.PALETTE.getImage());

        graphic.setOnMouseClicked(e -> ColorView.getPaletteSelection().changePreview(file));

        // Wrap in Pane because LabeledSkinBase sets mouseTransparent to true for graphics that are instanceof ImageView
        return new Pane(graphic);
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
