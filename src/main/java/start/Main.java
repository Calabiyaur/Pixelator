package main.java.start;

import java.util.ArrayList;
import java.util.List;

import javafx.application.Application;
import javafx.stage.Stage;

import main.java.logging.Logger;
import main.java.res.Config;
import main.java.res.Images;
import main.java.view.ColorView;

public class Main extends Application {

    private static final String TITLE = "Pixelator";
    private static Stage primaryStage;
    private static List<Stage> stages = new ArrayList<>();

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static List<Stage> getStages() {
        return stages;
    }

    @Override public void start(Stage primaryStage) {

        Logger.log("Application started!");
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> ExceptionHandler.handle(e));

        Main.primaryStage = primaryStage;
        MainScene scene = new MainScene();
        primaryStage.getIcons().add(Images.ICON.getImage());
        primaryStage.setMaximized(Config.getBoolean(Config.FULLSCREEN, false));
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
                Config.putDouble(Config.WIDTH, width);
                Config.putDouble(Config.HEIGHT, height);
            }
            Config.putBoolean(Config.FULLSCREEN, fullscreen);
            Config.putDouble(Config.RED, ColorView.getColor().getRed());
            Config.putDouble(Config.GREEN, ColorView.getColor().getGreen());
            Config.putDouble(Config.BLUE, ColorView.getColor().getBlue());
            Config.putDouble(Config.OPACITY, ColorView.getColor().getOpacity());
        } catch (Exception e) {
            Logger.log(e.getMessage() + "\nError while trying to close application. Closing anyway.");
        }
    }

}
