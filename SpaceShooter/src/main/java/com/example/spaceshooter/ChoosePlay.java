package com.example.spaceshooter;

import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;


public class ChoosePlay extends StackPane {

    public ChoosePlay(Runnable onPlay1, Runnable onPlay2) {

        setAlignment(Pos.CENTER);
        setPrefSize(1280, 720);

        String path = getClass().getResource("/assets/video/space_pixel_background.mp4").toExternalForm();
        Media media = new Media(path);
        MediaPlayer mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        mediaPlayer.setAutoPlay(true);

        MediaView mediaView = new MediaView(mediaPlayer);
        mediaView.setFitWidth(1280);
        mediaView.setFitHeight(720);
        mediaView.setPreserveRatio(false);

        MenuItem onePlay = new MenuItem("1 Player");
        MenuItem twoPlay = new MenuItem("2 Players");
     

        VBox menuItems = new VBox(10);
        menuItems.setAlignment(Pos.CENTER);
        menuItems.getChildren().addAll(onePlay, twoPlay);

        onePlay.setOnActivate(onPlay1);
        twoPlay.setOnActivate(onPlay2);

        getChildren().addAll(mediaView, menuItems);
    }
    
}
