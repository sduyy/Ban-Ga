package com.example.spaceshooter;

import javafx.scene.canvas.GraphicsContext;

public class Enemy {
    private double x, y;

    public Enemy(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void update() {
        y += 1.5; // 👈 giảm tốc độ rơi chậm hơn
    }

    public void render(GraphicsContext gc) {
        gc.drawImage(Assets.enemy, x, y, 40, 40);
    }

    public boolean collidesWith(Bullet b) {
        return b.getX() > x && b.getX() < x + 40 && b.getY() > y && b.getY() < y + 40;
    }
}
