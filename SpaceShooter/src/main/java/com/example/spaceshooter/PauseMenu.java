package com.example.spaceshooter;

import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;

/**
 * Màn hình menu hiển thị khi tạm dừng trò chơi (Resume, Restart, Exit to Menu).
 */
public class PauseMenu extends StackPane {
    /** Danh sách chứa các nút lựa chọn trong menu tạm dừng. */
    private VBox menuItems;
    /** Trình phát video nền của màn hình tạm dừng. */
    private MediaPlayer mediaPlayer;

    /**
     * Tạo menu tạm dừng với các hành động tương ứng cho Resume, Restart, Exit.
     * @param onResume hành động khi chọn tiếp tục chơi (Resume)
     * @param onRestart hành động khi chọn chơi lại màn hiện tại (Restart)
     * @param onSettings hành động khi chọn mở cài đặt (Settings) — hiện chưa sử dụng
     * @param onExit hành động khi chọn thoát về menu chính (Exit to Menu)
     */
    public PauseMenu(Runnable onResume, Runnable onRestart, Runnable onSettings, Runnable onExit) {
        setAlignment(Pos.CENTER);
        setPrefSize(1280, 720);

        // Tạo video nền
        String path = getClass().getResource("/assets/video/space_pixel_background.mp4").toExternalForm();
        Media media = new Media(path);
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        mediaPlayer.setAutoPlay(true);
        mediaPlayer.setMute(true);

        MediaView mediaView = new MediaView(mediaPlayer);
        mediaView.setFitWidth(1280);
        mediaView.setFitHeight(720);
        mediaView.setPreserveRatio(false);

        // Tạo các mục menu
        MenuItem resumeBtn = new MenuItem("Resume");
        MenuItem restartBtn = new MenuItem("Restart");
        MenuItem exitBtn = new MenuItem("Exit to Menu");

        menuItems = new VBox(10);
        menuItems.setAlignment(Pos.CENTER);
        menuItems.getChildren().addAll(resumeBtn, restartBtn, exitBtn);

        // Đặt mặc định nút đầu tiên được chọn
        resumeBtn.setActive(true);

        // Gán hành động cho các nút
        resumeBtn.setOnActivate(() -> {
            stopMedia();
            onResume.run();
        });

        restartBtn.setOnActivate(() -> {
            stopMedia();
            onRestart.run();
        });

        exitBtn.setOnActivate(() -> {
            stopMedia();
            onExit.run();
        });

        getChildren().addAll(mediaView, menuItems);
    }

    /**
     * Dừng và giải phóng tài nguyên video nền.
     */
    private void stopMedia() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose();
        }
    }

    /**
     * Lấy mục menu tại chỉ số cho trước.
     * @param index vị trí trong danh sách menu
     * @return đối tượng MenuItem tương ứng
     */
    private MenuItem getMenuItem(int index) {
        return (MenuItem) menuItems.getChildren().get(index);
    }
}
