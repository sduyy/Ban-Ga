package com.example.spaceshooter;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class SuperBossEnemy extends Enemy {
    private int hp;
    private final int maxHp;

    public SuperBossEnemy(double x, double y, int wave) {
        super(x, y, wave);
        this.amplitude = 120;
        this.frequency = 0.006 + wave * 0.001;
        this.canShoot = true;

        this.hp = 60 + wave * 4; // Super boss khỏe hơn boss thường
        this.maxHp = hp;
    }

    @Override
    public void update() {
        angle += frequency;
    }

    @Override
    public double getY() {
        return baseY + Math.sin(angle * 1.5) * 25;
    }

    @Override
    public void render(GraphicsContext gc) {
        double x = getX();
        double y = getY();

        // Vẽ thân Super Boss
        gc.setFill(Color.DARKMAGENTA);
        gc.fillRect(x, y, 100, 100);

        // Vẽ thanh máu phía trên boss
        gc.setFill(Color.RED);
        gc.fillRect(x, y - 12, 100, 10);

        gc.setFill(Color.LIGHTGREEN);
        gc.fillRect(x, y - 12, (hp / (double) maxHp) * 100, 10);
    }

    @Override
    public EnemyBullet shoot() {
        return new EnemyBullet(getX() + 45, getY() + 100);
    }

    @Override
    public boolean collidesWith(Bullet b) {
        return b.getX() > getX() && b.getX() < getX() + 100 &&
                b.getY() > getY() && b.getY() < getY() + 100;
    }

    @Override
    public boolean takeHit() {
        hp--;
        return hp <= 0;
    }
}
