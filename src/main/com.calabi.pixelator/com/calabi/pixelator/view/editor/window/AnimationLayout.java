package com.calabi.pixelator.view.editor.window;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javafx.collections.ListChangeListener;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;

import com.calabi.pixelator.control.basic.ImageButton;
import com.calabi.pixelator.control.basic.ToggleImageButton;
import com.calabi.pixelator.control.image.PlatformImageList;
import com.calabi.pixelator.control.parent.BasicScrollPane;
import com.calabi.pixelator.control.parent.DraggablePane.BorderRegion;
import com.calabi.pixelator.control.region.BalloonRegion;
import com.calabi.pixelator.res.Images;
import com.calabi.pixelator.util.Do;

import static com.calabi.pixelator.control.parent.DraggablePane.RESIZE_MARGIN;

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

        // Handle collapsing / expanding
        BorderRegion borderW = new BorderRegion(view, Cursor.W_RESIZE, RESIZE_MARGIN, view, flowWrapper);
        BorderRegion borderSW = new BorderRegion(view, Cursor.SW_RESIZE, RESIZE_MARGIN, view, flowWrapper);
        BorderRegion borderS = new BorderRegion(view, Cursor.S_RESIZE, RESIZE_MARGIN, view, flowWrapper);
        BorderRegion borderSE = new BorderRegion(view, Cursor.SE_RESIZE, RESIZE_MARGIN, view, flowWrapper);
        BorderRegion borderE = new BorderRegion(view, Cursor.E_RESIZE, RESIZE_MARGIN, view, flowWrapper);
        expand.selectedProperty().addListener((ov, o, n) -> Do.when(n,
                () -> {
                    view.add(flowWrapper, 1, 5, 2, 1);
                    view.add(borderW, 0, 5);
                    view.add(borderSW, 0, 6);
                    view.add(borderS, 1, 6, 2, 1);
                    view.add(borderSE, 3, 6);
                    view.add(borderE, 3, 5);
                    double flowWrapperHeight = flowWrapper.getHeight() > 0 ? flowWrapper.getHeight() : image.getHeight();
                    view.setPrefHeight(view.getPrefHeight() + flowWrapperHeight + RESIZE_MARGIN);
                },
                () -> {
                    view.remove(flowWrapper);
                    view.remove(borderW);
                    view.remove(borderSW);
                    view.remove(borderS);
                    view.remove(borderSE);
                    view.remove(borderE);
                    view.setPrefHeight(view.getPrefHeight() - flowWrapper.getHeight() - RESIZE_MARGIN);
                }
        ));

        return frameButtonsPane;
    }

    @Override
    public double getExtraHeight() {
        return expand.getHeight()
                + 4
                + (expand.isSelected() ? flowWrapper.getHeight() + RESIZE_MARGIN : 0);
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
        flowPane = new FlowPane();
        flowWrapper = new BasicScrollPane(flowPane);
        flowWrapper.setStyle("-fx-background-color: #DDDDDDFF");
        flowWrapper.setMinHeight(Math.min(image.getHeight(), MAX_FRAME_HEIGHT));
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
