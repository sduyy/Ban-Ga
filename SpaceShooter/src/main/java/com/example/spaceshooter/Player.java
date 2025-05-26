package com.example.spaceshooter;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

/**
 * Lớp đại diện cho người chơi (tàu không gian do người chơi điều khiển).
 */
public class Player {
    private Image sprite;
    private int score = 0;
    private int lives = 5;
    private double x, y;

    private boolean isHit = false;
    private long hitTime = 0;
    private boolean blinkState = false;
    private long lastBlinkTime = 0;

    private int missileCount = 3;

    private int shootLevel = 1;
    private int damageLevel = 1;
    private int fireRateLevel = 1;

    private static final SoundFX laserSound = new SoundFX("laser.wav");
    private static final SoundFX hitSound = new SoundFX("shipexplode.mp3");

    public Player(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Player(double x, double y, Image sprite) {
        this.x = x;
        this.y = y;
        this.sprite = sprite;
    }

    public void move(double dx, double dy) {
        x += dx;
        y += dy;
        clampPosition();
    }

    public void moveTo(double targetX, double targetY) {
        this.x = targetX;
        this.y = targetY;
        clampPosition();
    }

    private void clampPosition() {
        x = Math.max(0, Math.min(1240, x));
        y = Math.max(0, Math.min(680, y));
    }

    public boolean collidesWith(Enemy e) {
        double ex = e.getX();
        double ey = e.getY();
        return x < ex + 40 && x + 40 > ex && y < ey + 40 && y + 40 > ey;
    }

    public boolean collidesWith(EnemyBullet b) {
        return b.getX() > x && b.getX() < x + 40 &&
                b.getY() > y && b.getY() < y + 40;
    }

    public boolean collidesWith(PowerUp p) {
        double px = p.getX();
        double py = p.getY();
        return x < px + 30 && x + 40 > px && y < py + 30 && y + 40 > py;
    }

    public Bullet[] shoot() {
        laserSound.play();
        return switch (shootLevel) {
            case 1 -> new Bullet[]{new Bullet(x + 17.5, y, Assets.bullet, this)};
            case 2 -> new Bullet[]{
                    new Bullet(x + 12, y, Assets.bullet, this),
                    new Bullet(x + 23, y, Assets.bullet, this)
            };
            default -> new Bullet[]{
                    new Bullet(x + 10, y, Assets.bullet, this),
                    new Bullet(x + 17.5, y, Assets.bullet, this),
                    new Bullet(x + 25, y, Assets.bullet, this)
            };
        };
    }

    public Missile fireMissile() {
        if (missileCount <= 0) return null;
        missileCount--;
        return new Missile(x + 14, y, this);
    }

    public void update() {
        if (isHit && System.nanoTime() - hitTime > 300_000_000) {
            isHit = false;
        }

        if (System.nanoTime() - lastBlinkTime > 200_000_000) {
            blinkState = !blinkState;
            lastBlinkTime = System.nanoTime();
        }
    }

    public void markHit() {
        hitSound.play();
        isHit = true;
        hitTime = System.nanoTime();
    }

    public void render(GraphicsContext gc) {
        boolean shouldBlink = (lives <= 2 && blinkState) || isHit;

        if (shouldBlink) gc.setGlobalAlpha(0.4);
        gc.drawImage(sprite, x, y, 40, 40);
        gc.setGlobalAlpha(1.0);
    }

    public void upgradeShootLevel() {
        if (shootLevel < 3) shootLevel++;
    }

    public void upgradeDamageLevel() {
        if (damageLevel < 3) damageLevel++;
    }

    public void upgradeFireRateLevel() {
        if (fireRateLevel < 3) fireRateLevel++;
    }

    public int getShootCooldown() {
        return switch (fireRateLevel) {
            case 1 -> 500;
            case 2 -> 350;
            default -> 250;
        };
    }

    public int getBulletDamage() {
        return switch (damageLevel) {
            case 1 -> 10;
            case 2 -> 15;
            default -> 20;
        };
    }

    public void addMissile(int amount) {
        missileCount += amount;
    }

    public boolean canFireMissile() {
        return missileCount > 0;
    }

    public int getMissileCount() {
        return missileCount;
    }

    public int getShootLevel() {
        return shootLevel;
    }

    public int getDamageLevel() {
        return damageLevel;
    }

    public int getFireRateLevel() {
        return fireRateLevel;
    }

    public void addScore(int value) {
        score += value;
    }

    public int getScore() {
        return score;
    }

    public int getLives() {
        return lives;
    }

    public void setLives(int lives) {
        this.lives = lives;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }
}
