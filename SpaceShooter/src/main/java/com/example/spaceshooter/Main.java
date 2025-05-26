package com.example.spaceshooter;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.media.MediaPlayer;

/**
 * Lớp khởi động ứng dụng JavaFX, chứa phương thức main và thiết lập giao diện ban đầu.
 */
public class Main extends Application {

    /** Cho phép bật/tắt chế độ điều khiển bằng chuột (true = dùng chuột, false = dùng bàn phím). */
    public static boolean useMouseControl = true;

    /** Stage chính của ứng dụng (cửa sổ chính). */
    public static Stage mainStage;

    /** MediaPlayer dùng chung cho video nền (hiện chưa sử dụng lại, nhưng có thể mở rộng). */
    public static MediaPlayer sharedMediaPlayer;

    /**
     * Điểm vào chính của JavaFX: được gọi khi ứng dụng khởi động.
     * Thiết lập stage ban đầu và hiển thị màn hình StartScreen.
     *
     * @param stage Stage chính do JavaFX cung cấp
     */
    @Override
    public void start(Stage stage) {
        mainStage = stage;
        StartScreen.showMenu(stage);
    }

    /**
     * Phương thức main của chương trình: khởi chạy ứng dụng JavaFX.
     *
     * @param args tham số dòng lệnh (không dùng)
     */
    public static void main(String[] args) {
        launch(args);
    }
}
