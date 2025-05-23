package com.example.spaceshooter;

import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {
    public static boolean useMouseControl = true;
    public static Stage mainStage;
    public static javafx.scene.media.MediaPlayer sharedMediaPlayer;

    @Override
    public void start(Stage stage) {
        mainStage = stage;
        StartScreen.showMenu(stage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
