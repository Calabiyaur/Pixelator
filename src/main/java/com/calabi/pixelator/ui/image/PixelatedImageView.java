package com.calabi.pixelator.ui.image;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.Node;
import javafx.scene.image.ImageView;

import com.sun.javafx.geom.BaseBounds;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.javafx.scene.ImageViewHelper;
import com.sun.javafx.sg.prism.NGImageView;
import com.sun.javafx.sg.prism.NGNode;
import com.sun.prism.Graphics;
import com.sun.prism.Image;
import com.sun.prism.Texture;
import com.sun.prism.impl.BaseResourceFactory;
import org.apache.commons.lang3.reflect.FieldUtils;

import com.calabi.pixelator.main.ExceptionHandler;
import com.calabi.pixelator.util.ReflectionUtil;

public class PixelatedImageView extends ImageView { //FIXME: Horizontally, pixels vary in size (e.g. 2, 2, 2, 3, 1, 2, ...)

    private final IntegerProperty width = new SimpleIntegerProperty();
    private final IntegerProperty height = new SimpleIntegerProperty();

    public PixelatedImageView(javafx.scene.image.Image image) {
        super(image);
        width.set((int) image.getWidth());
        height.set((int) image.getHeight());
        imageProperty().addListener((ov, o, n) -> {
            width.set((int) n.getWidth());
            height.set((int) n.getHeight());
        });

        try {
            initialize();
        } catch (IllegalAccessException e) {
            ExceptionHandler.handle(e);
        }
    }

    private void initialize() throws IllegalAccessException {
        Object nodeHelper = FieldUtils.readField(this, "nodeHelper", true);
        FieldUtils.writeField(nodeHelper, "imageViewAccessor", null, true);
        ImageViewHelper.setImageViewAccessor(new ImageViewHelper.ImageViewAccessor() {
            @Override
            public NGNode doCreatePeer(Node node) {
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

            @Override
            public void doUpdatePeer(Node node) {
                ReflectionUtil.invokeMethod(node, "doUpdatePeer");
            }

            @Override
            public BaseBounds doComputeGeomBounds(Node node, BaseBounds bounds, BaseTransform tx) {
                return ReflectionUtil.invokeMethod(node, "doComputeGeomBounds", bounds, tx);
            }

            @Override
            public boolean doComputeContains(Node node, double localX, double localY) {
                return ReflectionUtil.invokeMethod(node, "doComputeContains", localX, localY);
            }
        });
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
