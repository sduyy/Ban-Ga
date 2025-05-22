package com.example.spaceshooter;

import javafx.scene.media.AudioClip;

public class SoundFX {
    private AudioClip clip;

    public SoundFX(String fileName) {
        try {
            clip = new AudioClip(getClass().getResource("/assets/sounds/" + fileName).toExternalForm());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void play() {
        if (clip != null) {
            clip.play();
        }
    }
}
