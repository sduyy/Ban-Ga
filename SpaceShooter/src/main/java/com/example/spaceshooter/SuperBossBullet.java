package com.example.spaceshooter;

import javafx.scene.canvas.GraphicsContext;


class SuperBossBullet extends EnemyBullet {
    private double x, y, dx, dy;

    public SuperBossBullet(double x, double y, double dx, double dy) {
        super(x, y);
        this.x = x;
        this.y = y;
        this.dx = dx;
        this.dy = dy;
    }

    @Override
    public boolean update() {
        x += dx;
        y += dy;
        return y <= 720 && x >= 0 && x <= 1280;
    }

    @Override
    public void render(GraphicsContext gc) {
        gc.drawImage(Assets.superBossBullet, x, y, 18, 36);
    }

    @Override
    public double getX() {
        return x;
    }

    @Override
    public double getY() {
        return y;
    }
}