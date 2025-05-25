package com.example.spaceshooter;

import javafx.scene.canvas.GraphicsContext;

public class Enemy {
    private static final SoundFX explodeSound = new SoundFX("shipexplode.mp3");

    protected double startX, baseY;
    protected double angle = 0;
    protected double amplitude = 40;
    protected double frequency = 0.02;
    protected boolean canShoot = true;
    protected int hp;

    public Enemy(double x, double y, int wave) {
        this.startX = x;
        this.baseY = y;
        this.frequency = 0.01 + Math.min(wave * 0.002, 0.03);
        this.hp = 10 + 2 * (wave - 1) + 2 * ((wave - 1) / 3) + wave * 10;
    }
    public boolean collidesWith(Missile m) {
        return m.getX() > getX() && m.getX() < getX() + 40 &&
                m.getY() > getY() && m.getY() < getY() + 40;
    }

    public void update() {
        angle += frequency;
    }

    public void render(GraphicsContext gc) {
        gc.drawImage(Assets.enemy, getX(), getY(), 40, 40);
    }

    public double getX() { return startX + Math.sin(angle) * amplitude; }
    public double getY() { return baseY + Math.sin(angle * 2) * 10; }

    public boolean collidesWith(Bullet b) {
        return b.getX() > getX() && b.getX() < getX() + 40 &&
                b.getY() > getY() && b.getY() < getY() + 40;
    }

    public EnemyBullet shoot() {
        return new EnemyBullet(getX() + 18, getY() + 40);
    }

    public boolean takeHit() {
        return takeDamage(1);
    }

    public boolean takeDamage(int damage) {
        hp -= damage;
        if (hp <= 0) {
            explodeSound.play();
            return true;
        }
        return hp <= 0;
    }

    public boolean isShooter() { return canShoot; }
}
