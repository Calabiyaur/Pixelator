package com.calabi.pixelator.main;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javafx.application.Application;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.input.Clipboard;
import javafx.scene.input.DataFormat;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import com.sun.glass.ui.ClipboardAssistance;

import com.calabi.pixelator.config.BuildInfo;
import com.calabi.pixelator.config.Config;
import com.calabi.pixelator.config.Images;
import com.calabi.pixelator.log.Logger;
import com.calabi.pixelator.project.Project;

public class Pixelator extends Application {

    public static final String TITLE = "Pixelator";
    static Stage primaryStage;
    static List<Stage> stages = new ArrayList<>();
    static BooleanProperty clipboardActive = new SimpleBooleanProperty();

    public static void main(String[] args) {
        launch(args);
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static List<Stage> getStages() {
        return stages;
    }

    public static BooleanProperty clipboardActiveProperty() {
        return clipboardActive;
    }

    public static void updateTitle() {
        primaryStage.setTitle(TITLE + " " + BuildInfo.getVersion()
                + (Project.active() ? " - " + Project.get().getName() : ""));
    }

    @Override
    public void start(Stage primaryStage) {
        Logger.log("Started " + TITLE + " " + BuildInfo.getVersion());
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> ExceptionHandler.handle(e));
        initClipboardListener();

        Pixelator.primaryStage = primaryStage;
        File projectFile = new File(Config.PROJECT.getString());
        if (projectFile.exists()) {
            Project.setSilently(new Project(projectFile));
        }

        MainScene scene = new MainScene();
        primaryStage.getIcons().add(Images.ICON.getImage());
        primaryStage.setMaximized(Config.FULLSCREEN.getBoolean());
        primaryStage.setX(Config.SCREEN_X.getDouble());
        primaryStage.setY(Config.SCREEN_Y.getDouble());
        primaryStage.setMinWidth(755);
        primaryStage.setMinHeight(530);

        primaryStage.setScene(scene);
        updateTitle();
        primaryStage.setOnCloseRequest(e -> onCloseRequest(primaryStage, scene, e));

        scene.openFiles(getParameters().getRaw());

        primaryStage.show();
    }

    private void initClipboardListener() {
        final Clipboard clipboard = Clipboard.getSystemClipboard();
        if (clipboard.getContent(DataFormat.IMAGE) != null) {
            clipboardActive.set(true);
        }

        new ClipboardAssistance(com.sun.glass.ui.Clipboard.SYSTEM) {
            @Override
            public void contentChanged() {
                clipboardActive.set(clipboard.hasImage());
            }
        };
    }

    private void onCloseRequest(Stage primaryStage, MainScene scene, WindowEvent e) {
        if (!scene.cleanAll()) {
            // If closing the scene is unsuccessful, don't close the stage.
            e.consume();
            return;
        }
        updateConfig(primaryStage);
        stages.forEach(Stage::close);
    }

    /**
     * Update only static config changes, such as:
     * - Window size
     * - Fullscreen
     * - Opened images
     */
    private void updateConfig(Stage stage) {
        try {
            MainScene scene = (MainScene) stage.getScene();
            double width = scene.getWidth();
            double height = scene.getHeight();
            boolean fullscreen = stage.isMaximized();
            double x = stage.getX();
            double y = stage.getY();

            if (!fullscreen) {
                Config.SCREEN_WIDTH.putDouble(width);
                Config.SCREEN_HEIGHT.putDouble(height);
            }
            Config.FULLSCREEN.putBoolean(fullscreen);
            Config.SCREEN_X.putDouble(x);
            Config.SCREEN_Y.putDouble(y);

        } catch (Exception e) {
            Logger.log(e.getMessage() + "\nError while trying to close application. Closing anyway.");
        }
    }

}
