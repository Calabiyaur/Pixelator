package com.calabi.pixelator.view.editor.window;

import javafx.scene.Node;
import javafx.scene.layout.Region;

import com.calabi.pixelator.control.image.ScalableImageView;
import com.calabi.pixelator.control.image.WritableImage;
import com.calabi.pixelator.files.PixelFile;
import com.calabi.pixelator.view.editor.ImageEditor;

public abstract class Layout {

    protected final ImageWindow view;
    protected final ImageEditor editor;
    protected final ScalableImageView imageView;
    protected final WritableImage image;
    protected final PixelFile file;

    public Layout(ImageWindow view) {
        this.view = view;
        this.editor = view.getEditor();
        this.imageView = view.getImageView();
        this.image = view.getImage();
        this.file = view.getFile();
    }

    public static Layout get(ImageWindow imageWindow) {
        switch(imageWindow.getFile().getCategory()) {
            case ANIMATION:
                return new AnimationLayout(imageWindow);
            case IMAGE:
                return new ImageLayout(imageWindow);
            case PALETTE:
                return new PaletteLayout(imageWindow);
        }
        throw new IllegalStateException();
    }

    public abstract Node createGraphic();

    public abstract Region createLowerContent();

    public abstract double getExtraHeight();

}
