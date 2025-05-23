package com.example.spaceshooter;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class MusicPlayer {
    private MediaPlayer player;

    public MusicPlayer(String fileName) {
        String path = getClass().getResource("/assets/music/" + fileName).toExternalForm();
        Media media = new Media(path);
        player = new MediaPlayer(media);
        player.setCycleCount(MediaPlayer.INDEFINITE);
        player.setVolume(0.2);
    }

    public void play() {
        player.play();
    }

    public void stop() {
        player.stop();
    }

    public void pause() {
        player.pause();
    }

    public void setVolume(double volume) {
        player.setVolume(volume);
    }
}
