package com.example.spaceshooter;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;

/**
 * Lớp giao diện phủ (overlay) hiển thị đếm ngược 3-2-1-GO! trước khi bắt đầu hoặc tiếp tục trò chơi.
 */
public class CountdownOverlay extends StackPane {
    /** Giá trị đếm ngược hiện tại (tính bằng giây). */
    private int countdownTime = 3;
    /** Đối tượng Text hiển thị số đếm ngược trên màn hình. */
    private final Text countdownText;
    /** Timeline thực hiện hoạt ảnh đếm ngược. */
    private Timeline timeline;
    /** Hành động sẽ được thực hiện sau khi đếm ngược kết thúc. */
    private final Runnable onFinish;

    /**
     * Khởi tạo overlay đếm ngược.
     * @param onFinish hành động được gọi sau khi đếm ngược kết thúc
     */
    public CountdownOverlay(Runnable onFinish) {
        this.onFinish = onFinish;
        setAlignment(Pos.CENTER);
        setPrefSize(1280, 720);

        countdownText = new Text("3");
        countdownText.setFont(Font.loadFont(getClass().getResourceAsStream("/fonts/Pixel Emulator.otf"), 150));
        countdownText.setStyle("-fx-fill: white;");
        getChildren().add(countdownText);
    }

    /**
     * Bắt đầu đếm ngược 3-2-1-GO! và gọi hàm `onFinish` sau đó.
     */
    public void start() {
        countdownTime = 3;
        countdownText.setText(String.valueOf(countdownTime));
        setVisible(true);

        timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            countdownTime--;
            if (countdownTime > 0) {
                countdownText.setText(String.valueOf(countdownTime));
            } else if (countdownTime == 0) {
                countdownText.setText("GO!");
            }
        }));

        timeline.setCycleCount(4); // 3...2...1...GO!
        timeline.setOnFinished(e -> {
            setVisible(false);
            onFinish.run();
        });

        timeline.playFromStart();
    }
}
