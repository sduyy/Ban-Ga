package com.example.spaceshooter;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class BossEnemy extends Enemy {
    private int bossHP;
    private final int maxHP;

    public BossEnemy(double x, double y, int wave) {
        super(x, y, wave);
        this.amplitude = 100;
        this.frequency = 0.008 + wave * 0.001;
        this.canShoot = true;
        this.bossHP = 10 + wave * 2;
        this.maxHP = bossHP;
    }

    @Override
    public void update() {
        angle += frequency;
    }

    @Override
    public double getY() {
        return baseY + Math.sin(angle * 1.5) * 20;
    }

    @Override
    public void render(GraphicsContext gc) {
        double x = getX();
        double y = getY();

        gc.drawImage(Assets.enemy, x, y, 80, 80);

        // Thanh máu boss
        gc.setFill(Color.RED);
        gc.fillRect(x, y - 10, 80, 8);  // nền
        gc.setFill(Color.LIMEGREEN);
        gc.fillRect(x, y - 10, (bossHP / (double) maxHP) * 80, 8);  // máu còn lại
    }

    @Override
    public boolean collidesWith(Bullet b) {
        return b.getX() > getX() && b.getX() < getX() + 80 &&
                b.getY() > getY() && b.getY() < getY() + 80;
    }

    @Override
    public EnemyBullet shoot() {
        return new EnemyBullet(getX() + 35, getY() + 80);
    }

    @Override
    public boolean takeHit() {
        bossHP--;
        return bossHP <= 0;
    }
}
