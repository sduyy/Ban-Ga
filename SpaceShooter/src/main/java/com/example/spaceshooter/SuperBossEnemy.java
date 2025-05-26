package com.example.spaceshooter;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Lớp đại diện cho boss siêu cấp trong game, kế thừa từ lớp Enemy cơ bản.
 * Boss này có nhiều chế độ tấn công, khả năng gọi boss nhỏ và trạng thái phẫn nộ khi máu thấp.
 */
public class SuperBossEnemy extends Enemy {
    private final int maxHp;
    private boolean[] miniBossCalled = new boolean[4];

    private int attackMode = 0;
    private long lastSkillSwitch = System.currentTimeMillis();
    private long skillCooldown = 4000;
    private long lastShotTime = 0;
    private long shotCooldown = 600;
    private double moveSpeed = 2.5;
    private boolean enraged = false;

    /**
     * Khởi tạo boss siêu cấp với vị trí và màn chơi chỉ định.
     *
     * @param x tọa độ x ban đầu của boss
     * @param y tọa độ y ban đầu của boss
     * @param wave số màn chơi hiện tại, dùng để cân bằng độ khó
     */
    public SuperBossEnemy(double x, double y, int wave) {
        super(x, y, wave);
        this.hp = (int)((500 + wave * 30) * 3.0);
        this.maxHp = hp;
        this.amplitude = 120;
        this.frequency = 0.006 + wave * 0.001;
        this.canShoot = true;
    }

    /**
     * Lấy giá trị máu tối đa của boss.
     *
     * @return giá trị HP tối đa
     */
    public int getMaxHp() {
        return maxHp;
    }

    /**
     * Cập nhật trạng thái boss bao gồm di chuyển, tấn công và kỹ năng đặc biệt.
     * Tự động kích hoạt trạng thái phẫn nộ khi máu xuống dưới 50%.
     * Luân phiên các chế độ tấn công theo thời gian.
     */
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

    /**
     * Kiểm tra và triệu hồi boss nhỏ khi máu xuống các mốc 75%, 50%, 25% và 10%.
     * Mỗi mốc chỉ triệu hồi 1 lần duy nhất.
     */
    private void checkMiniBossSummon() {
        double percent = hp / (double) maxHp;
        if (!miniBossCalled[0] && percent <= 0.75) { spawnMini(); miniBossCalled[0] = true; }
        else if (!miniBossCalled[1] && percent <= 0.5) { spawnMini(); miniBossCalled[1] = true; }
        else if (!miniBossCalled[2] && percent <= 0.25) { spawnMini(); miniBossCalled[2] = true; }
        else if (!miniBossCalled[3] && percent <= 0.1) { spawnMini(); miniBossCalled[3] = true; }
    }

    /**
     * Triệu hồi boss nhỏ tại vị trí ngẫu nhiên quanh boss chính.
     */
    private void spawnMini() {
        double spawnX = getX() + (Math.random() * 100 - 50);
        double spawnY = getY() + (Math.random() * 100 - 50);
        GameScene.enemies.add(new MiniBossEnemy(spawnX, spawnY, maxHp));
    }

    /**
     * Tấn công kiểu bắn tỏa tròn với 14 viên đạn.
     * Góc bắn thay đổi theo thời gian để tạo hiệu ứng xoắn ốc.
     */
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

    /**
     * Tấn công kiểu bắn nhắm vào vị trí hiện tại của người chơi.
     *
     * @param player đối tượng người chơi để tính toán hướng bắn
     */
    private void fireAimedShot(Player player) {
        double dx = player.getX() - getX();
        double dy = player.getY() - getY();
        double length = Math.sqrt(dx * dx + dy * dy);
        dx = dx / length * 4;
        dy = dy / length * 4;
        GameScene.enemyBullets.add(new SuperBossBullet(getX() + 50, getY() + 50, dx, dy));
    }

    /**
     * Tấn công kiểu bắn loạt 5 viên đạn song song hướng về phía người chơi.
     *
     * @param player đối tượng người chơi để tính toán hướng bắn
     */
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

    /**
     * Cập nhật chuyển động theo chế độ tấn công hiện tại:
     * - Chế độ 0: Di chuyển ngang theo sóng sin
     * - Chế độ 1: Đuổi theo vị trí ngang của người chơi
     * - Chế độ 2: Di chuyển ngang qua lại trong phạm vi màn hình
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

    /**
     * Tính toán tọa độ y với hiệu ứng dao động theo sóng sin.
     *
     * @return tọa độ y hiện tại sau khi áp dụng hiệu ứng
     */
    @Override
    public double getY() {
        return baseY + Math.sin(angle * 1.5) * 25;
    }

    /**
     * Vẽ boss lên màn hình cùng thanh máu.
     *
     * @param gc đối tượng GraphicsContext để vẽ
     */
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

    /**
     * Ghi đè phương thức shoot - boss sử dụng kỹ năng đặc biệt thay cho bắn thông thường.
     *
     * @return null vì boss không sử dụng cơ chế bắn thông thường
     */
    @Override
    public EnemyBullet shoot() {
        return null;
    }

    /**
     * Kiểm tra va chạm với đạn.
     *
     * @param b đối tượng đạn cần kiểm tra
     * @return true nếu có va chạm, false nếu không
     */
    @Override
    public boolean collidesWith(Bullet b) {
        return b.getX() > getX() && b.getX() < getX() + 100 &&
                b.getY() > getY() && b.getY() < getY() + 100;
    }

    /**
     * Nhận sát thương và kiểm tra boss có bị tiêu diệt hay không.
     *
     * @param damage lượng sát thương nhận vào
     * @return true nếu HP ≤ 0 sau khi trừ, false nếu còn sống
     */
    @Override
    public boolean takeDamage(int damage) {
        hp -= damage;
        return hp <= 0;
    }
}