package com.calabi.pixelator.view;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import com.calabi.pixelator.config.Config;
import com.calabi.pixelator.config.Images;
import com.calabi.pixelator.ui.control.BasicCheckBox;
import com.calabi.pixelator.ui.control.BasicIntegerField;
import com.calabi.pixelator.ui.control.ToggleImageButton;
import com.calabi.pixelator.ui.control.UndeselectableToggleGroup;
import com.calabi.pixelator.ui.image.PixelatedImageView;
import com.calabi.pixelator.ui.image.ScalableImageView;
import com.calabi.pixelator.ui.parent.BasicScrollPane;
import com.calabi.pixelator.ui.region.BalloonRegion;
import com.calabi.pixelator.util.Do;
import com.calabi.pixelator.view.editor.IWC;
import com.calabi.pixelator.view.tool.Select;
import com.calabi.pixelator.view.tool.Tool;
import com.calabi.pixelator.view.tool.Tools;

public class ToolView extends VBox {

    private static ToolView instance;
    private final List<Tools> tools = new ArrayList<>();
    private final ObjectProperty<Tools> currentTool = new SimpleObjectProperty<>();
    private int maxX;
    private int maxY;
    private final BooleanProperty replaceColor = new SimpleBooleanProperty();
    private final BooleanProperty alphaOnly = new SimpleBooleanProperty();
    private final BooleanProperty fillShape = new SimpleBooleanProperty();
    private final IntegerProperty thickness = new SimpleIntegerProperty();
    private final IntegerProperty bulge = new SimpleIntegerProperty();
    private final IntegerProperty tolerance = new SimpleIntegerProperty();
    private final BooleanProperty allFrames = new SimpleBooleanProperty();
    private final BasicScrollPane previewContainer;
    private final Label preview = new Label();
    private final Label previewTool = new Label();
    private final Label previewSelection = new Label();
    private final Label sizeText = new Label();
    private final Label zoomText = new Label();
    private final Label frameIndexText = new Label();

    private ToolView() {
        setSpacing(6);
        setPadding(new Insets(6, 0, 6, 6));

        getChildren().add(new Label("TOOLS"));
        ToggleGroup tg = new UndeselectableToggleGroup();

        FlowPane tools1 = createFirstToolLayer(tg);
        tools1.setVgap(1);
        tools1.setHgap(1);
        getChildren().add(1, tools1);

        getChildren().add(2, new Separator());

        FlowPane tools2 = createSecondToolLayer(tg);
        tools2.setVgap(1);
        tools2.setHgap(1);
        getChildren().add(3, tools2);

        getChildren().add(4, new Separator());

        getChildren().add(5, createPrefLayer());

        getChildren().add(6, new Separator());

        StackPane previewStack = new StackPane(preview, previewTool, previewSelection);
        previewContainer = new BasicScrollPane(previewStack);

        HBox detailBoxTop = new HBox(sizeText, new BalloonRegion(), zoomText);
        VBox detailBox = new VBox(detailBoxTop, frameIndexText);
        VBox previewGrid = new VBox(new Label("PREVIEW"), previewContainer, detailBox);
        VBox.setVgrow(previewGrid, Priority.ALWAYS);

        previewContainer.setOnScroll(e -> {
            ScalableImageView graphic = (ScalableImageView) preview.getGraphic();
            if (graphic != null) {
                graphic.scroll(e);
                Config.PREVIEW_ZOOM_LEVEL.putDouble(IWC.get().getCurrentFile(), graphic.getZoom());
                previewStack.setPrefWidth(graphic.getScaleX() * graphic.getWidth());
                previewStack.setPrefHeight(graphic.getScaleY() * graphic.getHeight());
            }
        });

        previewGrid.visibleProperty().bind(preview.graphicProperty().isNotNull());
        getChildren().add(7, previewGrid);

        //getChildren().add(8, createTextView());

        initConfig();
    }

    public static ToolView get() {
        if (instance == null) {
            instance = new ToolView();
        }
        return instance;
    }

    public ToolSettings getSettings() {
        return new ToolSettings(maxX, maxY, replaceColor.get(), alphaOnly.get(), fillShape.get(), thickness.get(),
                bulge.get(), tolerance.get(), allFrames.get());
    }

    private FlowPane createFirstToolLayer(ToggleGroup tg) {
        tools.addAll(List.of(
                Tools.PEN,
                Tools.LINE,
                Tools.FILL,
                Tools.PICK,
                Tools.RECTANGLE,
                Tools.ELLIPSE,
                Tools.FILL_COLOR
        ));

        FlowPane flowPane = new FlowPane();
        for (Tools tool : tools) {
            ToggleImageButton button = new ToggleImageButton(tg, Images.valueOf(tool.name()));
            button.setOnAction(e -> currentTool.set(tool));
            flowPane.getChildren().add(button);
        }

        return flowPane;
    }

