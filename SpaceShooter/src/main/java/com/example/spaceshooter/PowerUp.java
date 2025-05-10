package com.example.spaceshooter;

import javafx.scene.canvas.GraphicsContext;

public class PowerUp {
    private double x, y;
    private boolean collected = false;
    private boolean stuckAtBottom = false;
    private long timeStuck = 0;

    public PowerUp(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void update() {
        if (!stuckAtBottom) {
            y += 1.5;
            if (y >= 560) {
                y = 560;
                stuckAtBottom = true;
                timeStuck = System.currentTimeMillis();
            }
        } else {
            if (System.currentTimeMillis() - timeStuck > 5000) {
                collected = true;
            }
        }
    }

    public void render(GraphicsContext gc) {
        gc.drawImage(Assets.powerUp, x, y, 30, 30);
    }

    public boolean collidesWith(Player p) {
        double px = p.getX();
        double py = p.getY();
        return x < px + 40 && x + 30 > px && y < py + 40 && y + 30 > py;
    }

    public void collect() {
        collected = true;
    }

    public boolean isCollected() {
        return collected;
    }

    public boolean isOffScreen() {
        return false; // Vì giờ power-up không tự biến mất bằng tọa độ y nữa
    }
}
