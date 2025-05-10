package com.example.spaceshooter;

import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {
    public static boolean useMouseControl = true;
    public static Stage mainStage;

    @Override
    public void start(Stage stage) {
        mainStage = stage;
        MenuScene.showMenu(stage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
