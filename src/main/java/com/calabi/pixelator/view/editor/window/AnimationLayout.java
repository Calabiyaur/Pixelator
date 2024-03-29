package com.calabi.pixelator.view.editor.window;

import java.util.List;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.ListChangeListener;
import javafx.event.Event;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;

import com.sun.javafx.scene.DirtyBits;
import com.sun.javafx.scene.NodeHelper;
import com.sun.javafx.tk.PlatformImage;

import com.calabi.pixelator.config.Config;
import com.calabi.pixelator.config.Images;
import com.calabi.pixelator.ui.control.ImageButton;
import com.calabi.pixelator.ui.control.ToggleImageButton;
import com.calabi.pixelator.ui.image.PlatformImageList;
import com.calabi.pixelator.ui.image.WritableImage;
import com.calabi.pixelator.ui.parent.BasicScrollPane;
import com.calabi.pixelator.ui.parent.DraggablePane.BorderRegion;
import com.calabi.pixelator.ui.region.BalloonRegion;
import com.calabi.pixelator.util.BackgroundBuilder;
import com.calabi.pixelator.util.Do;

import static com.calabi.pixelator.ui.parent.DraggablePane.RESIZE_MARGIN;

public class AnimationLayout extends Layout {

    private static final int MAX_FRAME_WIDTH = 100;
    private static final int MAX_FRAME_HEIGHT = 100;

    private ImageButton addFrame;
    private ImageButton copyFrame;
    private ImageButton deleteFrame;

    private ImageButton previousFrame;
    private ImageButton nextFrame;
    private ToggleImageButton play;
    private ToggleImageButton expand;

    private FlowPane flowPane;
    private BasicScrollPane flowWrapper;
    private BorderRegion borderW;
    private BorderRegion borderSW;
    private BorderRegion borderS;
    private BorderRegion borderSE;
    private BorderRegion borderE;

    private PlatformImageList frameList;

    private final ObjectProperty<FrameCell> selectedFrame = new SimpleObjectProperty<>();

    private final ChangeListener<Boolean> expandListener = (ov, o, n) -> Do.when(n, this::expand, this::collapse);

    private final ChangeListener<Boolean> playListener = (ov, o, n) -> Do.when(n, editor::play, editor::stop);

    private final ChangeListener<FrameCell> frameCellChangeListener = (ov, o, n) -> {
        editor.setFrameIndex(flowPane.getChildren().indexOf(n));
        if (o != null) {
            o.setBordered(false);
        }
        if (n != null) {
            n.setBordered(true);
        }
    };

    private final ChangeListener<Boolean> playingListener = (pov, po, pn) -> {
        Do.when(!pn, () -> play.setSelected(false));
    };
    private final ChangeListener<Number> indexChangeListener = (pov, po, pn) -> {
        selectedFrame.set(((FrameCell) flowPane.getChildren().get(pn.intValue())));
    };
    private final ListChangeListener<PlatformImage> frameListListener = new ListChangeListener<>() {
        @Override
        public void onChanged(Change<? extends PlatformImage> change) {
            frameList.reload(change.getList());
        }
    };
    private final ChangeListener<Image> imageChangeListener = (ov, o, n) -> {
        if (o instanceof WritableImage) {
            ((WritableImage) o).playingProperty().removeListener(playingListener);
            ((WritableImage) o).indexProperty().removeListener(indexChangeListener);
            ((WritableImage) o).getFrameList().removeListener(frameListListener);
        }
        frameList.reload(((WritableImage) n).getFrameList());
        ((WritableImage) n).playingProperty().addListener(playingListener);
        ((WritableImage) n).indexProperty().addListener(indexChangeListener);
        ((WritableImage) n).getFrameList().addListener(frameListListener);
    };

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
        HBox frameButtonsPane = new HBox(addFrame, copyFrame, deleteFrame,
                new BalloonRegion(), previousFrame, nextFrame, play,
                new BalloonRegion(), expand);
        frameButtonsPane.setMinWidth(0);

        // Handle collapsing / expanding
        expand.selectedProperty().addListener(expandListener);

        Platform.runLater(() -> view.setPrefHeight(view.getPrefHeight() + expand.getHeight() + 4));

