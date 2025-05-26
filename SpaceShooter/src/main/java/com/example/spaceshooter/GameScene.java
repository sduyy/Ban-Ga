package com.example.spaceshooter;

import java.util.ArrayList;

import javafx.animation.AnimationTimer;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

/**
 * Lớp đại diện.
 */
public class GameScene {
    static Player player;
    static Player player2;
    static boolean shooting = false;
    static boolean shootingP2 = false;
    static boolean isTwoPlayerMode = false;
    static ArrayList<Bullet> bullets = new ArrayList<>();
    static ArrayList<Missile> missiles = new ArrayList<>();
    static ArrayList<Enemy> enemies = new ArrayList<>();
    static ArrayList<EnemyBullet> enemyBullets = new ArrayList<>();
    static ArrayList<Explosion> explosions = new ArrayList<>();
    static ArrayList<PowerUp> powerUps = new ArrayList<>();
    static int highScore = HighScoreManager.getHighScore();
    static int wave = 1;
    static boolean waveSpawned = false;
    static boolean gameOver = false;
    static boolean paused = false;
    static boolean superBossDefeated = false;
    static boolean autoPlay = false;
    static long lastShotTime = 0;
    static long lastEnemyShot = 0;
    private static AnimationTimer gameLoop;
    static long lastMissileTime = 0;
    static long lastShotTimeP2 = 0;
    static final long missileCooldown = 500_000_000L;

    private static MusicPlayer bgMusic;
    private static final SoundFX powerupSound = new SoundFX("powerup.wav");
    private static final SoundFX hitSound = new SoundFX("shipexplode.mp3");
    private static VBox gameOverMenu;

