package com.example.spaceshooter;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Random;

public class Main extends Application {
    Player player;
    ArrayList<Bullet> bullets = new ArrayList<>();
    ArrayList<Enemy> enemies = new ArrayList<>();
    long lastSpawn = 0;

    @Override
    public void start(Stage stage) {
        Canvas canvas = new Canvas(800, 600);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        // 🎥 Tạo video nền
        Media media = new Media(getClass().getResource("/assets/video/space_pixel_background.mp4").toExternalForm());
        MediaPlayer mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        mediaPlayer.setMute(true); // Tắt tiếng nếu có âm
        MediaView mediaView = new MediaView(mediaPlayer);
        mediaView.setFitWidth(800);
        mediaView.setFitHeight(600);
        mediaView.setPreserveRatio(false);
        mediaPlayer.play();

        // 📦 Root chứa video + canvas game
        Pane root = new Pane();
        root.getChildren().addAll(mediaView, canvas); // video bên dưới, game vẽ lên trên

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Space Shooter with Video Background");

        Assets.load();
        player = new Player(380, 500);

        // 🔑 Điều khiển
        scene.setOnKeyPressed(e -> {
            switch (e.getCode()) {
                case LEFT -> player.move(-10);
                case RIGHT -> player.move(10);
                case SPACE -> bullets.add(player.shoot());
            }
        });

        // 🎮 Game loop
        new AnimationTimer() {
            public void handle(long now) {
                gc.clearRect(0, 0, 800, 600); // ✅ Quan trọng!

                player.render(gc);

                bullets.removeIf(b -> !b.update());
                bullets.forEach(b -> b.render(gc));

                if (now - lastSpawn > 2_000_000_000) {
                    enemies.add(new Enemy(new Random().nextInt(760), -30));
                    lastSpawn = now;
                }

                for (Enemy e : enemies) {
                    e.update();
                }
                enemies.forEach(e -> e.render(gc));
            }
        }.start();

        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
