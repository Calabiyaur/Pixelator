package main.java.control.image;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.image.ImageView;

import com.sun.javafx.sg.prism.NGImageView;
import com.sun.javafx.sg.prism.NGNode;
import com.sun.prism.Graphics;
import com.sun.prism.Image;
import com.sun.prism.Texture;
import com.sun.prism.impl.BaseResourceFactory;

public class PixelatedImageView extends ImageView {

    private IntegerProperty width = new SimpleIntegerProperty();
    private IntegerProperty height = new SimpleIntegerProperty();

    public PixelatedImageView(javafx.scene.image.Image image) {
        super(image);
        width.set((int) image.getWidth());
        height.set((int) image.getHeight());
        imageProperty().addListener((ov, o, n) -> {
            width.set((int) n.getWidth());
            height.set((int) n.getHeight());
        });
    }

    @Override
    protected NGNode impl_createPeer() {
        return new NGImageView() {
            private Image image;

            @Override
            public void setImage(Object img) {
                super.setImage(img);
                image = (Image) img;
            }

            @Override
            protected void renderContent(Graphics g) {
                BaseResourceFactory factory = (BaseResourceFactory) g.getResourceFactory();
                Texture tex = factory.getCachedTexture(image, Texture.WrapMode.CLAMP_TO_EDGE);
                tex.setLinearFiltering(false);
                tex.unlock();
                super.renderContent(g);
            }
        };
    }

    public int getWidth() {
        return width.get();
    }

    public IntegerProperty widthProperty() {
        return width;
    }

    public int getHeight() {
        return height.get();
    }

    public IntegerProperty heightProperty() {
        return height;
    }
}
