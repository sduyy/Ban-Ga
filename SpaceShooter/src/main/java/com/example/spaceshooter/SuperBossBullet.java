package com.example.spaceshooter;

import javafx.scene.canvas.GraphicsContext;

/**
 * Lớp đại diện cho đạn của Boss Siêu Cấp.
 * Đạn di chuyển theo hướng xác định (dx, dy), có hình ảnh riêng và kích thước lớn.
 */
class SuperBossBullet extends EnemyBullet {
    /** Vận tốc trục X của đạn */
    private double dx;
    /** Vận tốc trục Y của đạn */
    private double dy;

    /**
     * Tạo đạn Boss Siêu Cấp tại vị trí (x, y) với hướng bay (dx, dy).
     * @param x tọa độ X ban đầu
     * @param y tọa độ Y ban đầu
     * @param dx vận tốc theo trục X
     * @param dy vận tốc theo trục Y
     */
    public SuperBossBullet(double x, double y, double dx, double dy) {
        super(x, y); // sử dụng x, y kế thừa
        this.dx = dx;
        this.dy = dy;
    }

    /**
     * Cập nhật vị trí đạn mỗi khung hình.
     * @return true nếu đạn còn trong màn hình, false nếu ra ngoài
     */
    @Override
    public boolean update() {
        x += dx;
        y += dy;
        return x >= -20 && x <= 1300 && y <= 740;
    }

    /**
     * Vẽ đạn Boss Siêu Cấp lên màn hình.
     */
    @Override
    public void render(GraphicsContext gc) {
        gc.drawImage(Assets.superBossBullet, x, y, 20, 40);
    }
}
