package com.example.spaceshooter;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class PowerUp {
    private double x, y;
    private PowerUpType type;
    private long spawnTime;
    private boolean collected = false;
    private boolean falling = true;
    private static final double SPEED = 1.5;
    private static final long DURATION = 5_000_000_000L;

    public PowerUp(double x, double y, PowerUpType type) {
        this.x = x;
        this.y = y;
        this.type = type;
        this.spawnTime = System.nanoTime();
    }

    public void update() {
        if (falling) {
            y += SPEED;
            if (y >= 650) {
                falling = false;
                spawnTime = System.nanoTime();
            }
        }
    }

    public void render(GraphicsContext gc) {
        Image img = switch (type) {
            case HEALTH -> Assets.powerUpHealth;
            case ENERGY -> Assets.powerUpEnergy;
            case AMMO -> Assets.powerUpAmmo;
            case DAMAGE -> Assets.powerUpDamage;
            case ROCKET -> Assets.powerUpRockets;
        };
        gc.drawImage(img, x, y, 32, 32);
    }

    public boolean isExpired() {
        return !falling && System.nanoTime() - spawnTime >= DURATION;
    }

    public boolean isCollected() {
        return collected;
    }

    public void collect() {
        collected = true;
    }

    public PowerUpType getType() {
        return type;
    }

    public double getX() { return x; }
    public double getY() { return y; }

    public boolean collidesWith(Player p) {
        double px = p.getX();
        double py = p.getY();
        return x < px + 40 && x + 32 > px && y < py + 40 && y + 32 > py;
    }
}
