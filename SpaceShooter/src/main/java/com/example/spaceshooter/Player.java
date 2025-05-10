package com.example.spaceshooter;

import javafx.scene.canvas.GraphicsContext;

public class Player {
    private double x, y;
    private boolean isHit = false;
    private long hitTime = 0;
    private boolean blinkState = false;
    private long lastBlinkTime = 0;

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
        x = Math.max(0, Math.min(760, x));
        y = Math.max(0, Math.min(560, y));
    }

    public boolean collidesWith(Enemy e) {
        double ex = e.getX();
        double ey = e.getY();
        return x < ex + 40 && x + 40 > ex && y < ey + 40 && y + 40 > ey;
    }

    public boolean collidesWith(EnemyBullet b) {
        return b.getX() > x && b.getX() < x + 40 && b.getY() > y && b.getY() < y + 40;
    }

    public double getX() { return x; }
    public double getY() { return y; }

    public Bullet shoot() {
        return new Bullet(x + 20, y);
    }

    public void update() {
        // Kết thúc hiệu ứng nhấp nháy khi bị trúng đạn sau 300ms
        if (isHit && System.nanoTime() - hitTime > 300_000_000) {
            isHit = false;
        }

        // Đổi trạng thái nhấp nháy nếu HP thấp (dùng thời gian thực)
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
            gc.setGlobalAlpha(0.4); // mờ đi
        }

        gc.drawImage(Assets.player, x, y, 40, 40);
        gc.setGlobalAlpha(1.0); // reset alpha
    }
}
