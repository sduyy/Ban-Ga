package com.example.spaceshooter;

import javafx.scene.canvas.GraphicsContext;

public class EnemyBullet {
    private double x, y;
    private final double speed = 5;

    public EnemyBullet(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public boolean update() {
        y += speed;
        return y <= 720;
    }

    public void render(GraphicsContext gc) {
        gc.drawImage(Assets.enemyBullet, x, y, 10, 20);
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public boolean collidesWith(Player p) {
        double px = p.getX();
        double py = p.getY();
        return x < px + 40 && x + 10 > px && y < py + 40 && y + 20 > py;
    }
}
