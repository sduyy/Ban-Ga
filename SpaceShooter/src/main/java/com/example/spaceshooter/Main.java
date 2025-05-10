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
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Random;

public class Main extends Application {
    Player player;
    ArrayList<Bullet> bullets = new ArrayList<>();
    ArrayList<Enemy> enemies = new ArrayList<>();
    long lastSpawn = 0;

    int score = 0;
    int lives = 3;

    @Override
    public void start(Stage stage) {
        Canvas canvas = new Canvas(800, 600);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        // 🎥 Video nền
        Media media = new Media(getClass().getResource("/assets/video/space_pixel_background.mp4").toExternalForm());
        MediaPlayer mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        mediaPlayer.setMute(true);
        MediaView mediaView = new MediaView(mediaPlayer);
        mediaView.setFitWidth(800);
        mediaView.setFitHeight(600);
        mediaView.setPreserveRatio(false);
        mediaPlayer.play();

        Pane root = new Pane();
        root.getChildren().addAll(mediaView, canvas);

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Space Shooter with Video Background");

        Assets.load();
        player = new Player(380, 500);

        // 🔧 Tăng tốc độ người chơi
        scene.setOnKeyPressed(e -> {
            switch (e.getCode()) {
                case LEFT -> player.move(-20);
                case RIGHT -> player.move(20);
                case SPACE -> bullets.add(player.shoot());
            }
        });

        // 🎮 Vòng lặp game
        new AnimationTimer() {
            public void handle(long now) {
                gc.clearRect(0, 0, 800, 600);

                // Player
                player.render(gc);

                // Bullets
                bullets.removeIf(b -> !b.update());
                bullets.forEach(b -> b.render(gc));

                // Enemy spawn
                if (now - lastSpawn > 2_000_000_000) {
                    enemies.add(new Enemy(new Random().nextInt(760), -30));
                    lastSpawn = now;
                }

                // Enemy update
                for (Enemy e : enemies) {
                    e.update();
                }

                // ✅ Va chạm bullet - enemy
                ArrayList<Enemy> toRemove = new ArrayList<>();
                for (Enemy e : enemies) {
                    for (Bullet b : bullets) {
                        if (e.collidesWith(b)) {
                            b.kill();
                            toRemove.add(e);
                            score += 100;
                        }
                    }
                }
                enemies.removeAll(toRemove);

                // Enemy render
                enemies.forEach(e -> e.render(gc));

                // ✅ HUD hiển thị
                gc.setFill(Color.WHITE);
                gc.fillText("Score: " + score, 10, 20);
                gc.fillText("Lives: " + lives, 10, 40);
            }
        }.start();

        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
