package com.example.spaceshooter;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import java.lang.Math;
import com.example.spaceshooter.GameScene;


public class SuperBossEnemy extends Enemy {
    private final int maxHp;
    private boolean[] miniBossCalled = new boolean[4];
    private int attackMode = 0;
    private long lastSkillSwitch = System.currentTimeMillis();
    private long skillCooldown = 4000;
    private long lastShotTime = 0;
    private long shotCooldown = 500;
    private double moveSpeed = 2.5;
    private boolean enraged = false;

    public SuperBossEnemy(double x, double y, int wave) {
        super(x, y, wave);
        this.amplitude = 120;
        this.frequency = 0.006 + wave * 0.001;
        this.canShoot = true;
        this.hp = (int)((500 + wave * 30) * 4.0);
        this.maxHp = hp;
    }

    public int getMaxHp() {
        return maxHp;
    }

    @Override
    public void update() {
        angle += frequency;
        long now = System.currentTimeMillis();

        if (!enraged && hp <= maxHp * 0.5) {
            enraged = true;
            skillCooldown = 2000;
            shotCooldown = 300;
            moveSpeed = 4;
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
        checkMiniBossSummon();
    }

    private void checkMiniBossSummon() {
        double percent = hp / (double) maxHp;
        if (!miniBossCalled[0] && percent <= 0.8) { spawnMini(); miniBossCalled[0] = true; }
        else if (!miniBossCalled[1] && percent <= 0.6) { spawnMini(); miniBossCalled[1] = true; }
        else if (!miniBossCalled[2] && percent <= 0.4) { spawnMini(); miniBossCalled[2] = true; }
        else if (!miniBossCalled[3] && percent <= 0.2) { spawnMini(); miniBossCalled[3] = true; }
    }

    private void spawnMini() {
        double spawnX = getX() + 40;
        double spawnY = getY() + 100;
        GameScene.enemies.add(new MiniBossEnemy(spawnX, spawnY, maxHp));
    }

    private void fireRadialBurst() {
        int count = 14;
        double angleStep = 2 * Math.PI / count;
        double timeOffset = (System.currentTimeMillis() % 3600) / 3600.0 * 2 * Math.PI;
        for (int i = 0; i < count; i++) {
            double angle = i * angleStep + timeOffset;
            double dx = Math.cos(angle) * 3;
            double dy = Math.sin(angle) * 3;
            GameScene.enemyBullets.add(new SuperBossBullet(getX() + 50, getY() + 50, dx, dy));
        }
    }

    private void fireAimedShot(Player player) {
        double dx = player.getX() - getX();
        double dy = player.getY() - getY();
        double length = Math.sqrt(dx * dx + dy * dy);
        dx = dx / length * 4;
        dy = dy / length * 4;
        GameScene.enemyBullets.add(new SuperBossBullet(getX() + 50, getY() + 50, dx, dy));
    }

    private void fireStrafeBurst(Player player) {
        double dx = player.getX() - getX();
        double dy = player.getY() - getY();
        double length = Math.sqrt(dx * dx + dy * dy);
        dx = dx / length * 3;
        dy = dy / length * 3;
        for (int i = -2; i <= 2; i++) {
            double offset = i * 15;
            GameScene.enemyBullets.add(new SuperBossBullet(getX() + 50 + offset, getY() + 100, dx, dy));
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
                if (startX < 100 || startX > 1180) moveSpeed *= -1;
            }
        }

        double minX = 100;
        double maxX = 1180;
        double minY = 40;
        double maxY = 320;
        startX = Math.max(minX, Math.min(startX, maxX));
        baseY = Math.max(minY, Math.min(baseY, maxY));
    }


    @Override
    public double getY() {
        return baseY + Math.sin(angle * 1.5) * 25;
    }

    @Override
    public void render(GraphicsContext gc) {
        double x = getX();
        double y = getY();
        gc.drawImage(Assets.superBossEnemy, x, y, 140, 140);
        gc.setFill(Color.RED);
        gc.fillRect(x, y - 12, 140, 10);
        gc.setFill(Color.LIGHTGREEN);
        gc.fillRect(x, y - 12, (hp / (double) maxHp) * 140, 10);
    }

    @Override
    public EnemyBullet shoot() {
        return null; // Không dùng shoot đơn, đã dùng kỹ năng thay thế
    }

    @Override
    public boolean collidesWith(Bullet b) {
        return b.getX() > getX() && b.getX() < getX() + 100 &&
                b.getY() > getY() && b.getY() < getY() + 100;
    }

    @Override
    public boolean takeDamage(int damage) {
        hp -= damage;
        return hp <= 0;
    }
}
