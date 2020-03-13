package com.calabi.pixelator.view.editor.window;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javafx.collections.ListChangeListener;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import com.calabi.pixelator.control.basic.ImageButton;
import com.calabi.pixelator.control.basic.ToggleImageButton;
import com.calabi.pixelator.control.image.PlatformImageList;
import com.calabi.pixelator.control.parent.BasicScrollPane;
import com.calabi.pixelator.control.region.BalloonRegion;
import com.calabi.pixelator.res.Images;
import com.calabi.pixelator.util.Do;

public class AnimationLayout extends Layout {

    private static final int MAX_FRAME_WIDTH = 100;
    private static final int MAX_FRAME_HEIGHT = 100;

    private ImageButton addFrame;
    private ImageButton deleteFrame;

    private ImageButton previousFrame;
    private ImageButton nextFrame;
    private ToggleImageButton play;
    private ToggleImageButton expand;

    private FlowPane flowPane;
    private BasicScrollPane flowWrapper;

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
        frameButtonsPane.setMinWidth(0);
        VBox framePane = new VBox(frameButtonsPane);

        // Handle collapsing / expanding
        expand.selectedProperty().addListener((ov, o, n) -> Do.when(n,
                () -> framePane.getChildren().add(flowWrapper),
                () -> framePane.getChildren().remove(flowWrapper)
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
        flowPane = new FlowPane(); //TODO: Make frame preview a popup to solve the resize dilemma (= "image or frame preview?")
        flowWrapper = new BasicScrollPane(flowPane);
        flowWrapper.setScrollByMouse(true);
        flowWrapper.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        flowWrapper.setFitToWidth(true);
        flowWrapper.setCenterContent(false);
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
            FrameCell frameView = new FrameCell(platformImage);
            frameView.setOnMousePressed(e -> image.setIndex(flowPane.getChildren().indexOf(frameView)));
            flowPane.getChildren().add(frameView);
        }
    }

    private void removeFrames(List<? extends Image> frameList) {
        for (Image platformImage : frameList) {
            flowPane.getChildren().removeIf(iv -> iv instanceof FrameCell && ((FrameCell) iv).image == platformImage);
        }
    }

    private static class FrameCell extends Pane {

        final ImageView imageView;
        final Image image;

        public FrameCell(Image image) {
            this.imageView = new ImageView(image);
            this.image = image;

            getChildren().add(imageView);

            style();
        }

        private void style() {
            if (image.getWidth() > MAX_FRAME_WIDTH || image.getHeight() > MAX_FRAME_HEIGHT) {
                double factor = Math.min(MAX_FRAME_WIDTH / image.getWidth(), MAX_FRAME_HEIGHT / image.getHeight());
                imageView.setFitWidth(factor * image.getWidth());
                imageView.setFitHeight(factor * image.getHeight());
            }
        }

    }

}
