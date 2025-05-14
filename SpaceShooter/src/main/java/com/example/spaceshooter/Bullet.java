package com.example.spaceshooter;

import javafx.scene.canvas.GraphicsContext;

public class Bullet {
    private double x, y;
    private boolean alive = true;
    private int damage; // Thêm trường damage

    public Bullet(double x, double y, int damage) { // Sửa constructor
        this.x = x;
        this.y = y;
        this.damage = damage;
    }

    public boolean update() {
        y -= 10;
        return alive && y > 0;
    }

    public void kill() {
        alive = false;
    }

    public void render(GraphicsContext gc) {
        gc.drawImage(Assets.bullet, x, y, 5, 10);
    }

    public double getX() { return x; }
    public double getY() { return y; }

    public int getDamage() {
        return damage;
    }
}
