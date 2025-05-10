package com.example.spaceshooter;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

public class PauseMenu extends VBox {
    public PauseMenu(Runnable onResume, Runnable onRestart, Runnable onSettings, Runnable onExit) {
        setSpacing(15);
        setAlignment(Pos.CENTER);
        setStyle("-fx-background-color: rgba(0,0,0,6); -fx-padding: 40px;");
        setPrefSize(800, 600);

        Button resumeBtn = new Button("Resume");
        Button restartBtn = new Button("Restart");
        Button settingsBtn = new Button("Settings");
        Button exitBtn = new Button("Exit to Menu");

        resumeBtn.setOnAction(e -> onResume.run());
        restartBtn.setOnAction(e -> onRestart.run());
        settingsBtn.setOnAction(e -> onSettings.run());
        exitBtn.setOnAction(e -> {
            MenuScene.showMenu(Main.mainStage);
        });

        getChildren().addAll(resumeBtn, restartBtn, settingsBtn, exitBtn);
    }
}

