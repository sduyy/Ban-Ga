package com.example.spaceshooter;

import javafx.scene.canvas.GraphicsContext;

public class Bullet {
    private double x, y;
    private boolean alive = true;

    public Bullet(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public boolean update() {
        y -= 10;
        alive = y > 0;
        return alive;
    }

    public void render(GraphicsContext gc) {
        if (alive) {
            gc.drawImage(Assets.bullet, x, y, 10, 20);
        }
    }

    public void kill() {
        alive = false;
    }

    public boolean isAlive() {
        return alive;
    }

    // ➕ Thêm 2 phương thức này
    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }
}
