package com.calabi.pixelator.view.dialog;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.VBox;

import com.calabi.pixelator.config.Config;
import com.calabi.pixelator.project.Project;
import com.calabi.pixelator.project.RecentProjectsConfig;
import com.calabi.pixelator.ui.control.UndeselectableToggleGroup;

public class OpenRecentProjectDialog extends BasicDialog {

    private final List<ToggleButton> buttons = new ArrayList<>();

    public OpenRecentProjectDialog() {
        setTitle("Open a recent Project");
        setOkText("Open");

        setPrefSize(700, 400);

        RecentProjectsConfig recentProjects = Config.RECENT_PROJECTS.getObject();

        if (!recentProjects.getFiles().isEmpty()) {
            VBox container = new VBox();
            ToggleGroup tg = new UndeselectableToggleGroup();

            for (File file : recentProjects.getFiles()) {
                ToggleButton button = new ToggleButton(file.getAbsolutePath());
                button.setToggleGroup(tg);
                button.setOnMouseClicked(e -> {
                    if (MouseButton.PRIMARY.equals(e.getButton()) && e.getClickCount() == 2) {
                        ok.fire();
                    }
                });
                container.getChildren().add(button);
                buttons.add(button);
            }

            addContent(container, 0, 0);

        } else {
            Label label = new Label("No recent projects found.");
            addContent(label, 0, 0);
            ok.setVisible(false);
            cancel.setText("Close");
        }
    }

    @Override
    public void focus() {
        if (!buttons.isEmpty()) {
            buttons.get(0).requestFocus();
        }
    }

    public Project getProject() {
        for (ToggleButton button : buttons) {
            if (button.isSelected()) {
                File file = new File(button.getText());
                if (file.exists()) {
                    return new Project(file);
                }
                break;
            }
        }
        return null;
    }

}
