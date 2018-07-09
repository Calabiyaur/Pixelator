package main.java.standard.control.basic;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public abstract class AbstractTabPane<T extends BasicTab> extends BorderPane {

    private HBox northBox = new HBox();
    private VBox westBox = new VBox();

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
    
    public AbstractTabPane() {
        setCenter(stackPane);
        BorderPane.setMargin(stackPane, new Insets(0, 0, 0, 6));
        tabs.addListener((ListChangeListener<T>) c -> {
            while (c.next()) {
                c.getAddedSubList().forEach(added -> onTabAdded(added));
                c.getRemoved().forEach(removed -> onTabRemoved(removed));
            }
        });

        northBox.setSpacing(6);
        westBox.setSpacing(6);
        setTop(new Group(northBox));
        setLeft(new Group(westBox));

        toggleGroup.selectedToggleProperty().addListener((ov, o, n) -> {
            if (n == null) {
                o.setSelected(true);
            }
        });
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
