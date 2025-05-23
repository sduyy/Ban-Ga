package com.example.spaceshooter;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class BossEnemy extends Enemy {
    private final int maxHP;
    protected int attackMode = 0;
    private long lastSkillSwitch = System.currentTimeMillis();
    private long skillCooldown = 5000;
    private long lastShotTime = 0;
    private long shotCooldown = 800;
    protected double moveSpeed = 2;
    private boolean enraged = false;

    public BossEnemy(double x, double y, int wave) {
        super(x, y, wave);
        this.amplitude = 100;
        this.frequency = 0.008 + wave * 0.001;
        this.canShoot = true;
        this.hp = (int)((500 + wave * 30) * 1.75);
        this.maxHP = hp;
    }

    @Override
    public void update() {
        angle += frequency;

        long now = System.currentTimeMillis();

        if (!enraged && hp <= maxHP * 0.3) {
            enraged = true;
            skillCooldown = 3000;
            shotCooldown = 400;
            moveSpeed = 3;
        }

        if (now - lastSkillSwitch > skillCooldown) {
            attackMode = (attackMode + 1) % 3;
            lastSkillSwitch = now;
        }

        if (now - lastShotTime > shotCooldown) {
            switch (attackMode) {
                case 0 -> fireRadialBurst();
                case 1 -> fireAimedShot(GameScene.player);
                case 2 -> fireStrafeBurst(GameScene.player);
            }
            lastShotTime = now;
        }

        updateMovementForMode();
    }

    @Override
    public double getY() {
        return baseY + Math.sin(angle * 1.5) * 20;
    }

    @Override
    public void render(GraphicsContext gc) {
        double x = getX();
        double y = getY();

        gc.drawImage(Assets.bossEnemy, x, y, 80, 80);
        gc.setFill(Color.RED);
        gc.fillRect(x, y - 10, 80, 8);
        gc.setFill(Color.LIMEGREEN);
        gc.fillRect(x, y - 10, (hp / (double) maxHP) * 80, 8);
    }

    @Override
    public boolean collidesWith(Bullet b) {
        return b.getX() > getX() && b.getX() < getX() + 80 &&
                b.getY() > getY() && b.getY() < getY() + 80;
    }

    @Override
    public EnemyBullet shoot() {
        return new BossEnemyBullet(getX() + 35, getY() + 80, 0, 5);
    }

    @Override
    public boolean takeDamage(int damage) {
        hp -= damage;
        return hp <= 0;
    }

    private void fireRadialBurst() {
        int count = 12;
        double angleStep = 2 * Math.PI / count;
        double timeOffset = (System.currentTimeMillis() % 3600) / 3600.0 * 2 * Math.PI;
        double dodgeAngle = Math.toRadians((System.currentTimeMillis() / 100) % 360);

        for (int i = 0; i < count; i++) {
            double angle = i * angleStep + timeOffset;
            if (Math.abs(angle - dodgeAngle) < Math.toRadians(15)) continue;

            double dx = Math.cos(angle) * 3;
            double dy = Math.sin(angle) * 3;
            GameScene.enemyBullets.add(new BossEnemyBullet(getX() + 40, getY() + 40, dx, dy));
        }
    }

    private void fireAimedShot(Player player) {
        double dx = player.getX() - getX();
        double dy = player.getY() - getY();
        double length = Math.sqrt(dx * dx + dy * dy);
        dx = dx / length * 4;
        dy = dy / length * 4;
        GameScene.enemyBullets.add(new BossEnemyBullet(getX() + 40, getY() + 40, dx, dy));
    }

    private void fireStrafeBurst(Player player) {
        double dx = player.getX() - getX();
        double dy = player.getY() - getY();
        double length = Math.sqrt(dx * dx + dy * dy);
        dx = dx / length * 3;
        dy = dy / length * 3;

        for (int i = -2; i <= 2; i++) {
            double offset = i * 15;
            GameScene.enemyBullets.add(new BossEnemyBullet(getX() + 40 + offset, getY() + 80, dx, dy));
        }
    }

    private void updateMovementForMode() {
        switch (attackMode) {
            case 0 -> startX += Math.sin(System.currentTimeMillis() * 0.001) * 0.5;
            case 1 -> {
                if (getX() < GameScene.player.getX()) startX += moveSpeed;
                else startX -= moveSpeed;
            }
            case 2 -> {
                startX += moveSpeed;
                if (startX < 50 || startX > 1280 - 130) moveSpeed *= -1;
            }
        }
        double minX = 100;
        double maxX = 1180;
        double minY = 40;
        double maxY = 320;

        startX = Math.max(minX, Math.min(startX, maxX));
        baseY = Math.max(minY, Math.min(baseY, maxY));
    }
}