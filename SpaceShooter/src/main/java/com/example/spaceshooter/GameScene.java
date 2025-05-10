package com.example.spaceshooter;

import javafx.animation.AnimationTimer;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;

import java.util.ArrayList;

public class GameScene {
    static Player player;
    static ArrayList<Bullet> bullets = new ArrayList<>();
    static ArrayList<Enemy> enemies = new ArrayList<>();
    static ArrayList<EnemyBullet> enemyBullets = new ArrayList<>();
    static ArrayList<PowerUp> powerUps = new ArrayList<>();
    static ArrayList<Explosion> explosions = new ArrayList<>();

    static int score = 0;
    static int highScore = HighScoreManager.getHighScore();
    static int lives = 5;
    static int wave = 1;
    static boolean waveSpawned = false;
    static boolean gameOver = false;
    static boolean paused = false;
    static boolean shooting = false;

    static long lastShotTime = 0;
    static final long SHOOT_COOLDOWN = 300_000_000;
    static long lastEnemyShot = 0;
    static final long ENEMY_SHOOT_INTERVAL = 1_500_000_000;

    public static void startGame() {
        Canvas canvas = new Canvas(800, 600);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        Media bgMedia = new Media(GameScene.class.getResource("/assets/video/space_pixel_background.mp4").toExternalForm());
        MediaPlayer mediaPlayer = new MediaPlayer(bgMedia);
        mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        mediaPlayer.setMute(true);
        mediaPlayer.play();
        MediaView mediaView = new MediaView(mediaPlayer);
        mediaView.setFitWidth(800);
        mediaView.setFitHeight(600);
        mediaView.setPreserveRatio(false);

        Pane root = new Pane(mediaView, canvas);
        Scene scene = new Scene(root);

        Assets.load();
        player = new Player(380, 500);
        resetGameState();

        scene.setOnKeyPressed(e -> {
            switch (e.getCode()) {
                case LEFT, A -> player.move(-20, 0);
                case RIGHT, D -> player.move(20, 0);
                case UP, W -> player.move(0, -20);
                case DOWN, S -> player.move(0, 20);
                case SPACE -> shooting = true;
                case ESCAPE -> paused = true;
                case P -> paused = false;
            }
        });

        scene.setOnKeyReleased(e -> {
            if (e.getCode() == javafx.scene.input.KeyCode.SPACE) {
                shooting = false;
            }
        });

        if (Main.useMouseControl) {
            scene.setOnMouseMoved((MouseEvent e) -> {
                player.moveTo(e.getX() - 20, e.getY() - 20);
            });

            scene.setOnMousePressed(e -> {
                if (e.isPrimaryButtonDown()) {
                    shooting = true;
                }
            });

            scene.setOnMouseReleased(e -> shooting = false);
        }

        new AnimationTimer() {
            public void handle(long now) {
                gc.clearRect(0, 0, 800, 600);

                if (paused) {
                    gc.setFill(Color.YELLOW);
                    gc.fillText("PAUSED - Press P to resume", 300, 300);
                    return;
                }

                if (gameOver) {
                    if (score > highScore) {
                        highScore = score;
                        HighScoreManager.saveHighScore(score);
                    }
                    gc.setFill(Color.RED);
                    gc.fillText("GAME OVER", 330, 280);
                    gc.setFill(Color.WHITE);
                    gc.fillText("Your Score: " + score, 330, 310);
                    gc.fillText("High Score: " + highScore, 330, 330);
                    return;
                }

                if (shooting && now - lastShotTime > SHOOT_COOLDOWN) {
                    bullets.add(player.shoot());
                    lastShotTime = now;
                }

                player.update();
                player.render(gc);

                bullets.removeIf(b -> !b.update());
                bullets.forEach(b -> b.render(gc));

                if (!waveSpawned && enemies.isEmpty()) {
                    spawnWave(wave++);
                    waveSpawned = true;
                }

                if (!enemies.isEmpty()) {
                    enemies.forEach(Enemy::update);
                    enemies.forEach(e -> e.render(gc));
                } else {
                    waveSpawned = false;
                }

                if (now - lastEnemyShot > ENEMY_SHOOT_INTERVAL) {
                    for (Enemy e : enemies) {
                        if (Math.random() < 0.2) {
                            enemyBullets.add(e.shoot());
                        }
                    }
                    lastEnemyShot = now;
                }

                enemyBullets.removeIf(b -> {
                    if (!b.update()) return true;
                    b.render(gc);
                    if (b.collidesWith(player)) {
                        lives--;
                        explosions.add(new Explosion(player.getX(), player.getY()));
                        player.markHit();
                        if (lives <= 0) gameOver = true;
                        return true;
                    }
                    return false;
                });

                ArrayList<Enemy> toRemove = new ArrayList<>();
                for (Enemy e : enemies) {
                    for (Bullet b : bullets) {
                        if (e.collidesWith(b)) {
                            b.kill();
                            if (e.takeHit()) {
                                toRemove.add(e);
                                score += 100;
                                explosions.add(new Explosion(e.getX(), e.getY()));
                                if (Math.random() < 0.2)
                                    powerUps.add(new PowerUp(e.getX(), e.getY()));
                            }
                        }
                    }

                    if (player.collidesWith(e)) {
                        toRemove.add(e);
                        lives--;
                        player.markHit();
                        explosions.add(new Explosion(player.getX(), player.getY()));
                        if (lives <= 0) gameOver = true;
                    }
                }
                enemies.removeAll(toRemove);

                powerUps.removeIf(p -> {
                    p.update();
                    p.render(gc);
                    if (p.collidesWith(player)) {
                        p.collect();
                        if (lives < 5) lives++;
                        return true;
                    }
                    return p.isCollected() || p.isOffScreen();
                });

                explosions.removeIf(e -> {
                    e.render(gc);
                    return e.isFinished();
                });

                drawHUD(gc);
            }
        }.start();

        Main.mainStage.setScene(scene);
        Main.mainStage.setTitle("Space Shooter");
    }

    private static void drawHUD(GraphicsContext gc) {
        gc.setFill(Color.BLACK);
        gc.fillText("Score: " + score, 11, 21);
        gc.fillText("Lives: " + lives, 11, 41);
        gc.fillText("Wave: " + (wave - 1), 11, 61);

        gc.setFill(Color.WHITE);
        gc.fillText("Score: " + score, 10, 20);
        gc.fillText("Lives: " + lives, 10, 40);
        gc.fillText("Wave: " + (wave - 1), 10, 60);
    }

    private static void spawnWave(int waveNum) {
        if (waveNum % 4 == 0) {
            enemies.add(new BossEnemy(350, 50, waveNum));
            return;
        }

        switch (waveNum % 4) {
            case 1 -> {
                for (int i = 0; i < 10; i++) {
                    enemies.add(new Enemy(50 + i * 70, 100, waveNum));
                }
            }
            case 2 -> {
                for (int i = 0; i < 9; i++) {
                    double y = 100 + Math.abs(i - 4) * 20;
                    enemies.add(new Enemy(80 + i * 60, y, waveNum));
                }
            }
            case 3 -> {
                for (int row = 0; row < 2; row++) {
                    for (int col = 0; col < 5; col++) {
                        enemies.add(new Enemy(100 + col * 100, 100 + row * 60, waveNum));
                    }
                }
            }
        }
    }

    private static void resetGameState() {
        bullets.clear();
        enemies.clear();
        enemyBullets.clear();
        powerUps.clear();
        explosions.clear();
        score = 0;
        lives = 5;
        wave = 1;
        waveSpawned = false;
        gameOver = false;
        paused = false;
        shooting = false;
    }
}
