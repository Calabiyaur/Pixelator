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
import javafx.geometry.Pos;
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
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import com.calabi.pixelator.control.basic.BasicCheckBox;
import com.calabi.pixelator.control.basic.BasicIntegerField;
import com.calabi.pixelator.control.basic.ToggleImageButton;
import com.calabi.pixelator.control.basic.UndeselectableToggleGroup;
import com.calabi.pixelator.control.image.PixelatedImageView;
import com.calabi.pixelator.control.region.BalloonRegion;
import com.calabi.pixelator.res.Config;
import com.calabi.pixelator.res.Images;
import com.calabi.pixelator.util.Do;
import com.calabi.pixelator.view.tool.Select;
import com.calabi.pixelator.view.tool.Tool;
import com.calabi.pixelator.view.tool.Tools;

public class ToolView extends VBox {

    private static ToolView instance;
    private List<Tools> tools = new ArrayList<>();
    private ObjectProperty<Tools> currentTool = new SimpleObjectProperty<>();
    private int maxX;
    private int maxY;
    private BooleanProperty replaceColor = new SimpleBooleanProperty();
    private BooleanProperty alphaOnly = new SimpleBooleanProperty();
    private BooleanProperty fillShape = new SimpleBooleanProperty();
    private IntegerProperty thickness = new SimpleIntegerProperty();
    private IntegerProperty bulge = new SimpleIntegerProperty();
    private Label preview = new Label();
    private Label previewTool = new Label();
    private Label previewSelection = new Label();
    private Pane clipWrapper;
    private Text sizeText = new Text();
    private Text zoomText = new Text();
    private Text frameIndexText = new Text();

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

        clipWrapper = new Pane();
        VBox.setVgrow(clipWrapper, Priority.ALWAYS);

        Rectangle clip = new Rectangle();
        clip.widthProperty().bind(clipWrapper.widthProperty());
        clip.heightProperty().bind(clipWrapper.heightProperty());

        StackPane previewStack = new StackPane(preview, previewTool, previewSelection);
        clipWrapper.getChildren().add(previewStack);
        previewStack.setClip(clip);
        previewStack.setAlignment(Pos.TOP_LEFT);

        clipWrapper.maxWidthProperty().bind(previewStack.widthProperty());
        clipWrapper.maxHeightProperty().bind(previewStack.heightProperty());

        HBox detailBoxTop = new HBox(sizeText, new BalloonRegion(), zoomText);
        VBox detailBox = new VBox(detailBoxTop, frameIndexText);
        VBox previewGrid = new VBox(new Label("PREVIEW"), clipWrapper, detailBox);
        VBox.setVgrow(previewGrid, Priority.ALWAYS);

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
        return new ToolSettings(maxX, maxY, replaceColor.get(), alphaOnly.get(), fillShape.get(), thickness.get(), bulge.get());
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
        Arrays.asList(replaceColorField, alphaOnlyField, fillShapeField, thicknessField).forEach(field -> {
            field.getControlWrapper().setPrefWidth(45);
            field.setMinWidth(100);
        });

        thicknessField.setMinValue(1);
        thicknessField.setMaxValue(10);
        ToggleGroup tg = new UndeselectableToggleGroup();
        ToggleImageButton bulgeLeft = new ToggleImageButton(tg, Images.BULGE_LEFT);
        ToggleImageButton bulgeCenter = new ToggleImageButton(tg, Images.BULGE_CENTER);
        ToggleImageButton bulgeRight = new ToggleImageButton(tg, Images.BULGE_RIGHT);

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

        GridPane prefBox = new GridPane();
        prefBox.addRow(0, replaceColorField);
        prefBox.addRow(1, alphaOnlyField);
        prefBox.addRow(2, fillShapeField);
        prefBox.addRow(3, thicknessField, bulgeLeft, bulgeCenter, bulgeRight);
        prefBox.setVgap(4);
        GridPane.setMargin(bulgeLeft, new Insets(0, 0, 0, 3));
        GridPane.setMargin(bulgeCenter, new Insets(0, 1, 0, 1));

        return prefBox;
    }

    private Pane createTextView() {
        GridPane grid = new GridPane();
        grid.setVgap(6);
        grid.setHgap(6);

        Text actingText = new Text();
        Tool.actingToolProperty().addListener((ov, o, n) -> actingText.setText(n.toString()));
        grid.addRow(0, new Text("actingTool ="), actingText);

        Text selectText = new Text();
        Select.getMe().typeProperty().addListener((ov, o, n) -> selectText.setText(n.name()));
        grid.addRow(1, new Text("select.type ="), selectText);

        return grid;
    }

    private void initConfig() {
        currentTool.addListener((ov, o, n) -> Config.TOOL.putInt(tools.indexOf(n)));
        replaceColor.addListener((ov, o, n) -> Config.REPLACE.putBoolean(n));
        alphaOnly.addListener((ov, o, n) -> Config.ALPHA_ONLY.putBoolean(n));
        fillShape.addListener((ov, o, n) -> Config.FILL_SHAPE.putBoolean(n));
        thickness.addListener((ov, o, n) -> Config.THICKNESS.putInt(n.intValue()));
        bulge.addListener((ov, o, n) -> Config.BULGE.putInt(n.intValue()));
    }

    public void setPreview(Image image, Image toolImage, Image selectionImage) {
        if (image == null) {
            preview.setGraphic(null);
            previewTool.setGraphic(null);
            previewSelection.setGraphic(null);
        } else {
            preview.setGraphic(new PixelatedImageView(image));
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
        if (preview.getWidth() > clipWrapper.getWidth()) {
            double xTranslate =
                    -Math.min(Math.max(0, x - clipWrapper.getWidth() / 2), preview.getWidth() - clipWrapper.getWidth());
            preview.setTranslateX(Math.round(xTranslate));
            previewTool.setTranslateX(Math.round(xTranslate));
            previewSelection.setTranslateX(Math.round(xTranslate));
        } else {
            preview.setTranslateX(0);
            previewTool.setTranslateX(0);
            previewSelection.setTranslateX(0);
        }

        if (preview.getHeight() > clipWrapper.getHeight()) {
            double yTranslate =
                    -Math.min(Math.max(0, y - clipWrapper.getHeight() / 2), preview.getHeight() - clipWrapper.getHeight());
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

}
