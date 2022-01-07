package com.calabi.pixelator.view.undo;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;

import com.calabi.pixelator.ui.image.PixelatedImageView;
import com.calabi.pixelator.ui.image.WritableImage;

public class ImageChange implements Undoable {

    private final PixelatedImageView imageView;
    private final WritableImage previousImage;
    private final WritableImage image;

    public ImageChange(PixelatedImageView imageView, WritableImage previousImage, WritableImage image) {
        this.imageView = imageView;
        this.previousImage = previousImage;
        this.image = image;
    }

    @Override
    public void undo() {
        imageView.setImage(previousImage);
        if (image.isAnimated()) {
            previousImage.setIndex(image.getIndex());
            if (image.stop()) {
                previousImage.play();
            }
        }
    }

    @Override
    public void redo() {
        imageView.setImage(image);
        if (previousImage.isAnimated()) {
            image.setIndex(previousImage.getIndex());
            if (previousImage.stop()) {
                image.play();
            }
        }
    }

    @Override
    public boolean isEmpty() {
        if (previousImage.getWidth() != image.getWidth() || previousImage.getHeight() != image.getHeight()) {
            return false;
        }
        PixelReader previousReader = previousImage.getPixelReader();
        PixelReader reader = image.getPixelReader();
        for (int i = 0; i < image.getWidth(); i++) {
            for (int j = 0; j < image.getHeight(); j++) {
                if (!previousReader.getColor(i, j).equals(reader.getColor(i, j))) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public Undoable copy() {
        return this;
    }

    public Image getPreviousImage() {
        return previousImage;
    }

    public Image getImage() {
        return image;
    }
}
