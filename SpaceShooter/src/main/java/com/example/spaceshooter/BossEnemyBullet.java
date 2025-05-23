package com.example.spaceshooter;

import javafx.scene.canvas.GraphicsContext;

public class BossEnemyBullet extends EnemyBullet {
    private double x, y;
    private double dx, dy;
    private final int width = 16;
    private final int height = 32;

    public BossEnemyBullet(double x, double y, double dx, double dy) {
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
        return y <= 720;
    }

    @Override
    public void render(GraphicsContext gc) {
        gc.drawImage(Assets.bossBullet, x, y, width, height);
    }

    public boolean isOffScreen(double screenWidth, double screenHeight) {
        return y > screenHeight || x < -width || x > screenWidth + width;
    }

    @Override
    public double getX() {
        return x;
    }

    @Override
    public double getY() {
        return y;
    }

    @Override
    public boolean collidesWith(Player p) {
        double px = p.getX();
        double py = p.getY();
        return x < px + 40 && x + width > px && y < py + 40 && y + height > py;
    }
}