    /**
     * Khởi tạo và bắt đầu trò chơi.
     */
    public static void startGame() {
        bgMusic = new MusicPlayer("eclipse.mp3");
        bgMusic.play();

        Canvas canvas = new Canvas(1280, 720);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        String videoPath = GameScene.class.getResource("/assets/video/bgspedup4.mp4").toExternalForm();
        Media media = new Media(videoPath);
        MediaPlayer mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        mediaPlayer.setMute(true);
        mediaPlayer.setAutoPlay(false);

        MediaView mediaView = new MediaView(mediaPlayer);
        mediaView.setFitWidth(1280);
        mediaView.setFitHeight(720);
        mediaView.setPreserveRatio(false);

        Pane root = new Pane(mediaView, canvas);
        PauseMenu[] pauseMenu = new PauseMenu[1];

        CountdownOverlay countdownOverlay = new CountdownOverlay(() -> {
            paused = false;
        });
        countdownOverlay.setVisible(false);
        root.getChildren().add(countdownOverlay);

        pauseMenu[0] = new PauseMenu(
                () -> {
                    pauseMenu[0].setVisible(false);
                    paused = true;
                    countdownOverlay.start();
                },
                () -> {
                    resetGameState();
                    pauseMenu[0].setVisible(false);
                    paused = true;
                    countdownOverlay.start();
                    if (bgMusic != null) bgMusic.play();
                },
                () -> {},
                () -> {
                    paused = false;
                    mediaPlayer.dispose();
                    if (bgMusic != null) bgMusic.stop();
                    Main.mainStage.getScene().setRoot(new StackPane());
                    StartScreen.showMenu(Main.mainStage);
                }
        );
        root.getChildren().add(pauseMenu[0]);
        pauseMenu[0].setVisible(false);

        gameOverMenu = new VBox(20);
        gameOverMenu.setAlignment(Pos.CENTER);
        gameOverMenu.setTranslateX(1280 / 2 - 100); // căn giữa
        gameOverMenu.setTranslateY(550);
        gameOverMenu.setVisible(false);

        MenuItem restartItem = new MenuItem("RESTART");
        MenuItem exitItem = new MenuItem("EXIT");

        restartItem.setOnActivate(() -> {
            resetGameState();
            gameOverMenu.setVisible(false);
            paused = true;
            countdownOverlay.start();
            if (bgMusic != null) bgMusic.play();
        });

        exitItem.setOnActivate(() -> {
            paused = false;
            mediaPlayer.dispose();
            if (bgMusic != null) bgMusic.stop();
            Main.mainStage.getScene().setRoot(new StackPane());
            StartScreen.showMenu(Main.mainStage);
        });
        gameOverMenu.getChildren().addAll(restartItem, exitItem);
        root.getChildren().add(gameOverMenu);

        Scene scene = new Scene(root);
        Assets.load();
        resetGameState();



        scene.setOnKeyPressed(e -> {
            switch (e.getCode()) {
                case A -> player.move(-20, 0);
                case D -> player.move(20, 0);
                case W -> player.move(0, -20);
                case S -> player.move(0, 20);
                case SPACE -> shooting = true;
                case M -> {
                    Missile m = player.fireMissile();
                    if (m != null) missiles.add(m);
                }

                case LEFT -> { if (isTwoPlayerMode && player2 != null) player2.move(-20, 0); }
                case RIGHT -> { if (isTwoPlayerMode && player2 != null) player2.move(20, 0); }
                case UP -> { if (isTwoPlayerMode && player2 != null) player2.move(0, -20); }
                case DOWN -> { if (isTwoPlayerMode && player2 != null) player2.move(0, 20); }
                case ENTER -> shootingP2 = true;
                case SHIFT -> {
                    if (isTwoPlayerMode && player2 != null) {
                        Missile m = player2.fireMissile();
                        if (m != null) missiles.add(m);
                    }
                }

                case T -> {
                    if (!isTwoPlayerMode) autoPlay = !autoPlay;
                }

                case ESCAPE -> {
                    if (gameOver || wave > 17) {
                        paused = false;
                        mediaPlayer.dispose();
                        if (bgMusic != null) bgMusic.stop();
                        Main.mainStage.getScene().setRoot(new StackPane());
                        StartScreen.showMenu(Main.mainStage);
                    } else {
                        paused = true;
                        pauseMenu[0].setVisible(true);
                    }
                }

                case P -> {
                    if (!gameOver && wave <= 17) {
                        paused = false;
                        pauseMenu[0].setVisible(false);
                    }
                }
            }
        });

        scene.setOnKeyReleased(e -> {
            if (e.getCode() == javafx.scene.input.KeyCode.SPACE) shooting = false;
            if (e.getCode() == javafx.scene.input.KeyCode.ENTER) shootingP2 = false;
        });

        if (Main.useMouseControl && !isTwoPlayerMode) {
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

    /**
     * Bắt đầu vòng lặp trò chơi.
     * Vòng lặp này sẽ cập nhật trạng thái trò chơi
     * @param gc
     */
    private static void startGameLoop(GraphicsContext gc) {
        if (gameLoop != null) gameLoop.stop();

        gameLoop = new AnimationTimer() {
            public void handle(long now) {
                gc.clearRect(0, 0, 1280, 720);
                if (paused) return;

                gc.setTextAlign(TextAlignment.CENTER);

                if (wave > 17) {
                    if (player.getScore() > highScore) {
                        HighScoreManager.saveHighScore(player.getScore());
                        highScore = player.getScore();
                    }
                    if (bgMusic != null) bgMusic.stop();
                    
                    gc.setFont(StartScreen.TITLE_FONT);
                    gc.setFill(Color.LIME);
                    gc.fillText("YOU WIN!", 1280 / 2, 340);
                    gc.setFont(StartScreen.FONT);
                    gc.setFill(Color.WHITE);
                    gc.fillText("Your Score: " + player.getScore(), 1280 / 2, 400);
                    gc.fillText("High Score: " + highScore, 1280 / 2, 440);
                    
                    gameOverMenu.setVisible(true);
                    return;
                }

                /**
                 * Nếu trò chơi đã kết thúc (game over) thì hiển thị thông báo
                 */
                if (gameOver) {
                    if (player.getScore() > highScore) {
                        HighScoreManager.saveHighScore(player.getScore());
                        highScore = player.getScore();
                    }
                    if (bgMusic != null) bgMusic.stop();
                    gc.setFont(StartScreen.TITLE_FONT);
                    gc.setFill(Color.RED);
                    gc.fillText("GAME OVER", 1280 / 2, 340);
                    gc.setFont(StartScreen.FONT);
                    gc.setFill(Color.WHITE);
                    gc.fillText("Your Score: " + player.getScore(), 1280 / 2, 400);
                    gc.fillText("High Score: " + highScore, 1280 / 2, 440);
                    
                    gameOverMenu.setVisible(true);
                    return;
                }

                gc.setTextAlign(TextAlignment.LEFT);

                if (player.getLives() > 0 && shooting && now - lastShotTime > player.getShootCooldown() * 1_000_000L) {
                    for (Bullet b : player.shoot()) bullets.add(b);
                    lastShotTime = now;
                }

                if (isTwoPlayerMode && player2 != null && player2.getLives() > 0 && shootingP2 && now - lastShotTimeP2 > player2.getShootCooldown() * 1_000_000L) {
                    for (Bullet b : player2.shoot()) bullets.add(b);
                    lastShotTimeP2 = now;
                }

                if (player.getLives() > 0) {
                    player.update();
                    player.render(gc);
                }
                if (isTwoPlayerMode && player2 != null && player2.getLives() > 0) {
                    player2.update();
                    player2.render(gc);
                }

                if (autoPlay && !gameOver) {
                    if (player.getY() < 630) {
                        player.move(0, 5);
                    }

                    PowerUp nearestBuff = powerUps.stream()
                            .filter(p -> !p.isCollected())
                            .min((p1, p2) -> Double.compare(
                                    Math.abs(player.getX() - p1.getX()),
                                    Math.abs(player.getX() - p2.getX())))
                            .orElse(null);

                    boolean interruptedByBuff = false;
                    if (nearestBuff != null && Math.abs(nearestBuff.getY() - player.getY()) < 50) {
                        double px = player.getX();
                        double bx = nearestBuff.getX();
                        if (Math.abs(px - bx) > 10) {
                            player.move(px < bx ? 5 : -5, 0);
                            interruptedByBuff = true;
                        }
                    }

                    if (!interruptedByBuff) {
                        boolean dangerLeft = false;
                        boolean dangerRight = false;
                        double px = player.getX();
                        double py = player.getY();

                        for (EnemyBullet b : enemyBullets) {
                            double bx = b.getX();
                            double by = b.getY();
                            if (Math.abs(by - py) < 100) {
                                if (bx < px && Math.abs(bx - px) < 60) dangerLeft = true;
                                if (bx > px && Math.abs(bx - px) < 60) dangerRight = true;
                            }
                        }

                        if (dangerLeft && !dangerRight && px < 1230) {
                            player.move(20, 0);
                        } else if (dangerRight && !dangerLeft && px > 50) {
                            player.move(-20, 0);
                        } else if (dangerLeft && dangerRight) {
                            if (px > 640) player.move(-20, 0);
                            else player.move(20, 0);
                        }

                        Enemy closest = enemies.stream()
                                .min((e1, e2) -> Double.compare(
                                        Math.abs(player.getX() - e1.getX()),
                                        Math.abs(player.getX() - e2.getX())))
                                .orElse(null);

                        if (wave > 16 && (closest instanceof BossEnemy || closest instanceof SuperBossEnemy || closest instanceof MiniBossEnemy)) {

                            if (Math.abs(player.getX() - closest.getX()) < 20 && player.getMissileCount() > 0 && now - lastMissileTime > missileCooldown) {
                                Missile m = player.fireMissile();
                                if (m != null) {
                                    missiles.add(m);
                                    lastMissileTime = now;
                                }
                            }
                        }

                        if (closest != null) {
                            px = player.getX();
                            py = player.getY();
                            double ex = closest.getX();
                            if (Math.abs(px - ex) > 10) {
                                player.move(px < ex ? 5 : -5, 0);
                            }
                        }

                        if (now - lastShotTime > player.getShootCooldown() * 1_000_000L) {
                            for (Bullet b : player.shoot()) bullets.add(b);
                            lastShotTime = now;
                        }
                    }
                }

                /**
                 * Cập nhật và vẽ tên lửa (missiles) nếu có
                 */
                ArrayList<Missile> missilesToRemove = new ArrayList<>();
                for (Missile m : missiles) {
                    if (!m.update(enemies, explosions, powerUps)) {
                        missilesToRemove.add(m);
                        continue;
                    }

                    m.render(gc);

                    for (Enemy e : new ArrayList<>(enemies)) {
                        if (e.collidesWith(m)) {
                            m.explode(enemies, explosions, powerUps);
                            missilesToRemove.add(m);
                            break;
                        }
                    }
                }
                missiles.removeAll(missilesToRemove);

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

                    boolean hit = false;

                    if (player.getLives() > 0 && player.collidesWith(b)) {
                        player.setLives(player.getLives() - 1);
                        explosions.add(new Explosion(player.getX(), player.getY()));
                        player.markHit();
                        hit = true;
                    }

                    if (isTwoPlayerMode && player2 != null && player2.getLives() > 0 && player2.collidesWith(b)) {
                        player2.setLives(player2.getLives() - 1);
                        explosions.add(new Explosion(player2.getX(), player2.getY()));
                        player2.markHit();
                        hit = true;
                    }

                    if (hit) checkGameOver();
                    return hit;
                });

                /**
                 * Kiểm tra va chạm giữa đạn của người chơi và địch,
                 * cũng như xử lý va chạm với người chơi.
                 */
                ArrayList<Enemy> toRemove = new ArrayList<>();
                for (Enemy e : enemies) {
                    for (Bullet b : bullets) {
                        if (e.collidesWith(b)) {
                            b.kill();
                            if (e.takeDamage(b.getDamage())) {
                                toRemove.add(e);
                                explosions.add(new Explosion(e.getX(), e.getY()));
                                if (e instanceof SuperBossEnemy) superBossDefeated = true;

                                int earned = (e instanceof SuperBossEnemy) ? 1000 :
                                        (e instanceof BossEnemy ? 500 : 100);

                                Player owner = b.getOwner();
                                owner.addScore(earned);
                                dropPowerUps(e);
                            }
                        }
                    }
                    boolean enemyRemoved = false;
                    if (player.getLives() > 0 && player.collidesWith(e)) {
                        player.setLives(player.getLives() - 1);
                        explosions.add(new Explosion(player.getX(), player.getY()));
                        player.markHit();
                        if (e instanceof BossEnemy || e instanceof SuperBossEnemy || e instanceof MiniBossEnemy) {
                            if (e.takeDamage(1)) {
                                enemyRemoved = true;
                                toRemove.add(e);
                                explosions.add(new Explosion(e.getX(), e.getY()));
                                if (e instanceof SuperBossEnemy) superBossDefeated = true;
                                player.addScore((e instanceof SuperBossEnemy) ? 1000 : (e instanceof BossEnemy ? 500 : 100));
                                dropPowerUps(e);
                            }
                        } else {
                            enemyRemoved = true;
                            toRemove.add(e);
                            player.addScore(100);
                        }
                        checkGameOver();
                    }
                    if (isTwoPlayerMode && !enemyRemoved && player2 != null && player2.getLives() > 0 && player2.collidesWith(e)) {
                        player2.setLives(player2.getLives() - 1);
                        explosions.add(new Explosion(player2.getX(), player2.getY()));
                        player2.markHit();
                        if (e instanceof BossEnemy || e instanceof SuperBossEnemy || e instanceof MiniBossEnemy) {
                            if (e.takeDamage(1)) {
                                enemyRemoved = true;
                                toRemove.add(e);
                                explosions.add(new Explosion(e.getX(), e.getY()));
                                if (e instanceof SuperBossEnemy) superBossDefeated = true;
                                player2.addScore((e instanceof SuperBossEnemy) ? 1000 : (e instanceof BossEnemy ? 500 : 100));
                                dropPowerUps(e);
                            }
                        } else {
                            enemyRemoved = true;
                            toRemove.add(e);
                            player2.addScore(100);
                        }
                        checkGameOver();
                    }
                }
                enemies.removeAll(toRemove);

                explosions.removeIf(ex -> {
                    ex.render(gc);
                    return ex.isFinished();
                });

                powerUps.removeIf(p -> {
                    p.update();
                    if (p.isExpired()) return true;
                    p.render(gc);

                    if (!p.isCollected()) {
                        if (player.collidesWith(p)) {
                            powerupSound.play();
                            p.collect();
                            applyPowerUpToPlayer(player, p.getType());
                            player.addScore(30);
                            return true;
                        } else if (isTwoPlayerMode && player2 != null && player2.collidesWith(p)) {
                            powerupSound.play();
                            p.collect();
                            applyPowerUpToPlayer(player2, p.getType());
                            player2.addScore(30);
                            return true;
                        }
                    }

                    return false;
                });

                drawHUD(gc);
            }
        };
        gameLoop.start();
    }

