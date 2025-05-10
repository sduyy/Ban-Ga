package com.example.spaceshooter;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Enemy {
    protected double startX, baseY;
    protected double angle = 0;
    protected double amplitude = 40;
    protected double frequency = 0.015;
    protected boolean canShoot = true;
    protected int hp;

    public Enemy(double x, double y) {
        this(x, y, 1); // mặc định wave 1 nếu không có thông tin
    }

    public Enemy(double x, double y, int wave) {
        this.startX = x;
        this.baseY = y;
        this.canShoot = true;
        this.hp = Math.min(1 + wave / 2, 5); // wave 1–2: hp = 1, wave 3–4: hp = 2, max 5
        this.frequency = 0.01 + Math.min(wave * 0.002, 0.03); // dao động tăng dần nhẹ
    }

    public void update() {
        angle += frequency;
    }

    public void render(GraphicsContext gc) {
        gc.drawImage(Assets.enemy, getX(), getY(), 40, 40);
    }

    public double getX() {
        return startX + Math.sin(angle) * amplitude;
    }

    public double getY() {
        return baseY + Math.sin(angle * 2) * 10;
    }

    public boolean collidesWith(Bullet b) {
        return b.getX() > getX() && b.getX() < getX() + 40 && b.getY() > getY() && b.getY() < getY() + 40;
    }

    public boolean isShooter() {
        return canShoot;
    }

    public EnemyBullet shoot() {
        return new EnemyBullet(getX() + 18, getY() + 40);
    }

    public boolean takeHit() {
        hp--;
        return hp <= 0;
    }
}
