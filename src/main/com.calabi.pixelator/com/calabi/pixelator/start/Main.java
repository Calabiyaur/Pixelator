package com.calabi.pixelator.start;

import java.util.ArrayList;
import java.util.List;

import javafx.application.Application;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.input.Clipboard;
import javafx.stage.Stage;

import com.calabi.pixelator.logging.Logger;
import com.calabi.pixelator.res.Config;
import com.calabi.pixelator.res.Images;

public class Main extends Application {

    private static final String TITLE = "Pixelator";
    private static Stage primaryStage;
    private static List<Stage> stages = new ArrayList<>();
    private static BooleanProperty clipboardActive = new SimpleBooleanProperty();

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static List<Stage> getStages() {
        return stages;
    }

    public static BooleanProperty clipboardActiveProperty() {
        return clipboardActive;
    }

    @Override
    public void start(Stage primaryStage) {

        Logger.log("Application started!");
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> ExceptionHandler.handle(e));
        initClipboardListener();

        Main.primaryStage = primaryStage;
        MainScene scene = new MainScene();
        primaryStage.getIcons().add(Images.ICON.getImage());
        primaryStage.setMaximized(Config.FULLSCREEN.getBoolean());
        primaryStage.setMinWidth(755);
        primaryStage.setMinHeight(530);

        primaryStage.setScene(scene);
        primaryStage.setTitle(TITLE);
        primaryStage.setOnCloseRequest(e -> {
            if (!scene.closeAll()) {
                // If closing the scene is unsuccessful, don't close the stage.
                e.consume();
                return;
            }
            updateConfig(primaryStage);
            stages.forEach(s -> s.close());
        });

        List<String> files = getParameters().getRaw();
        scene.openFiles(files);

        primaryStage.show();
    }

    private void initClipboardListener() {
        final Clipboard systemClipboard = Clipboard.getSystemClipboard();

        new com.sun.glass.ui.ClipboardAssistance(com.sun.glass.ui.Clipboard.SYSTEM) {
            @Override
            public void contentChanged() {
                clipboardActive.set(systemClipboard.hasImage());
            }
        };
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

            if (!fullscreen) {
                Config.WIDTH.putDouble(width);
                Config.HEIGHT.putDouble(height);
            }
            Config.FULLSCREEN.putBoolean(fullscreen);
        } catch (Exception e) {
            Logger.log(e.getMessage() + "\nError while trying to close application. Closing anyway.");
        }
    }

}
