package com.example.spaceshooter;

import javafx.scene.canvas.GraphicsContext;

public class Player {
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

    public Player(double x, double y) {
        this.x = x;
        this.y = y;
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
        return b.getX() > x && b.getX() < x + 40 && b.getY() > y && b.getY() < y + 40;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public Bullet[] shoot() {
        laserSound.play();
        int damage = getBulletDamage();
        return switch (shootLevel) {
            case 1 -> new Bullet[]{new Bullet(x + 17.5, y, damage)};
            case 2 -> new Bullet[]{
                    new Bullet(x + 12, y, damage),
                    new Bullet(x + 23, y, damage)
            };
            default -> new Bullet[]{
                    new Bullet(x + 10, y, damage),
                    new Bullet(x + 17.5, y, damage),
                    new Bullet(x + 25, y, damage)
            };
        };
    }

    public Missile fireMissile() {
        if (missileCount <= 0) return null;
        missileCount--;
        return new Missile(x + 14, y);
    }

    public boolean canFireMissile() {
        return missileCount > 0;
    }

    public void addMissile(int amount) {
        missileCount += 1;
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
        isHit = true;
        hitTime = System.nanoTime();
    }

    public void render(GraphicsContext gc) {
        boolean shouldBlink = (GameScene.lives <= 2 && blinkState) || isHit;

        if (shouldBlink) {
            gc.setGlobalAlpha(0.4);
        }

        gc.drawImage(Assets.player, x, y, 40, 40);
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
}
