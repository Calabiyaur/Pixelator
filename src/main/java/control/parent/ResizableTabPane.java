package main.java.control.parent;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.SplitPane;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import main.java.meta.Direction;

public class ResizableTabPane<T extends BasicTab> extends ResizableBorderPane {

    private Pane northBox;
    private Pane westBox;

    private StackPane stackPane = new StackPane();
    private ObservableList<T> tabs = FXCollections.observableArrayList();
    private ToggleGroup toggleGroup = new ToggleGroup();
    private SingleSelectionModel<T> selectionModel = new SingleSelectionModel<T>() {
        @Override protected T getModelItem(int index) {
            return tabs.get(index);
        }

        @Override protected int getItemCount() {
            return tabs.size();
        }
    };

    public ResizableTabPane(Direction rotateNorth, Direction rotateWest) {
        tabs.addListener((ListChangeListener<T>) c -> {
            while (c.next()) {
                c.getAddedSubList().forEach(added -> onTabAdded(added));
                c.getRemoved().forEach(removed -> onTabRemoved(removed));
            }
        });

        initNorth(rotateNorth);
        initWest(rotateWest);
        setCenter(stackPane);
        setRight(null);
        getSplitPane().setDividerPosition(0, 0.25);

        toggleGroup.selectedToggleProperty().addListener((ov, o, n) -> {
            if (n == null) {
                o.setSelected(true);
            }
        });
    }

    @SuppressWarnings("Duplicates")
    private void initNorth(Direction rotate) {
        if (rotate.isVertical()) {
            northBox = new VBox();
            ((VBox) northBox).setSpacing(6);
        } else if (rotate.isHorizontal()) {
            northBox = new HBox();
            ((HBox) northBox).setSpacing(6);
        }
        northBox.setRotate(rotate.getRotate());
        setTop(new Group(northBox));
    }

    @SuppressWarnings("Duplicates")
    private void initWest(Direction rotate) {
        if (rotate.isVertical()) {
            westBox = new VBox();
            ((VBox) westBox).setSpacing(6);
        } else if (rotate.isHorizontal()) {
            westBox = new HBox();
            ((HBox) westBox).setSpacing(6);
        }
        westBox.setRotate(rotate.getRotate());
        Pane left = new Pane(new Group(westBox));
        SplitPane.setResizableWithParent(left, false);
        setLeft(left);
    }

    public void addTab(T tab, String text) {
        tabs.add(tab);
        tab.setText(text);
    }

    public void removeTab(T tab) {
        tabs.remove(tab);
    }

    private void onTabAdded(T tab) {
        stackPane.getChildren().add(tab);
        TabToggle toggle = tab.getToggle();

        // Add toggle to respective side of the border pane
        switch(toggle.getDirection()) {
            case NORTH:
                northBox.getChildren().add(toggle);
                break;
            case WEST:
                westBox.getChildren().add(toggle);
                break;
            default:
                throw new IllegalStateException("Only simple directions allowed!");
        }

        toggle.setToggleGroup(toggleGroup);
        toggle.selectedProperty().addListener((ov, o, n) -> {
            if (n) {
                selectionModel.select(tab);
            }
        });

        toggle.setOnMouseClicked(e -> {
            if (MouseButton.MIDDLE.equals(e.getButton()) && toggle.isClosable()) {
                tabs.remove(tab);
            }
        });

        toggle.setSelected(true);
    }

    private void onTabRemoved(T tab) {
        stackPane.getChildren().remove(tab);
        ((Pane) tab.getToggle().getParent()).getChildren().remove(tab.getToggle());
    }

    public final SingleSelectionModel<T> getSelectionModel() {
        return selectionModel;
    }

}
