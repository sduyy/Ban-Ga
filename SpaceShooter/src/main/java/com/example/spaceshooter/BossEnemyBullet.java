package com.example.spaceshooter;

import javafx.scene.canvas.GraphicsContext;

/**
 * Lớp đại diện cho đạn do BossEnemy bắn ra (di chuyển theo hướng chỉ định, kích thước lớn).
 */
public class BossEnemyBullet extends EnemyBullet {
    /** Vận tốc theo trục X (ngang) của viên đạn. */
    private double dx;
    /** Vận tốc theo trục Y (dọc) của viên đạn. */
    private double dy;
    /** Chiều rộng của đạn. */
    private final int width = 16;
    /** Chiều cao của đạn. */
    private final int height = 32;

    /**
     * Tạo viên đạn của Boss tại vị trí (x, y) với vận tốc cho trước.
     * @param x tọa độ X ban đầu
     * @param y tọa độ Y ban đầu
     * @param dx vận tốc theo trục X
     * @param dy vận tốc theo trục Y
     */
    public BossEnemyBullet(double x, double y, double dx, double dy) {
        super(x, y);
        this.dx = dx;
        this.dy = dy;
    }

    /**
     * Cập nhật vị trí của đạn mỗi khung hình.
     * @return true nếu đạn còn trong màn hình, false nếu cần loại bỏ
     */
    @Override
    public boolean update() {
        x += dx;
        y += dy;
        return y <= 720;
    }

    /**
     * Vẽ viên đạn Boss lên canvas.
     * @param gc đối tượng GraphicsContext
     */
    @Override
    public void render(GraphicsContext gc) {
        gc.drawImage(Assets.bossBullet, x, y, width, height);
    }

    /**
     * Kiểm tra viên đạn có nằm ngoài màn hình không.
     * @param screenWidth chiều rộng màn hình
     * @param screenHeight chiều cao màn hình
     * @return true nếu đã bay ra ngoài
     */
    public boolean isOffScreen(double screenWidth, double screenHeight) {
        return y > screenHeight || x < -width || x > screenWidth + width;
    }

    /**
     * Kiểm tra va chạm với người chơi.
     * @param p đối tượng Player
     * @return true nếu có va chạm
     */
    @Override
    public boolean collidesWith(Player p) {
        double px = p.getX();
        double py = p.getY();
        return x < px + 40 && x + width > px &&
                y < py + 40 && y + height > py;
    }
}
