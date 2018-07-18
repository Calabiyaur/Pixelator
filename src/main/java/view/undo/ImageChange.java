package main.java.view.undo;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;

import main.java.control.image.PixelatedImageView;

public class ImageChange implements Undoable {

    private PixelatedImageView imageView;
    private Image previousImage;
    private Image image;

    public ImageChange(PixelatedImageView imageView, Image previousImage, Image image) {
        this.imageView = imageView;
        this.previousImage = previousImage;
        this.image = image;
    }

    @Override
    public void undo() {
        imageView.setImage(previousImage);
    }

    @Override
    public void redo() {
        imageView.setImage(image);
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
    public Undoable clone() {
        return this;
    }
}
