package com.calabi.pixelator.view.editor.window;

import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;

import com.calabi.pixelator.control.region.BalloonRegion;
import com.calabi.pixelator.res.Images;
import com.calabi.pixelator.view.ColorView;

public class PaletteLayout extends Layout {

    public PaletteLayout(ImageWindow view) {
        super(view);
    }

    @Override
    public Region createLowerContent() {

        Button preview = new Button("Preview");
        preview.setOnAction(e -> ColorView.getPaletteSelection().changePreview(file));
        Button apply = new Button("Apply");
        ColorView.getPaletteSelection().getEditor().updateImage(image);
        apply.setDisable(true);

        preview.setGraphic(Images.OPEN.getImageView());

        return new HBox(preview, new BalloonRegion(), apply);
    }

}
