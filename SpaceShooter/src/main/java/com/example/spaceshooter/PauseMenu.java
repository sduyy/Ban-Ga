package com.example.spaceshooter;

import javafx.geometry.Pos;
import javafx.scene.layout.VBox;

public class PauseMenu extends VBox {
    private VBox menuItems;
    public PauseMenu(Runnable onResume, Runnable onRestart, Runnable onSettings, Runnable onExit) {
        setSpacing(20);
        setAlignment(Pos.CENTER);
        setStyle("-fx-background-color: rgba(0,0,0,6); -fx-padding: 40px;");
        setPrefSize(800, 600);

        MenuItem resumeBtn = new MenuItem("Resume");
        MenuItem restartBtn = new MenuItem("Restart");
        MenuItem settingsBtn = new MenuItem("Settings");
        MenuItem exitBtn = new MenuItem("Exit to Menu");
        menuItems = new VBox(10);
        menuItems.setAlignment(Pos.CENTER);
        menuItems.getChildren().addAll(resumeBtn, restartBtn, settingsBtn, exitBtn);
        getMenuItem(0).setActive(true);


        resumeBtn.setOnActivate(onResume);
        restartBtn.setOnActivate(onRestart);
        settingsBtn.setOnActivate(onSettings);
        exitBtn.setOnActivate(onExit);
        
        getChildren().addAll(resumeBtn, restartBtn, settingsBtn, exitBtn);
    }

     private MenuItem getMenuItem(int index) {
        return (MenuItem)menuItems.getChildren().get(index);
    }
}