    /**
     * Vẽ giao diện người dùng (HUD) lên
     * @param gc
     */
    private static void drawHUD(GraphicsContext gc) {
        gc.setFont(Font.font(StartScreen.FONT.getFamily(), 13));
        gc.setFill(Color.BLACK);

        if (!isTwoPlayerMode) {
            gc.fillText("Score: " + player.getScore(), 11, 18);
            gc.fillText("Lives: " + player.getLives(), 11, 34);
            gc.fillText("Missiles: " + player.getMissileCount(), 11, 50);
            gc.fillText("AI: " + (autoPlay ? "ON" : "OFF"), 11, 66);

            gc.setTextAlign(TextAlignment.CENTER);
            gc.fillText("Wave: " + (wave - 1), 1280 / 2, 18);
            gc.setTextAlign(TextAlignment.LEFT);
        } else {

            // 2 players HUD
            gc.fillText("P1 Score: " + player.getScore(), 11, 18);
            gc.fillText("P1 Lives: " + player.getLives(), 11, 34);
            gc.fillText("P1 Missiles: " + player.getMissileCount(), 11, 50);

            gc.setTextAlign(TextAlignment.CENTER);
            gc.fillText("Wave: " + (wave - 1), 1280 / 2, 18);
            gc.setTextAlign(TextAlignment.LEFT);

            gc.fillText("P2 Score: " + player2.getScore(), 1050, 18);
            gc.fillText("P2 Lives: " + player2.getLives(), 1050, 34);
            gc.fillText("P2 Missiles: " + player2.getMissileCount(), 1050, 50);
        }

        gc.setFill(Color.WHITE);

        if (!isTwoPlayerMode) {
            gc.fillText("Score: " + player.getScore(), 10, 17);
            gc.fillText("Lives: " + player.getLives(), 10, 33);
            gc.fillText("Missiles: " + player.getMissileCount(), 10, 49);
            gc.fillText("AI: " + (autoPlay ? "ON" : "OFF"), 10, 65);

            gc.setTextAlign(TextAlignment.CENTER);
            gc.fillText("Wave: " + (wave - 1), 1280 / 2, 17);
            gc.setTextAlign(TextAlignment.LEFT);
        } else {
            gc.fillText("P1 Score: " + player.getScore(), 10, 17);
            gc.fillText("P1 Lives: " + player.getLives(), 10, 33);
            gc.fillText("P1 Missiles: " + player.getMissileCount(), 10, 49);

            gc.setTextAlign(TextAlignment.CENTER);
            gc.fillText("Wave: " + (wave - 1), 1280 / 2, 17);
            gc.setTextAlign(TextAlignment.LEFT);

            gc.fillText("P2 Score: " + player2.getScore(), 1049, 17);
            gc.fillText("P2 Lives: " + player2.getLives(), 1049, 33);
            gc.fillText("P2 Missiles: " + player2.getMissileCount(), 1049, 49);
        }
    }

