package com.example.spaceshooter;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

/**
 * Vật phẩm tăng sức mạnh (power-up) mà người chơi có thể thu thập để nâng cấp khả năng chiến đấu.
 */
public class PowerUp {
    /** Tọa độ X của vật phẩm. */
    private double x;
    /** Tọa độ Y của vật phẩm. */
    private double y;
    /** Loại vật phẩm. */
    private PowerUpType type;
    /** Thời điểm xuất hiện trên màn hình. */
    private long spawnTime;
    /** Cờ cho biết vật phẩm đã được thu thập chưa. */
    private boolean collected = false;
    /** Cờ cho biết vật phẩm đang rơi hay đã chạm đất. */
    private boolean falling = true;

    private static final double SPEED = 1.5;
    private static final long DURATION = 5_000_000_000L; // 5 giây

    /**
     * Tạo vật phẩm tại vị trí (x, y) với loại cho trước.
     * @param x vị trí X
     * @param y vị trí Y
     * @param type loại vật phẩm
     */
    public PowerUp(double x, double y, PowerUpType type) {
        this.x = x;
        this.y = y;
        this.type = type;
        this.spawnTime = System.nanoTime();
    }

    /**
     * Cập nhật trạng thái vật phẩm mỗi khung hình.
     */
    public void update() {
        if (falling) {
            y += SPEED;
            if (y >= 650) {
                falling = false;
                spawnTime = System.nanoTime(); // bắt đầu đếm thời gian tồn tại trên mặt đất
            }
        }
    }

    /**
     * Vẽ vật phẩm lên màn hình.
     * @param gc GraphicsContext để vẽ
     */
    public void render(GraphicsContext gc) {
        Image img = switch (type) {
            case HEALTH -> Assets.powerUpHealth;
            case ENERGY -> Assets.powerUpEnergy;
            case AMMO -> Assets.powerUpAmmo;
            case DAMAGE -> Assets.powerUpDamage;
            case ROCKET -> Assets.powerUpRockets;
        };
        gc.drawImage(img, x, y, 32, 32);
    }

    /**
     * Kiểm tra vật phẩm đã hết hạn chưa (sau khi chạm đất).
     * @return true nếu đã hết hạn
     */
    public boolean isExpired() {
        return !falling && System.nanoTime() - spawnTime >= DURATION;
    }

    /** Đánh dấu vật phẩm đã được thu thập. */
    public void collect() {
        collected = true;
    }

    /** @return true nếu vật phẩm đã được nhặt */
    public boolean isCollected() {
        return collected;
    }

    public PowerUpType getType() {
        return type;
    }

    public double getX() { return x; }

    public double getY() { return y; }

    /**
     * Kiểm tra va chạm với người chơi.
     * @param p người chơi
     * @return true nếu chạm
     */
    public boolean collidesWith(Player p) {
        double px = p.getX();
        double py = p.getY();
        return x < px + 40 && x + 32 > px && y < py + 40 && y + 32 > py;
    }
}