    private FlowPane createSecondToolLayer(ToggleGroup tg) {
        List<Tools> tools2 = List.of(
                Tools.SELECT,
                Tools.WAND,
                Tools.SELECT_COLOR
        );
        tools.addAll(tools2);

        FlowPane flowPane = new FlowPane();
        for (Tools tool : tools2) {
            ToggleImageButton button = new ToggleImageButton(tg, Images.valueOf(tool.name()));
            button.setOnAction(e -> currentTool.set(tool));
            flowPane.getChildren().add(button);
        }

        int index = Config.TOOL.getInt();
        if (0 <= index && index < tg.getToggles().size()) {
            ((ToggleButton) tg.getToggles().get(index)).fire();
        }

        return flowPane;
    }

    private Pane createPrefLayer() {
        BasicCheckBox replaceColorField = new BasicCheckBox("Replace", Config.REPLACE.getBoolean());
        BasicCheckBox alphaOnlyField = new BasicCheckBox("Alpha only", Config.ALPHA_ONLY.getBoolean());
        BasicCheckBox fillShapeField = new BasicCheckBox("Fill shape", Config.FILL_SHAPE.getBoolean());
        BasicIntegerField thicknessField = new BasicIntegerField("Thickness", Config.THICKNESS.getInt());
        BasicIntegerField toleranceField = new BasicIntegerField("Tolerance", "%", Config.TOLERANCE.getInt());
        BasicCheckBox allFramesField = new BasicCheckBox("All frames", Config.ALL_FRAMES.getBoolean());

        List.of(replaceColorField, alphaOnlyField, fillShapeField, thicknessField, toleranceField, allFramesField)
                .forEach(field -> field.getControlWrapper().setPrefWidth(45));

        thicknessField.setMinValue(1);
        thicknessField.setMaxValue(10);
        ToggleGroup tg = new UndeselectableToggleGroup();
        ToggleImageButton bulgeLeft = new ToggleImageButton(tg, Images.BULGE_LEFT);
        ToggleImageButton bulgeCenter = new ToggleImageButton(tg, Images.BULGE_CENTER);
        ToggleImageButton bulgeRight = new ToggleImageButton(tg, Images.BULGE_RIGHT);
        toleranceField.setMinValue(0);
        toleranceField.setMaxValue(99);

        replaceColor.bindBidirectional(replaceColorField.valueProperty());
        alphaOnly.bindBidirectional(alphaOnlyField.valueProperty());
        fillShape.bindBidirectional(fillShapeField.valueProperty());
        thickness.bind(thicknessField.valueProperty());
        List<ToggleImageButton> bulgeButtons = Arrays.asList(bulgeLeft, bulgeCenter, bulgeRight);
        thickness.addListener((ov, o, n) -> bulgeButtons.forEach(b -> b.setDisable(n.intValue() == 1)));
        bulgeLeft.selectedProperty().addListener((ov, o, n) -> Do.when(n, () -> bulge.set(-1)));
        bulgeCenter.selectedProperty().addListener((ov, o, n) -> Do.when(n, () -> bulge.set(0)));
        bulgeRight.selectedProperty().addListener((ov, o, n) -> Do.when(n, () -> bulge.set(1)));
        switch(Config.BULGE.getInt()) {
            case -1 -> bulgeLeft.fire();
            case 1 -> bulgeRight.fire();
            default -> bulgeCenter.fire();
        }
        bulgeButtons.forEach(b -> b.setDisable(getThickness() == 1));
        tolerance.bind(toleranceField.valueProperty());
        allFrames.bindBidirectional(allFramesField.valueProperty());

        GridPane prefBox = new GridPane();
        prefBox.addRow(0, replaceColorField.getFrontLabel(), replaceColorField.getControlWrapper());
        prefBox.addRow(1, alphaOnlyField.getFrontLabel(), alphaOnlyField.getControlWrapper());
        prefBox.addRow(2, fillShapeField.getFrontLabel(), fillShapeField.getControlWrapper());
        prefBox.addRow(3, thicknessField.getFrontLabel(), thicknessField.getControlWrapper(),
                new HBox(bulgeLeft, bulgeCenter, bulgeRight));
        prefBox.addRow(4, toleranceField.getFrontLabel(), toleranceField.getControlWrapper(), toleranceField.getBackLabel());
        prefBox.addRow(5, allFramesField.getFrontLabel(), allFramesField.getControlWrapper());
        prefBox.setVgap(4);
        HBox.setMargin(bulgeLeft, new Insets(0, 0, 0, 3));
        HBox.setMargin(bulgeCenter, new Insets(0, 1, 0, 1));

        return prefBox;
    }

