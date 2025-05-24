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
import javafx.scene.text.TextAlignment;

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

    // (Đã bỏ biến static score, dùng score trong Player)
    static int highScore = HighScoreManager.getHighScore();
    // (Đã bỏ biến static lives, dùng lives riêng cho player)
    // (Đã bỏ biến static lives2, dùng lives riêng cho player2)
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

    public static void startGame() {
        bgMusic = new MusicPlayer("eclipse.mp3");
        bgMusic.play();

        Canvas canvas = new Canvas(1280, 720);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        String videoPath = GameScene.class.getResource("/assets/video/space_pixel_background.mp4").toExternalForm();
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
                },
                () -> {},
                () -> {
                    paused = false;
                    mediaPlayer.dispose();
                    Main.mainStage.getScene().setRoot(new StackPane());
                    StartScreen.showMenu(Main.mainStage);
                }
        );
        root.getChildren().add(pauseMenu[0]);
        pauseMenu[0].setVisible(false);

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
                    gc.setFont(StartScreen.TITLE_FONT);
                    gc.setFill(Color.LIME);
                    gc.fillText("YOU WIN!", 1280 / 2, 340);
                    gc.setFont(StartScreen.FONT);
                    gc.setFill(Color.WHITE);
                    gc.fillText("Your Score: " + player.getScore(), 1280 / 2, 400);
                    gc.fillText("High Score: " + highScore, 1280 / 2, 440);
                    gc.setFill(Color.LIGHTGRAY);
                    gc.fillText("Press ESC to return to Main Menu", 1280 / 2, 500);
                    return;
                }

                if (gameOver) {
                    if (player.getScore() > highScore) {
                        HighScoreManager.saveHighScore(player.getScore());
                        highScore = player.getScore();
                    }
                    bgMusic.stop();
                    gc.setFont(StartScreen.TITLE_FONT);
                    gc.setFill(Color.RED);
                    gc.fillText("GAME OVER", 1280 / 2, 340);
                    gc.setFont(StartScreen.FONT);
                    gc.setFill(Color.WHITE);
                    gc.fillText("Your Score: " + player.getScore(), 1280 / 2, 400);
                    gc.fillText("High Score: " + highScore, 1280 / 2, 440);
                    gc.setFill(Color.LIGHTGRAY);
                    gc.fillText("Press ESC to return to Main Menu", 1280 / 2, 500);
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
                            break; // Dừng kiểm tra sau khi tên lửa nổ
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

                    if (player.getLives() > 0 && player.collidesWith(b)) { // Sửa: chỉ xử lý khi còn mạng
                        player.setLives(player.getLives() - 1); // Sửa: trừ mạng player1
                        explosions.add(new Explosion(player.getX(), player.getY()));
                        player.markHit();
                        hit = true;
                    }

                    if (isTwoPlayerMode && player2 != null && player2.getLives() > 0 && player2.collidesWith(b)) { // Sửa: chỉ xử lý khi còn mạng
                        player2.setLives(player2.getLives() - 1); // Sửa: trừ mạng player2
                        explosions.add(new Explosion(player2.getX(), player2.getY()));
                        player2.markHit();
                        hit = true;
                    }

                    if (hit) checkGameOver();
                    return hit;
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

                                int earned = (e instanceof SuperBossEnemy) ? 1000 :
                                        (e instanceof BossEnemy ? 500 : 100);

                                Player owner = b.getOwner();
                                owner.addScore(earned); // Sửa: cộng điểm cho người bắn
                                dropPowerUps(e);
                            }
                        }
                    }
                    boolean enemyRemoved = false; // Sửa: theo dõi việc enemy đã bị loại bỏ
                    if (player.getLives() > 0 && player.collidesWith(e)) {
                        // Player 1 va chạm với địch
                        player.setLives(player.getLives() - 1); // Sửa: trừ mạng player1
                        explosions.add(new Explosion(player.getX(), player.getY()));
                        player.markHit();
                        // Xử lý địch khi va chạm với player1
                        if (e instanceof BossEnemy || e instanceof SuperBossEnemy || e instanceof MiniBossEnemy) {
                            if (e.takeDamage(1)) {
                                enemyRemoved = true;
                                toRemove.add(e);
                                explosions.add(new Explosion(e.getX(), e.getY()));
                                if (e instanceof SuperBossEnemy) superBossDefeated = true;
                                player.addScore((e instanceof SuperBossEnemy) ? 1000 : (e instanceof BossEnemy ? 500 : 100)); // Sửa: cộng điểm cho player1
                                dropPowerUps(e);
                            }
                        } else {
                            enemyRemoved = true;
                            toRemove.add(e);
                            player.addScore(100); // Sửa: player1 tiêu diệt địch thường +100 điểm
                        }
                        checkGameOver();
                    }
                    if (isTwoPlayerMode && !enemyRemoved && player2 != null && player2.getLives() > 0 && player2.collidesWith(e)) {
                        // Player 2 va chạm với địch (chưa bị loại bởi player1)
                        player2.setLives(player2.getLives() - 1); // Sửa: trừ mạng player2
                        explosions.add(new Explosion(player2.getX(), player2.getY()));
                        player2.markHit();
                        // Xử lý địch khi va chạm với player2
                        if (e instanceof BossEnemy || e instanceof SuperBossEnemy || e instanceof MiniBossEnemy) {
                            if (e.takeDamage(1)) {
                                enemyRemoved = true;
                                toRemove.add(e);
                                explosions.add(new Explosion(e.getX(), e.getY()));
                                if (e instanceof SuperBossEnemy) superBossDefeated = true;
                                player2.addScore((e instanceof SuperBossEnemy) ? 1000 : (e instanceof BossEnemy ? 500 : 100)); // Sửa: cộng điểm cho player2
                                dropPowerUps(e);
                            }
                        } else {
                            enemyRemoved = true;
                            toRemove.add(e);
                            player2.addScore(100); // Sửa: player2 tiêu diệt địch thường +100 điểm
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
                            p.collect();
                            applyPowerUpToPlayer(player, p.getType());
                            player.addScore(30); // Sửa: cộng điểm cho player1 nhặt vật phẩm
                            return true;
                        } else if (isTwoPlayerMode && player2 != null && player2.collidesWith(p)) {
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

    private static void drawHUD(GraphicsContext gc) {
        gc.setFill(Color.BLACK);

        if (!isTwoPlayerMode) {
            // HUD chế độ 1 người
            gc.fillText("Score: " + player.getScore(), 11, 21);
            gc.fillText("Lives: " + player.getLives(), 11, 41);
            gc.fillText("Wave: " + (wave - 1), 11, 61);
            gc.fillText("Missiles: " + player.getMissileCount(), 11, 81);
            gc.fillText("AI: " + (autoPlay ? "ON" : "OFF"), 11, 101);
        } else {
            // HUD chế độ 2 người

            // Góc trái: Player 1
            gc.fillText("P1 Score: " + player.getScore(), 11, 21);
            gc.fillText("P1 Lives: " + player.getLives(), 11, 41);
            gc.fillText("P1 Missiles: " + player.getMissileCount(), 11, 61);

            // Giữa: Wave
            gc.setTextAlign(TextAlignment.CENTER);
            gc.fillText("Wave " + (wave - 1), 1280 / 2, 21);
            gc.setTextAlign(TextAlignment.LEFT);

            // Góc phải: Player 2
            gc.fillText("P2 Score: " + player2.getScore(), 1050, 21);
            gc.fillText("P2 Lives: " + player2.getLives(), 1050, 41);
            gc.fillText("P2 Missiles: " + player2.getMissileCount(), 1050, 61);
        }

        // Chữ trắng đè lên để tạo độ nét
        gc.setFill(Color.WHITE);

        if (!isTwoPlayerMode) {
            gc.fillText("Score: " + player.getScore(), 10, 20);
            gc.fillText("Lives: " + player.getLives(), 10, 40);
            gc.fillText("Wave: " + (wave - 1), 10, 60);
            gc.fillText("Missiles: " + player.getMissileCount(), 10, 80);
            gc.fillText("AI: " + (autoPlay ? "ON" : "OFF"), 10, 100);
        } else {
            // Player 1
            gc.fillText("P1 Score: " + player.getScore(), 10, 20);
            gc.fillText("P1 Lives: " + player.getLives(), 10, 40);
            gc.fillText("P1 Missiles: " + player.getMissileCount(), 10, 60);

            // Wave
            gc.setTextAlign(TextAlignment.CENTER);
            gc.fillText("Wave " + (wave - 1), 1280 / 2 - 1, 20);
            gc.setTextAlign(TextAlignment.LEFT);

            // Player 2
            gc.fillText("P2 Score: " + player2.getScore(), 1049, 20);
            gc.fillText("P2 Lives: " + player2.getLives(), 1049, 40);
            gc.fillText("P2 Missiles: " + player2.getMissileCount(), 1049, 60);
        }
    }

    private static void applyPowerUp(PowerUpType type) {
        switch (type) {
            case HEALTH -> player.setLives(Math.min(player.getLives() + 1, 5)); // Sửa: tăng mạng cho player1
            case ROCKET -> player.addMissile(1);
            case AMMO -> player.upgradeShootLevel();
            case DAMAGE -> player.upgradeDamageLevel();
            case ENERGY -> player.upgradeFireRateLevel();
        }
    }

    private static void applyPowerUpToPlayer(Player p, PowerUpType type) {
        if (p == player) {
            applyPowerUp(type); // dùng hàm sẵn có cho player1
        } else if (p == player2) {
            switch (type) {
                case HEALTH -> player2.setLives(Math.min(player2.getLives() + 1, 5)); // Sửa: tăng mạng cho player2
                case ROCKET -> player2.addMissile(1);
                case AMMO -> player2.upgradeShootLevel();
                case DAMAGE -> player2.upgradeDamageLevel();
                case ENERGY -> player2.upgradeFireRateLevel();
            }
        }
    }

    public static void dropPowerUps(Enemy e) {
        double x = e.getX(), y = e.getY();
        if (e instanceof BossEnemy || e instanceof SuperBossEnemy) {
            powerUps.add(new PowerUp(x, y, PowerUpType.ROCKET));
            if ((player.getLives() < 5) || (player2 != null && player2.getLives() < 5))
                powerUps.add(new PowerUp(x + 10, y, PowerUpType.HEALTH)); // Sửa: xét cả player2
            if ((player.getFireRateLevel() < 3) || (player2 != null && player2.getFireRateLevel() < 3))
                powerUps.add(new PowerUp(x + 20, y, PowerUpType.ENERGY)); // Sửa: xét cả player2
            if ((player.getShootLevel() < 3) || (player2 != null && player2.getShootLevel() < 3))
                powerUps.add(new PowerUp(x + 30, y, PowerUpType.AMMO)); // Sửa: xét cả player2
            if ((player.getDamageLevel() < 3) || (player2 != null && player2.getDamageLevel() < 3))
                powerUps.add(new PowerUp(x + 40, y, PowerUpType.DAMAGE)); // Sửa: xét cả player2
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

    private static void checkGameOver() {
        if (!isTwoPlayerMode) {
            if (player.getLives() <= 0) gameOver = true; // Sửa: dùng số mạng player
        } else {
            if (player.getLives() <= 0 && player2.getLives() <= 0) gameOver = true; // Sửa: cả 2 người chết mới thua
        }
    }

    protected static void resetGameState() {
        bullets.clear();
        missiles.clear();
        enemies.clear();
        enemyBullets.clear();
        explosions.clear();
        powerUps.clear();
        // (Bỏ đặt lại score & lives, dùng Player.setLives và Player.score)
        wave = 1;
        waveSpawned = false;
        gameOver = false;
        paused = false;
        shooting = false;
        shootingP2 = false;
        superBossDefeated = false;
        autoPlay = false;

        player = new Player(420, 640, Assets.player);
        player.setLives(5); // Sửa: thiết lập số mạng cho player1

        if (isTwoPlayerMode) {
            player2 = new Player(820, 640, Assets.player2);
            player2.setLives(5); // Sửa: thiết lập số mạng cho player2
            autoPlay = false;
        } else {
            player2 = null;
        }
    }
}
