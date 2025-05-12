package com.example.spaceshooter;

import java.util.ArrayList;

import javafx.animation.AnimationTimer;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;

public class GameScene {
    static Player player;
    static ArrayList<Bullet> bullets = new ArrayList<>();
    static ArrayList<Missile> missiles = new ArrayList<>();
    static ArrayList<Enemy> enemies = new ArrayList<>();
    static ArrayList<EnemyBullet> enemyBullets = new ArrayList<>();
    static ArrayList<Explosion> explosions = new ArrayList<>();

    static int score = 0;
    static int highScore = HighScoreManager.getHighScore();
    static int lives = 5;
    static int wave = 1;
    static boolean waveSpawned = false;
    static boolean gameOver = false;
    static boolean paused = false;
    static boolean shooting = false;
    static int bossCount = 0;

    static long lastShotTime = 0;
    static final long SHOOT_COOLDOWN = 300_000_000;
    static long lastEnemyShot = 0;
    static final long ENEMY_SHOOT_INTERVAL = 1_500_000_000;
    private static AnimationTimer gameLoop;

    public static void startGame() {
        Canvas canvas = new Canvas(1280, 720);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        Media bgMedia = new Media(GameScene.class.getResource("/assets/video/space_pixel_background.mp4").toExternalForm());
        MediaPlayer mediaPlayer = new MediaPlayer(bgMedia);
        mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        mediaPlayer.setMute(true);
        mediaPlayer.play();
        MediaView mediaView = new MediaView(mediaPlayer);
        mediaView.setFitWidth(1280);
        mediaView.setFitHeight(720);
        mediaView.setPreserveRatio(false);

        Pane root = new Pane(mediaView, canvas);
        final PauseMenu[] pauseMenuRef = new PauseMenu[1];

        pauseMenuRef[0] = new PauseMenu(
                () -> {
                    paused = false;
                    pauseMenuRef[0].setVisible(false);
                },
                () -> {
                    paused = false;
                    pauseMenuRef[0].setVisible(false);
                    resetGameState();
                },
                () -> {
                    System.out.println("Settings pressed");
                },
                () -> {
                    MenuScene.showMenu(Main.mainStage);
                }
        );

        root.getChildren().add(pauseMenuRef[0]);
        pauseMenuRef[0].setVisible(false);
        Scene scene = new Scene(root);
        Assets.load();
        player = new Player(620, 640);
        resetGameState();

        scene.setOnKeyPressed(e -> {
            switch (e.getCode()) {
                case LEFT, A -> player.move(-20, 0);
                case RIGHT, D -> player.move(20, 0);
                case UP, W -> player.move(0, -20);
                case DOWN, S -> player.move(0, 20);
                case SPACE -> shooting = true;
                case M -> {
                    Missile m = player.fireMissile();
                    if (m != null) missiles.add(m);
                }
                case ESCAPE -> {
                    paused = true;
                    pauseMenuRef[0].setVisible(true);
                }
                case P -> {
                    paused = false;
                    pauseMenuRef[0].setVisible(false);
                }
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
                if (e.getButton() == MouseButton.PRIMARY) {
                    shooting = true;
                } else if (e.getButton() == MouseButton.MIDDLE) {
                    Missile m = player.fireMissile();
                    if (m != null) missiles.add(m);
                }
            });

            scene.setOnMouseReleased(e -> shooting = false);
        }

        if (gameLoop != null) gameLoop.stop();
        gameLoop = new AnimationTimer() {
            public void handle(long now) {
                gc.clearRect(0, 0, 1280, 720);

                if (enemies.isEmpty() && wave == 16) {
                    gameOver = true;
                    gc.setFill(Color.LIME);
                    gc.fillText("YOU WIN!", 600, 340);
                    return;
                }

                if (paused) return;

                if (gameOver) {
                    if (score > highScore) {
                        highScore = score;
                        HighScoreManager.saveHighScore(score);
                    }
                    gc.setFill(Color.RED);
                    gc.fillText("GAME OVER", 580, 320);
                    gc.setFill(Color.WHITE);
                    gc.fillText("Your Score: " + score, 580, 350);
                    gc.fillText("High Score: " + highScore, 580, 370);
                    return;
                }

                if (shooting && now - lastShotTime > SHOOT_COOLDOWN) {
                    bullets.add(player.shoot());
                    lastShotTime = now;
                }

                player.update();
                player.render(gc);

                missiles.removeIf(m -> !m.update(enemies, explosions));
                missiles.forEach(m -> m.render(gc));

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

                explosions.removeIf(e -> {
                    e.render(gc);
                    return e.isFinished();
                });

                drawHUD(gc);
            }
        };
        gameLoop.start();

        Main.mainStage.setScene(scene);
        Main.mainStage.setTitle("Space Shooter");
    }

    private static void drawHUD(GraphicsContext gc) {
        gc.setFill(Color.BLACK);
        gc.fillText("Score: " + score, 11, 21);
        gc.fillText("Lives: " + lives, 11, 41);
        gc.fillText("Wave: " + (wave - 1), 11, 61);
        gc.fillText("Missiles: " + player.getMissileCount(), 11, 81);

        gc.setFill(Color.WHITE);
        gc.fillText("Score: " + score, 10, 20);
        gc.fillText("Lives: " + lives, 10, 40);
        gc.fillText("Wave: " + (wave - 1), 10, 60);
        gc.fillText("Missiles: " + player.getMissileCount(), 10, 80);
    }

    private static void spawnWave(int waveNum) {
        if (waveNum == 16) {
            enemies.add(new SuperBossEnemy(600, 50, waveNum));
            return;
        } else if (waveNum % 5 == 0) {
            enemies.add(new BossEnemy(600, 50, waveNum));
            return;
        }

        switch (waveNum % 4) {
            case 1 -> {
                for (int i = 0; i < 10; i++)
                    enemies.add(new Enemy(80 + i * 110, 100, waveNum));
            }
            case 2 -> {
                for (int i = 0; i < 9; i++) {
                    double y = 100 + Math.abs(i - 4) * 20;
                    enemies.add(new Enemy(100 + i * 100, y, waveNum));
                }
            }
            case 3 -> {
                for (int row = 0; row < 2; row++) {
                    for (int col = 0; col < 6; col++) {
                        enemies.add(new Enemy(150 + col * 150, 100 + row * 70, waveNum));
                    }
                }
            }
            case 0 -> {
                for (int i = 0; i < 10; i++) {
                    enemies.add(new Enemy(80 + i * 110, 150 + (i % 2) * 40, waveNum));
                }
            }
        }
    }

    protected static void resetGameState() {
        bullets.clear();
        missiles.clear();
        enemies.clear();
        enemyBullets.clear();
        explosions.clear();
        score = 0;
        lives = 5;
        wave = 1;
        waveSpawned = false;
        gameOver = false;
        paused = false;
        shooting = false;
        player = new Player(620, 640);
    }
}