    /**
     *  Áp dụng hiệu ứng tăng cường (power-up) cho người chơi.
     *  Tăng cường có thể là tăng máu, tên lửa, đạn, sát thương hoặc tốc độ bắn.
     * @param type
     */
    private static void applyPowerUp(PowerUpType type) {
        switch (type) {
            case HEALTH -> player.setLives(Math.min(player.getLives() + 1, 5));
            case ROCKET -> player.addMissile(1);
            case AMMO -> player.upgradeShootLevel();
            case DAMAGE -> player.upgradeDamageLevel();
            case ENERGY -> player.upgradeFireRateLevel();
        }
    }

    /**
     * Áp dụng hiệu ứng tăng cường (power-up) cho người chơi.
     * Nếu là chế độ hai người chơi, sẽ áp dụng cho người chơi thứ hai nếu có.
     * @param p
     * @param type
     */
    private static void applyPowerUpToPlayer(Player p, PowerUpType type) {
        if (p == player) {
            applyPowerUp(type);
        } else if (p == player2) {
            switch (type) {
                case HEALTH -> player2.setLives(Math.min(player2.getLives() + 1, 5));
                case ROCKET -> player2.addMissile(1);
                case AMMO -> player2.upgradeShootLevel();
                case DAMAGE -> player2.upgradeDamageLevel();
                case ENERGY -> player2.upgradeFireRateLevel();
            }
        }
    }

