package com.calabi.pixelator.view.dialog;

import java.io.File;

import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

import com.calabi.pixelator.project.Project;
import com.calabi.pixelator.ui.control.BasicDirectoryField;
import com.calabi.pixelator.view.editor.IWC;
import com.calabi.pixelator.view.editor.window.ImageWindow;

public class NewProjectDialog extends BasicDialog {

    private final BasicDirectoryField locationField;

    public NewProjectDialog() {
        setTitle("Create a new Project");
        setOkText("Create");

        setPrefSize(700, 400);

        // Find location of last opened image
        File file = null;
        for (ImageWindow imageWindow : IWC.get().imageWindows()) {
            if (imageWindow.getFile().getFile() != null) {
                file = imageWindow.getFile().getFile();
                break;
            }
        }

        File location = file == null ? new File("") : file.getParentFile();

        locationField = new BasicDirectoryField("Project location", location);
        GridPane.setHgrow(locationField, Priority.ALWAYS);

        addContent(locationField, 0, 0);
    }

    @Override
    public void focus() {
        locationField.focus();
    }

    public Project getProject() {
        File location = locationField.getValue();
        if (location != null && location.exists()) {
            return new Project(location);
        } else {
            return null;
        }
    }

}
