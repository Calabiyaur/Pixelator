package com.calabi.pixelator.view.editor.window;

import java.util.List;
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

    private ImageButton addFrame;
    private ImageButton deleteFrame;

    private ImageButton previousFrame;
    private ImageButton nextFrame;
    private ToggleImageButton play;
    private ToggleImageButton expand;

    private FlowPane frames;

    public AnimationLayout(ImageWindow view) {
        super(view);
    }

    @Override
    public Node createGraphic() {
        return null;
    }

    @Override
    public Region createLowerContent() {

        createNodes();
        initBehavior();

        // Create content
        HBox frameButtonsPane = new HBox(addFrame, deleteFrame,
                new BalloonRegion(), previousFrame, nextFrame, play,
                new BalloonRegion(), expand);
        VBox framePane = new VBox(frameButtonsPane);

        // Handle collapsing / expanding
        expand.selectedProperty().addListener((ov, o, n) -> Do.when(n,
                () -> framePane.getChildren().add(frames),
                () -> framePane.getChildren().remove(frames)
        ));

        return framePane;
    }

    private void createNodes() {
        // Button pane nodes that are always visible:
        addFrame = new ImageButton(Images.ADD_FRAME);
        deleteFrame = new ImageButton(Images.REMOVE_FRAME);

        previousFrame = new ImageButton(Images.PREVIOUS_FRAME);
        nextFrame = new ImageButton(Images.NEXT_FRAME);

        play = new ToggleImageButton(Images.PLAY, Images.PAUSE);

        expand = new ToggleImageButton(Images.DROP_ARROW_DOWN, Images.DROP_ARROW_UP);

        // Frame pane nodes
        frames = new FlowPane();
    }

    private void initBehavior() {
        addFrame.setOnAction(e -> editor.addFrame());
        deleteFrame.setOnAction(e -> editor.removeFrame());

        previousFrame.setOnAction(e -> editor.previousFrame());
        nextFrame.setOnAction(e -> editor.nextFrame());

        // Store index because we want to continue with the frame we left off with
        AtomicInteger i = new AtomicInteger(0);
        play.selectedProperty().addListener((ov, o, n) -> Do.when(n, () -> {
            i.set(image.getIndex());
            editor.play();
        }, () -> {
            editor.stop();
            image.setIndex(i.get());
        }));

        // Synchronize images with the underlying image's frames
        PlatformImageList frameList = new PlatformImageList(image);
        addFrames(frameList);
        frameList.addListener((ListChangeListener<Image>) c -> {
            while (c.next()) {
                removeFrames(c.getRemoved());
                addFrames(c.getAddedSubList());
            }
        });
    }

    private void addFrames(List<? extends Image> frameList) {
        for (Image platformImage : frameList) {
            PixelatedImageView frameView = new PixelatedImageView(platformImage);
            frameView.setOnMousePressed(e -> image.setIndex(frames.getChildren().indexOf(frameView)));
            frames.getChildren().add(frameView);
        }
    }

    private void removeFrames(List<? extends Image> frameList) {
        for (Image platformImage : frameList) {
            frames.getChildren().removeIf(iv -> iv instanceof ImageView && ((ImageView) iv).getImage() == platformImage);
        }
    }

}
