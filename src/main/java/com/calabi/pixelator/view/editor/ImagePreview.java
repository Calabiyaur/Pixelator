package com.calabi.pixelator.view.editor;

import javafx.scene.layout.StackPane;

import com.calabi.pixelator.ui.image.ScalableImageView;

public class ImagePreview extends StackPane {

    public ImagePreview(ScalableImageView imageView) {
        super(imageView);
        minWidthProperty().bind(imageView.scaleXProperty().multiply(imageView.widthProperty()));
        minHeightProperty().bind(imageView.scaleYProperty().multiply(imageView.heightProperty()));
        prefWidthProperty().bind(imageView.scaleXProperty().multiply(imageView.widthProperty()));
        prefHeightProperty().bind(imageView.scaleYProperty().multiply(imageView.heightProperty()));
    }

}
