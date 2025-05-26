package com.example.spaceshooter;

import javafx.scene.canvas.GraphicsContext;

/**
 * Lớp đại diện cho địch thường trong trò chơi (tàu địch cơ bản có thể di chuyển và bắn).
 */
public class Enemy {
    /** Hiệu ứng âm thanh khi địch phát nổ. */
    private static final SoundFX explodeSound = new SoundFX("shipexplode.mp3");

    /** Tọa độ X ban đầu khi sinh ra. */
    protected double startX;
    /** Tọa độ Y gốc. */
    protected double baseY;
    /** Góc để tính chuyển động theo thời gian. */
    protected double angle = 0;
    /** Biên độ dao động ngang. */
    protected double amplitude = 40;
    /** Tần số dao động. */
    protected double frequency = 0.02;
    /** Cờ xác định địch có thể bắn hay không. */
    protected boolean canShoot = true;
    /** Máu hiện tại của địch. */
    protected int hp;

    /**
     * Khởi tạo địch thường với vị trí và cấp độ wave.
     * @param x vị trí X ban đầu
     * @param y vị trí Y ban đầu
     * @param wave đợt tấn công hiện tại (càng cao càng mạnh)
     */
    public Enemy(double x, double y, int wave) {
        this.startX = x;
        this.baseY = y;
        this.frequency = 0.01 + Math.min(wave * 0.002, 0.03);
        this.hp = 10 + 2 * (wave - 1) + 2 * ((wave - 1) / 3) + wave * 10;
    }

    /**
     * Cập nhật trạng thái di chuyển mỗi khung hình.
     */
    public void update() {
        angle += frequency;
    }

    /**
     * Vẽ địch lên màn hình.
     * @param gc đối tượng GraphicsContext để vẽ
     */
    public void render(GraphicsContext gc) {
        gc.drawImage(Assets.enemy, getX(), getY(), 40, 40);
    }

    /**
     * Tính toán vị trí X dao động ngang.
     * @return vị trí X hiện tại
     */
    public double getX() {
        return startX + Math.sin(angle) * amplitude;
    }

    /**
     * Tính toán vị trí Y dao động lên xuống nhẹ.
     * @return vị trí Y hiện tại
     */
    public double getY() {
        return baseY + Math.sin(angle * 2) * 10;
    }

    /**
     * Kiểm tra va chạm với đạn của người chơi.
     * @param b viên đạn
     * @return true nếu có va chạm
     */
    public boolean collidesWith(Bullet b) {
        return b.getX() > getX() && b.getX() < getX() + 40 &&
                b.getY() > getY() && b.getY() < getY() + 40;
    }

    /**
     * Kiểm tra va chạm với tên lửa (missile).
     * @param m tên lửa
     * @return true nếu có va chạm
     */
    public boolean collidesWith(Missile m) {
        return m.getX() > getX() && m.getX() < getX() + 40 &&
                m.getY() > getY() && m.getY() < getY() + 40;
    }

    /**
     * Giảm 1 máu và trả về true nếu đã chết.
     * @return true nếu máu còn lại <= 0
     */
    public boolean takeHit() {
        return takeDamage(1);
    }

    /**
     * Gây sát thương cho địch.
     * @param damage lượng sát thương
     * @return true nếu địch bị tiêu diệt
     */
    public boolean takeDamage(int damage) {
        hp -= damage;
        if (hp <= 0) {
            explodeSound.play();
            return true;
        }
        return false;
    }

    /**
     * Địch bắn ra một viên đạn.
     * @return EnemyBullet mới
     */
    public EnemyBullet shoot() {
        return new EnemyBullet(getX() + 18, getY() + 40);
    }

    /**
     * Kiểm tra địch này có thể bắn đạn không.
     * @return true nếu có thể bắn
     */
    public boolean isShooter() {
        return canShoot;
    }
}
