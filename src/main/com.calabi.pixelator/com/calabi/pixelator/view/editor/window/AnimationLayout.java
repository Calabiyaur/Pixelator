package com.calabi.pixelator.view.editor.window;

import java.util.concurrent.atomic.AtomicInteger;

import javafx.collections.ListChangeListener;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import com.calabi.pixelator.control.basic.ImageButton;
import com.calabi.pixelator.control.basic.ToggleImageButton;
import com.calabi.pixelator.control.image.PixelatedImageView;
import com.calabi.pixelator.control.image.PlatformImageList;
import com.calabi.pixelator.control.region.BalloonRegion;
import com.calabi.pixelator.res.Images;
import com.calabi.pixelator.util.Do;

public class AnimationLayout extends Layout {

    public AnimationLayout(ImageWindow view) {
        super(view);
    }

    @Override
    public Node createGraphic() {
        return null;
    }

    @Override
    public Region createLowerContent() {

        ImageButton addFrame = new ImageButton(Images.ADD_FRAME);
        addFrame.setOnAction(e -> editor.addFrame());
        ImageButton deleteFrame = new ImageButton(Images.REMOVE_FRAME);
        deleteFrame.setOnAction(e -> editor.removeFrame());

        ImageButton previousFrame = new ImageButton(Images.PREVIOUS_FRAME);
        previousFrame.setOnAction(e -> editor.previousFrame());
        ImageButton nextFrame = new ImageButton(Images.NEXT_FRAME);
        nextFrame.setOnAction(e -> editor.nextFrame());
        ToggleImageButton play = new ToggleImageButton(Images.PLAY, Images.PAUSE);
        AtomicInteger i = new AtomicInteger(0);
        play.selectedProperty().addListener((ov, o, n) -> Do.when(n, () -> {
            i.set(image.getIndex());
            editor.play();
        }, () -> {
            editor.stop();
            image.setIndex(i.get());
        }));

        ToggleImageButton expand = new ToggleImageButton(Images.DROP_ARROW_DOWN, Images.DROP_ARROW_UP);
        FlowPane frames = new FlowPane();
        PlatformImageList frameList = new PlatformImageList(image);
        for (Image platformImage : frameList) {
            PixelatedImageView frameView = new PixelatedImageView(platformImage); //TODO Extract method
            frameView.setOnMousePressed(e -> image.setIndex(frames.getChildren().indexOf(frameView)));
            frames.getChildren().add(frameView);
        }
        frameList.addListener((ListChangeListener<Image>) c -> {
            while (c.next()) {
                for (Image platformImage : c.getRemoved()) {
                    frames.getChildren().removeIf(iv ->
                            iv instanceof ImageView && ((ImageView) iv).getImage() == platformImage);
                }
                for (Image platformImage : c.getAddedSubList()) {
                    PixelatedImageView frameView = new PixelatedImageView(platformImage); //TODO Extract method
                    frameView.setOnMousePressed(e -> image.setIndex(frames.getChildren().indexOf(frameView)));
                    frames.getChildren().add(frameView);
                }
            }
        });

        HBox frameButtonsPane = new HBox(addFrame, deleteFrame,
                new BalloonRegion(), previousFrame, nextFrame, play,
                new BalloonRegion(), expand);
        VBox framePane = new VBox(frameButtonsPane);

        expand.selectedProperty().addListener((ov, o, n) -> Do.when(n,
                () -> framePane.getChildren().add(frames),
                () -> framePane.getChildren().remove(frames)
        ));

        return framePane;
    }

}
