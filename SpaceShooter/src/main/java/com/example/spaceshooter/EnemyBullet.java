package com.example.spaceshooter;

import javafx.scene.canvas.GraphicsContext;

/**
 * Lớp đại diện cho đạn được bắn ra từ địch thường (di chuyển thẳng xuống dưới).
 */
public class EnemyBullet {
    /** Tọa độ X của đạn địch. */
    protected double x;
    /** Tọa độ Y của đạn địch. */
    protected double y;
    /** Tốc độ di chuyển của đạn (pixel mỗi khung hình). */
    private final double speed = 5;

    /**
     * Tạo một viên đạn địch tại vị trí (x, y) cho trước.
     * @param x tọa độ X ban đầu của đạn
     * @param y tọa độ Y ban đầu của đạn
     */
    public EnemyBullet(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Cập nhật vị trí viên đạn mỗi khung hình.
     * @return true nếu đạn còn trong màn hình, false nếu đã bay ra ngoài
     */
    public boolean update() {
        y += speed;
        return y <= 720;
    }

    /**
     * Vẽ đạn địch lên màn hình.
     * @param gc đối tượng GraphicsContext để vẽ
     */
    public void render(GraphicsContext gc) {
        gc.drawImage(Assets.enemyBullet, x, y, 10, 20);
    }

    /**
     * Kiểm tra va chạm giữa đạn và người chơi.
     * @param p đối tượng Player cần kiểm tra
     * @return true nếu đạn va chạm với người chơi
     */
    public boolean collidesWith(Player p) {
        double px = p.getX();
        double py = p.getY();
        return x < px + 40 && x + 10 > px &&
                y < py + 40 && y + 20 > py;
    }

    /** @return vị trí X hiện tại của đạn */
    public double getX() {
        return x;
    }

    /** @return vị trí Y hiện tại của đạn */
    public double getY() {
        return y;
    }
}