        return frameButtonsPane;
    }

    private void expand() {
        if (borderW == null) {
            borderW = new BorderRegion(view, Cursor.W_RESIZE, RESIZE_MARGIN, view, flowWrapper);
            borderSW = new BorderRegion(view, Cursor.SW_RESIZE, RESIZE_MARGIN, view, flowWrapper);
            borderS = new BorderRegion(view, Cursor.S_RESIZE, RESIZE_MARGIN, view, flowWrapper);
            borderSE = new BorderRegion(view, Cursor.SE_RESIZE, RESIZE_MARGIN, view, flowWrapper);
            borderE = new BorderRegion(view, Cursor.E_RESIZE, RESIZE_MARGIN, view, flowWrapper);
        }

        view.add(flowWrapper, 1, 5, 2, 1);
        view.add(borderW, 0, 5);
        view.add(borderSW, 0, 6);
        view.add(borderS, 1, 6, 2, 1);
        view.add(borderSE, 3, 6);
        view.add(borderE, 3, 5);
        double fitHeight = Math.min(MAX_FRAME_HEIGHT, image.getHeight() + 2 * FrameCell.BORDER_WIDTH);
        double flowWrapperHeight = flowWrapper.getHeight() > 0 ? flowWrapper.getHeight() : fitHeight;
        flowWrapper.setPrefHeight(flowWrapperHeight);
        view.setPrefHeight(view.getPrefHeight() + flowWrapperHeight + RESIZE_MARGIN);
    }

    private void collapse() {
        view.remove(flowWrapper);
        view.remove(borderW);
        view.remove(borderSW);
        view.remove(borderS);
        view.remove(borderSE);
        view.remove(borderE);
        view.setPrefHeight(view.getPrefHeight() - flowWrapper.getHeight() - RESIZE_MARGIN);
    }

    @Override
    public double getExtraHeight() {
        return expand.getHeight()
                + 4
                + (expand.isSelected() ? flowWrapper.getHeight() + RESIZE_MARGIN : 0);
    }

    @Override
    public void refresh() {
        for (Node child : flowPane.getChildren()) {
            NodeHelper.markDirty(((FrameCell) child).imageView, DirtyBits.NODE_CONTENTS);
        }
    }

    @Override
    public void dispose() {
        if (expand.isSelected()) {
            collapse();
        }
        view.setPrefHeight(view.getPrefHeight() - expand.getHeight());

        expand.selectedProperty().removeListener(expandListener);
        play.selectedProperty().removeListener(playListener);
        selectedFrame.removeListener(frameCellChangeListener);
        imageView.imageProperty().removeListener(imageChangeListener);
    }

    private void createNodes() {
        // Button pane nodes that are always visible:
        addFrame = new ImageButton(Images.ADD_FRAME);
        copyFrame = new ImageButton(Images.DUPLICATE_FRAME);
        deleteFrame = new ImageButton(Images.REMOVE_FRAME);

        previousFrame = new ImageButton(Images.PREVIOUS_FRAME);
        nextFrame = new ImageButton(Images.NEXT_FRAME);

        play = new ToggleImageButton(Images.PLAY, Images.PAUSE);

        expand = new ToggleImageButton(Images.DROP_ARROW_DOWN, Images.DROP_ARROW_UP);

        // Frame pane nodes
        flowPane = new FlowPane();
        flowWrapper = new BasicScrollPane(flowPane);
        flowWrapper.setStyle("-fx-background-color: " + Config.IMAGE_BACKGROUND_COLOR.getString().replace("0x", "#"));
        flowWrapper.setMinHeight(Math.min(image.getHeight(), MAX_FRAME_HEIGHT));
        flowWrapper.setScrollByMouse(true);
        flowWrapper.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        flowWrapper.setFitToWidth(true);
        flowWrapper.setCenterContent(false);
        flowWrapper.setOnScrollFinished(Event::consume);
    }

    private void initBehavior() {
        addFrame.setOnAction(e -> editor.addFrame());
        copyFrame.setOnAction(e -> editor.duplicateFrame());
        deleteFrame.setOnAction(e -> editor.removeFrame());

        previousFrame.setOnAction(e -> editor.previousFrame());
        nextFrame.setOnAction(e -> editor.nextFrame());

        // Store index because we want to continue with the frame we left off with
        play.selectedProperty().addListener(playListener);

        // Show border around selected frame
        selectedFrame.addListener(frameCellChangeListener);

        // Synchronize images with the underlying image's frames
        frameList = new PlatformImageList(image);
        refreshFrames(frameList);
        frameList.addListener(() -> refreshFrames(frameList));
        imageView.imageProperty().addListener(imageChangeListener);
        imageChangeListener.changed(imageView.imageProperty(), null, image);
    }

    private void refreshFrames(List<? extends Image> frameList) {

        flowPane.getChildren().clear();

        for (int i = 0; i < frameList.size(); i++) {
            Image platformImage = frameList.get(i);
            FrameCell frameView = new FrameCell(platformImage);
            frameView.setOnMousePressed(e -> selectedFrame.set(frameView));
            flowPane.getChildren().add(frameView);

            if (i == image.getIndex()) {
                selectedFrame.set(frameView);
            }
        }
    }

    private static class FrameCell extends Pane {

        final static int BORDER_WIDTH = 2;

        final ImageView imageView;
        final Image image;

        FrameCell(Image image) {
            this.imageView = new ImageView(image);
            this.image = image;

            getChildren().add(imageView);

            style();
        }

        private void style() {

            setPadding(new Insets(BORDER_WIDTH));
            imageView.setTranslateX(BORDER_WIDTH);
            imageView.setTranslateY(BORDER_WIDTH);

            if (image.getWidth() > MAX_FRAME_WIDTH || image.getHeight() > MAX_FRAME_HEIGHT) {
                double factor = Math.min(MAX_FRAME_WIDTH / image.getWidth(), MAX_FRAME_HEIGHT / image.getHeight());
                imageView.setFitWidth(factor * image.getWidth());
                imageView.setFitHeight(factor * image.getHeight());
            }
        }

        void setBordered(boolean bordered) {
            Color background = Color.valueOf(Config.IMAGE_BACKGROUND_COLOR.getString());
            if (bordered) {
                setBackground(BackgroundBuilder.color(background).border(Color.BLACK).borderWidth(BORDER_WIDTH).build());
            } else {
                setBackground(BackgroundBuilder.color(background).build());
            }
        }

    }

}
