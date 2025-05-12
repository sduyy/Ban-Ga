package com.example.spaceshooter;

import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;

public class PauseMenu extends StackPane {
    private VBox menuItems;
    public PauseMenu(Runnable onResume, Runnable onRestart, Runnable onSettings, Runnable onExit) {
        
        setAlignment(Pos.CENTER);
        setPrefSize(800, 600);
        // Đường dẫn đến video
        String path = getClass().getResource("/assets/video/space_pixel_background.mp4").toExternalForm();
        // Tạo Media và MediaPlayer
        Media media = new Media(path);
        MediaPlayer mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE); // Lặp lại video
        mediaPlayer.setAutoPlay(true);
        // Tạo MediaView để hiển thị video
        MediaView mediaView = new MediaView(mediaPlayer);
        mediaView.setFitWidth(800);  // Tuỳ chỉnh kích thước
        mediaView.setFitHeight(600);
        mediaView.setPreserveRatio(true);
        ;

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

        getChildren().addAll(mediaView,menuItems);
        
    }

     private MenuItem getMenuItem(int index) {
        return (MenuItem)menuItems.getChildren().get(index);
    }
}

