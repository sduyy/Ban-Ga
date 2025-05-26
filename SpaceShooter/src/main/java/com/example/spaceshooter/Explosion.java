package com.example.spaceshooter;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Hiệu ứng vụ nổ (khi tàu hoặc đạn nổ tung).
 */
public class Explosion {
    /** Tọa độ X của tâm vụ nổ. */
    private double x;
    /** Tọa độ Y của tâm vụ nổ. */
    private double y;
    /** Số khung hình còn lại mà vụ nổ sẽ hiển thị (tuổi thọ của hiệu ứng nổ). */
    private int duration = 15;
    /** Bán kính của vụ nổ (độ lớn hiệu ứng vòng tròn). */
    private int radius;

    /**
     * Tạo hiệu ứng nổ tại vị trí (x, y) với bán kính mặc định.
     * @param x tọa độ X của tâm vụ nổ
     * @param y tọa độ Y của tâm vụ nổ
     */
    public Explosion(double x, double y) {
        this(x, y, 40);
    }

    /**
     * Tạo hiệu ứng nổ tại vị trí (x, y) với bán kính tùy chỉnh.
     * @param x tọa độ X của tâm vụ nổ
     * @param y tọa độ Y của tâm vụ nổ
     * @param radius bán kính vụ nổ
     */
    public Explosion(double x, double y, int radius) {
        this.x = x;
        this.y = y;
        this.radius = radius;
    }

    /**
     * Vẽ hiệu ứng vụ nổ (các vòng tròn đồng tâm màu cam, vàng, trắng) lên màn hình.
     * Mỗi lần vẽ sẽ giảm duration đi 1 để đếm ngược thời gian tồn tại hiệu ứng.
     * @param gc GraphicsContext để vẽ hiệu ứng nổ
     */
    public void render(GraphicsContext gc) {
        // Vòng ngoài - màu đỏ cam
        gc.setFill(Color.ORANGERED);
        gc.fillOval(x, y, radius, radius);

        // Vòng giữa - màu vàng
        gc.setFill(Color.YELLOW);
        gc.fillOval(x + radius * 0.2, y + radius * 0.2, radius * 0.6, radius * 0.6);

        // Vòng trong - màu trắng
        gc.setFill(Color.WHITE);
        gc.fillOval(x + radius * 0.35, y + radius * 0.35, radius * 0.3, radius * 0.3);

        // Giảm thời lượng còn lại
        duration--;
    }

    /**
     * Kiểm tra xem hiệu ứng nổ đã kết thúc chưa.
     * @return true nếu duration <= 0
     */
    public boolean isFinished() {
        return duration <= 0;
    }
}
