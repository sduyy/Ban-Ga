package com.example.spaceshooter;

import java.util.ArrayList;

import javafx.animation.AnimationTimer;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import com.example.spaceshooter.Assets;
import com.example.spaceshooter.SuperBossEnemy;


public class GameScene {
    static Player player;
    static ArrayList<Bullet> bullets = new ArrayList<>();
    static ArrayList<Missile> missiles = new ArrayList<>();
    static ArrayList<Enemy> enemies = new ArrayList<>();
    static ArrayList<EnemyBullet> enemyBullets = new ArrayList<>();
    static ArrayList<Explosion> explosions = new ArrayList<>();
    static ArrayList<PowerUp> powerUps = new ArrayList<>();

    static int score = 0;
    static int highScore = HighScoreManager.getHighScore();
    static int lives = 5;
    static int wave = 1;
    static boolean waveSpawned = false;
    static boolean gameOver = false;
    static boolean paused = false;
    static boolean shooting = false;
    static boolean superBossDefeated = false;

    static long lastShotTime = 0;
    static long lastEnemyShot = 0;
    private static AnimationTimer gameLoop;

    public static void startGame() {
        Canvas canvas = new Canvas(1280, 720);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        if (Main.sharedMediaPlayer == null) {
            String videoPath = GameScene.class.getResource("/assets/video/space_pixel_background.mp4").toExternalForm();
            Main.sharedMediaPlayer = new MediaPlayer(new Media(videoPath));
            Main.sharedMediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            Main.sharedMediaPlayer.setMute(true);
            Main.sharedMediaPlayer.setAutoPlay(false);
        }
        MediaPlayer mediaPlayer = Main.sharedMediaPlayer;
        MediaView mediaView = new MediaView(mediaPlayer);
        mediaView.setFitWidth(1280);
        mediaView.setFitHeight(720);
        mediaView.setPreserveRatio(false);

        Pane root = new Pane(mediaView, canvas);
        PauseMenu[] pauseMenu = new PauseMenu[1];
        pauseMenu[0] = new PauseMenu(
            () -> { paused = false;pauseMenu[0].setVisible(false);},
            () -> { paused = false; resetGameState(); pauseMenu[0].setVisible(false); },
            () -> {},
            () -> {
                paused = false;
                Main.mainStage.getScene().setRoot(new StackPane());
                StartScreen.showMenu(Main.mainStage);
            }
        );
        root.getChildren().add(pauseMenu[0]);
        pauseMenu[0].setVisible(false);

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
                    pauseMenu[0].setVisible(true);
                }
                case P -> {
                    paused = false;
                    pauseMenu[0].setVisible(false);
                }
            }
        });

        scene.setOnKeyReleased(e -> {
            if (e.getCode() == javafx.scene.input.KeyCode.SPACE) shooting = false;
        });

        if (Main.useMouseControl) {
            scene.setOnMouseMoved(e -> player.moveTo(e.getX() - 20, e.getY() - 20));
            scene.setOnMousePressed(e -> {
                if (e.getButton() == MouseButton.PRIMARY) shooting = true;
                else if (e.getButton() == MouseButton.MIDDLE) {
                    Missile m = player.fireMissile();
                    if (m != null) missiles.add(m);
                }
            });
            scene.setOnMouseReleased(e -> shooting = false);
        }

        mediaPlayer.setOnReady(() -> {
            mediaPlayer.play();
            Main.mainStage.setScene(scene);
            startGameLoop(gc);
        });

        mediaPlayer.setOnError(() -> {
            System.err.println("Video error: " + mediaPlayer.getError());
            Main.mainStage.setScene(scene);
            startGameLoop(gc);
        });
    }

    private static void startGameLoop(GraphicsContext gc) {
        if (gameLoop != null) gameLoop.stop();

        gameLoop = new AnimationTimer() {
            public void handle(long now) {
                gc.clearRect(0, 0, 1280, 720);
                if (paused) return;

                if (wave > 17) {
                    if (score > highScore) {
                        HighScoreManager.saveHighScore(score);
                        highScore = score;
                    }
                    gc.setFill(Color.LIME);
                    gc.fillText("YOU WIN!", 580, 340);
                    gc.setFill(Color.WHITE);
                    gc.fillText("Your Score: " + score, 580, 370);
                    gc.fillText("High Score: " + highScore, 580, 390);
                    return;
                }

                if (gameOver) {
                    if (score > highScore) {
                        HighScoreManager.saveHighScore(score);
                        highScore = score;
                    }
                    gc.setFill(Color.RED);
                    gc.fillText("GAME OVER", 580, 340);
                    gc.setFill(Color.WHITE);
                    gc.fillText("Your Score: " + score, 580, 370);
                    gc.fillText("High Score: " + highScore, 580, 390);
                    return;
                }

                if (shooting && now - lastShotTime > player.getShootCooldown() * 1_000_000L) {
                    for (Bullet b : player.shoot()) bullets.add(b);
                    lastShotTime = now;
                }

                player.update();
                player.render(gc);

                missiles.removeIf(m -> !m.update(enemies, explosions, powerUps));
                missiles.forEach(m -> m.render(gc));

                bullets.removeIf(b -> !b.update());
                bullets.forEach(b -> b.render(gc));

                if (!waveSpawned && enemies.isEmpty()) {
                    spawnWave(wave++);
                    waveSpawned = true;
                }

                if (!enemies.isEmpty()) {
                    new ArrayList<>(enemies).forEach(Enemy::update);
                    new ArrayList<>(enemies).forEach(e -> e.render(gc));
                } else waveSpawned = false;

                if (now - lastEnemyShot > 1_500_000_000L) {
                    for (Enemy e : enemies) {
                        if (Math.random() < 0.2) {
                            EnemyBullet b = e.shoot();
                            if (b != null) {
                                enemyBullets.add(b);
                            }
                        }
                    }
                    lastEnemyShot = now;
                }

                enemyBullets.removeIf(b -> {
                    if (b == null) return true;

                    if (!b.update()) return true;
                    b.render(gc);
                    if (player.collidesWith(b)) {
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
                            if (e.takeDamage(b.getDamage())) {
                                toRemove.add(e);
                                explosions.add(new Explosion(e.getX(), e.getY()));
                                if (e instanceof SuperBossEnemy) superBossDefeated = true;
                                score += (e instanceof SuperBossEnemy) ? 1000 : (e instanceof BossEnemy ? 500 : 100);
                                dropPowerUps(e);
                            }
                        }
                    }
                    if (player.collidesWith(e)) {
                        toRemove.add(e);
                        lives--;
                        explosions.add(new Explosion(player.getX(), player.getY()));
                        player.markHit();
                        if (lives <= 0) gameOver = true;
                    }
                }

                enemies.removeAll(toRemove);

                explosions.removeIf(e -> {
                    e.render(gc);
                    return e.isFinished();
                });

                powerUps.removeIf(p -> {
                    p.update();
                    if (p.isExpired()) return true;
                    p.render(gc);
                    if (!p.isCollected() && p.collidesWith(player)) {
                        p.collect();
                        applyPowerUp(p.getType());
                        return true;
                    }
                    return false;
                });

                drawHUD(gc);
            }
        };
        gameLoop.start();
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

    private static void applyPowerUp(PowerUpType type) {
        switch (type) {
            case HEALTH -> lives = Math.min(lives + 1, 5);
            case ROCKET -> player.addMissile(1); // ✅ +1 tên lửa
            case AMMO -> player.upgradeShootLevel();
            case DAMAGE -> player.upgradeDamageLevel();
            case ENERGY -> player.upgradeFireRateLevel();
        }
    }

    public static void dropPowerUps(Enemy e) {
        double x = e.getX(), y = e.getY();
        if (e instanceof BossEnemy || e instanceof SuperBossEnemy) {
            powerUps.add(new PowerUp(x, y, PowerUpType.ROCKET));
            if (lives < 5) powerUps.add(new PowerUp(x + 10, y, PowerUpType.HEALTH));
            if (player.getFireRateLevel() < 3) powerUps.add(new PowerUp(x + 20, y, PowerUpType.ENERGY));
            if (player.getShootLevel() < 3) powerUps.add(new PowerUp(x + 30, y, PowerUpType.AMMO));
            if (player.getDamageLevel() < 3) powerUps.add(new PowerUp(x + 40, y, PowerUpType.DAMAGE));
        } else {
            if (Math.random() < 0.25) {
                PowerUpType type = switch ((int) (Math.random() * 4)) {
                    case 0 -> PowerUpType.HEALTH;
                    case 1 -> PowerUpType.ENERGY;
                    case 2 -> PowerUpType.AMMO;
                    default -> PowerUpType.DAMAGE;
                };
                powerUps.add(new PowerUp(x, y, type));
            }
        }
    }

    private static void spawnWave(int waveNum) {
        if (waveNum == 16) {
            enemies.add(new SuperBossEnemy(590, 50, waveNum));
            return;
        } else if (waveNum % 5 == 0) {
            enemies.add(new BossEnemy(600, 50, waveNum));
            return;
        }

        int count = switch (waveNum % 4) {
            case 1, 0 -> 10;
            case 2 -> 9;
            default -> 12;
        };

        double spacing = 100;
        double totalWidth = (count - 1) * spacing;
        double startX = (1280 - totalWidth - 40) / 2;

        for (int i = 0; i < count; i++) {
            double x = startX + i * spacing;
            double y = 100 + (i % 2) * 40;
            enemies.add(new Enemy(x, y, waveNum));
        }
    }
    protected static void resetGameState() {
        bullets.clear();
        missiles.clear();
        enemies.clear();
        enemyBullets.clear();
        explosions.clear();
        powerUps.clear();
        score = 0;
        lives = 5;
        wave = 1;
        waveSpawned = false;
        gameOver = false;
        paused = false;
        shooting = false;
        superBossDefeated = false;
        player = new Player(620, 640);
    }
}