    private Pane createTextView() {
        GridPane grid = new GridPane();
        grid.setVgap(6);
        grid.setHgap(6);

        Label actingText = new Label();
        Tool.actingToolProperty().addListener((ov, o, n) -> actingText.setText(n.toString()));
        grid.addRow(0, new Label("actingTool ="), actingText);

        Label selectText = new Label();
        Select.getMe().typeProperty().addListener((ov, o, n) -> selectText.setText(n.name()));
        grid.addRow(1, new Label("select.type ="), selectText);

        return grid;
    }

    private void initConfig() {
        currentTool.addListener((ov, o, n) -> Config.TOOL.putInt(tools.indexOf(n)));
        replaceColor.addListener((ov, o, n) -> Config.REPLACE.putBoolean(n));
        alphaOnly.addListener((ov, o, n) -> Config.ALPHA_ONLY.putBoolean(n));
        fillShape.addListener((ov, o, n) -> Config.FILL_SHAPE.putBoolean(n));
        thickness.addListener((ov, o, n) -> Config.THICKNESS.putInt(n.intValue()));
        bulge.addListener((ov, o, n) -> Config.BULGE.putInt(n.intValue()));
        tolerance.addListener((ov, o, n) -> Config.TOLERANCE.putInt(n.intValue()));
        allFrames.addListener((ov, o, n) -> Config.ALL_FRAMES.putBoolean(n));
    }

    public void reload() {
        replaceColor.set(Config.REPLACE.getBoolean());
        alphaOnly.set(Config.ALPHA_ONLY.getBoolean());
        fillShape.set(Config.FILL_SHAPE.getBoolean());
        //TODO: thickness.set(Config.THICKNESS.getInt());
        //TODO: bulge.set(Config.BULGE.getInt());
        //TODO: tolerance.set(Config.TOLERANCE.getInt());
        allFrames.set(Config.ALL_FRAMES.getBoolean());
    }

    public void setPreview(Image image, Image toolImage, Image selectionImage, double zoom) {
        if (image == null) {
            preview.setGraphic(null);
            previewTool.setGraphic(null);
            previewSelection.setGraphic(null);
        } else {
            preview.setGraphic(new ScalableImageView(image, zoom));
            preview.setTranslateX(0);
            preview.setTranslateY(0);
            previewTool.setGraphic(new PixelatedImageView(toolImage));
            previewTool.setTranslateX(0);
            previewTool.setTranslateY(0);
            previewSelection.setGraphic(new PixelatedImageView(selectionImage));
            previewSelection.setTranslateX(0);
            previewSelection.setTranslateY(0);
            preview.requestLayout();
        }
    }

    public void setPreviewPosition(double x, double y) {
        if (preview.getWidth() > previewContainer.getWidth()) {
            double xTranslate =
                    -Math.min(Math.max(0, x - previewContainer.getWidth() / 2), preview.getWidth() - previewContainer.getWidth());
            preview.setTranslateX(Math.round(xTranslate));
            previewTool.setTranslateX(Math.round(xTranslate));
            previewSelection.setTranslateX(Math.round(xTranslate));
        } else {
            preview.setTranslateX(0);
            previewTool.setTranslateX(0);
            previewSelection.setTranslateX(0);
        }

        if (preview.getHeight() > previewContainer.getHeight()) {
            double yTranslate =
                    -Math.min(Math.max(0, y - previewContainer.getHeight() / 2), preview.getHeight() - previewContainer.getHeight());
            preview.setTranslateY(Math.round(yTranslate));
            previewTool.setTranslateY(Math.round(yTranslate));
            previewSelection.setTranslateY(Math.round(yTranslate));
        } else {
            preview.setTranslateY(0);
            previewTool.setTranslateY(0);
            previewSelection.setTranslateY(0);
        }
    }

    public void setSize(int width, int height) {
        maxX = width;
        maxY = height;
        sizeText.setText(width + "×" + height);
    }

    public void setZoom(double zoom) {
        zoomText.setText(Math.round(zoom * 100) + " %");
    }

    public void setFrameIndex(int frameIndex, int frameCount) {
        frameIndexText.setText("Frame " + (frameIndex + 1) + " / " + frameCount);
    }

    public void hideFrameIndex() {
        frameIndexText.setText(null);
    }

    public ObjectProperty<Tools> currentToolProperty() {
        return currentTool;
    }

    public Tools getCurrentTool() {
        return currentTool.get();
    }

    public boolean isReplaceColor() {
        return replaceColor.get();
    }

    public void setReplaceColor(boolean replaceColor) {
        this.replaceColor.set(replaceColor);
    }

    public BooleanProperty replaceColorProperty() {
        return replaceColor;
    }

    public boolean isAlphaOnly() {
        return alphaOnly.get();
    }

    public boolean isFillShape() {
        return fillShape.get();
    }

    public void setFillShape(boolean fillShape) {
        this.fillShape.set(fillShape);
    }

    public int getThickness() {
        return thickness.get();
    }

    public int getBulge() {
        return bulge.get();
    }

    public boolean isAllFrames() {
        return allFrames.get();
    }

}
