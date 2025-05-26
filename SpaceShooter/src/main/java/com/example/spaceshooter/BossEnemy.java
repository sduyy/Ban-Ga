package com.example.spaceshooter;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Lớp đại diện cho địch Boss (kẻ địch mạnh với nhiều kiểu tấn công và hành vi phức tạp).
 */
public class BossEnemy extends Enemy {
    /** Hiệu ứng âm thanh khi Boss phát nổ. */
    private static final SoundFX explodeSound = new SoundFX("missile.wav");

    /** Lượng máu tối đa của Boss. */
    private final int maxHP;
    /** Chế độ tấn công hiện tại của Boss (thay đổi luân phiên). */
    protected int attackMode = 0;
    /** Thời điểm cuối cùng chuyển chế độ tấn công. */
    private long lastSkillSwitch = System.currentTimeMillis();
    /** Khoảng cách giữa hai lần chuyển chế độ tấn công (ms). */
    private long skillCooldown = 5000;
    /** Thời điểm Boss bắn gần nhất. */
    private long lastShotTime = 0;
    /** Thời gian giữa hai lần bắn (ms). */
    private long shotCooldown = 800;
    /** Tốc độ di chuyển của Boss. */
    protected double moveSpeed = 2;
    /** Trạng thái cuồng nộ (khi máu < 30%). */
    private boolean enraged = false;

    /**
     * Khởi tạo Boss tại vị trí (x, y), thuộc wave chỉ định.
     */
    public BossEnemy(double x, double y, int wave) {
        super(x, y, wave);
        this.amplitude = 100;
        this.frequency = 0.008 + wave * 0.001;
        this.canShoot = true;
        this.hp = (int)((500 + wave * 30) * 1.75);
        this.maxHP = hp;
    }

    /**
     * Cập nhật trạng thái Boss mỗi khung hình.
     */
    @Override
    public void update() {
        angle += frequency;
        long now = System.currentTimeMillis();

        // Chuyển sang trạng thái cuồng nộ nếu máu thấp
        if (!enraged && hp <= maxHP * 0.3) {
            enraged = true;
            skillCooldown = 3000;
            shotCooldown = 400;
            moveSpeed = 3;
        }

        // Chuyển chế độ tấn công sau mỗi skillCooldown
        if (now - lastSkillSwitch > skillCooldown) {
            attackMode = (attackMode + 1) % 3;
            lastSkillSwitch = now;
        }

        // Bắn theo chế độ hiện tại
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

    /**
     * Trả về vị trí Y với hiệu ứng dao động (sin).
     */
    @Override
    public double getY() {
        return baseY + Math.sin(angle * 1.5) * 20;
    }

    /**
     * Vẽ Boss và thanh máu.
     */
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

    /**
     * Kiểm tra va chạm giữa Boss và đạn của người chơi.
     */
    @Override
    public boolean collidesWith(Bullet b) {
        return b.getX() > getX() && b.getX() < getX() + 80 &&
                b.getY() > getY() && b.getY() < getY() + 80;
    }

    /**
     * Boss bắn 1 viên đạn thẳng xuống (mặc định).
     */
    @Override
    public EnemyBullet shoot() {
        return new BossEnemyBullet(getX() + 35, getY() + 80, 0, 5);
    }

    /**
     * Gây sát thương lên Boss. Trả về true nếu bị tiêu diệt.
     */
    @Override
    public boolean takeDamage(int damage) {
        hp -= damage;
        if (hp <= 0) {
            explodeSound.play();
            return true;
        }
        return false;
    }

    /**
     * Tấn công theo kiểu bắn vòng tròn (trừ một góc để tạo khoảng tránh).
     */
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

    /**
     * Tấn công theo kiểu nhắm vào người chơi.
     */
    private void fireAimedShot(Player player) {
        double dx = player.getX() - getX();
        double dy = player.getY() - getY();
        double length = Math.sqrt(dx * dx + dy * dy);
        dx = dx / length * 4;
        dy = dy / length * 4;
        GameScene.enemyBullets.add(new BossEnemyBullet(getX() + 40, getY() + 40, dx, dy));
    }

    /**
     * Tấn công theo kiểu bắn dàn ngang hướng về người chơi.
     */
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

    /**
     * Cập nhật chuyển động tùy theo chế độ tấn công.
     */
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

        // Giới hạn phạm vi di chuyển
        startX = Math.max(100, Math.min(startX, 1180));
        baseY = Math.max(40, Math.min(baseY, 320));
    }
}