    /**
     * Thả các hiệu ứng tăng cường (power-ups) khi một kẻ địch bị tiêu diệt.
     * Nếu là trùm (BossEnemy hoặc SuperBossEnemy), sẽ thả nhiều loại power-up.
     * @param e
     */
    public static void dropPowerUps(Enemy e) {
        double x = e.getX(), y = e.getY();
        if (e instanceof BossEnemy || e instanceof SuperBossEnemy) {
            powerUps.add(new PowerUp(x, y, PowerUpType.ROCKET));
            if ((player.getLives() < 5) || (player2 != null && player2.getLives() < 5))
                powerUps.add(new PowerUp(x + 10, y, PowerUpType.HEALTH));
            if ((player.getFireRateLevel() < 3) || (player2 != null && player2.getFireRateLevel() < 3))
                powerUps.add(new PowerUp(x + 20, y, PowerUpType.ENERGY));
            if ((player.getShootLevel() < 3) || (player2 != null && player2.getShootLevel() < 3))
                powerUps.add(new PowerUp(x + 30, y, PowerUpType.AMMO));
            if ((player.getDamageLevel() < 3) || (player2 != null && player2.getDamageLevel() < 3))
                powerUps.add(new PowerUp(x + 40, y, PowerUpType.DAMAGE));
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

    /**
     * Sinh ra một làn sóng kẻ địch mới dựa trên số lượng sóng hiện tại.
     * Nếu làn sóng là 16, sẽ sinh ra một trùm siêu mạnh (SuperBossEnemy).
     * @param waveNum
     */
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

    /**
     * Kiểm tra xem trò chơi đã kết thúc hay chưa.
     * Nếu là chế độ hai người chơi, trò chơi kết thúc khi cả hai người chơi đều hết mạng.
     * Nếu là chế độ một người chơi, trò chơi kết thúc khi người chơi hết mạng.
     */
    private static void checkGameOver() {
        if (!isTwoPlayerMode) {
            if (player.getLives() <= 0) gameOver = true;
        } else {
            if (player.getLives() <= 0 && player2.getLives() <= 0) gameOver = true;
        }
    }

    /**
     * Đặt lại trạng thái trò chơi về ban đầu.
     * Xóa tất cả các đối tượng như đạn, kẻ địch, hiệu ứng nổ, vật phẩm tăng cường, v.v.
     */
    protected static void resetGameState() {
        bullets.clear();
        missiles.clear();
        enemies.clear();
        enemyBullets.clear();
        explosions.clear();
        powerUps.clear();
        wave = 1;
        waveSpawned = false;
        gameOver = false;
        paused = false;
        shooting = false;
        shootingP2 = false;
        superBossDefeated = false;
        autoPlay = false;

        player = new Player(420, 640, Assets.player);
        player.setLives(5);

        if (isTwoPlayerMode) {
            player2 = new Player(820, 640, Assets.player2);
            player2.setLives(5);
            autoPlay = false;
        } else {
            player2 = null;
        }
    }
}
