package com.example.spaceshooter;

import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;

/**
 * Màn hình để người dùng chọn chế độ chơi: 1 người chơi hoặc 2 người chơi.
 */
public class ChoosePlay extends StackPane {
    /** Trình phát video nền cho màn hình chọn chế độ chơi. */
    private MediaPlayer mediaPlayer;

    /**
     * Khởi tạo màn hình chọn chế độ chơi với hai lựa chọn (1 người chơi, 2 người chơi).
     * @param onPlay1 hành động khi chọn 1 người chơi
     * @param onPlay2 hành động khi chọn 2 người chơi
     */
    public ChoosePlay(Runnable onPlay1, Runnable onPlay2) {
        setAlignment(Pos.CENTER);
        setPrefSize(1280, 720);

        // Tải video nền và cấu hình phát lặp lại (tắt tiếng nếu có âm thanh)
        String path = getClass().getResource("/assets/video/bgspedup4.mp4").toExternalForm();
        Media media = new Media(path);
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        mediaPlayer.setAutoPlay(true);
        mediaPlayer.setMute(true); // Tránh âm thanh nền gây khó chịu

        MediaView mediaView = new MediaView(mediaPlayer);
        mediaView.setFitWidth(1280);
        mediaView.setFitHeight(720);
        mediaView.setPreserveRatio(false);

        // Các tùy chọn chế độ chơi
        MenuItem onePlay = new MenuItem("1 Player");
        MenuItem twoPlay = new MenuItem("2 Players");

        VBox menuItems = new VBox(10);
        menuItems.setAlignment(Pos.CENTER);
        menuItems.getChildren().addAll(onePlay, twoPlay);

        // Gắn sự kiện khi người dùng chọn chế độ
        onePlay.setOnActivate(() -> {
            stopBackgroundVideo();
            onPlay1.run();
        });

        twoPlay.setOnActivate(() -> {
            stopBackgroundVideo();
            onPlay2.run();
        });

        getChildren().addAll(mediaView, menuItems);
    }

    /**
     * Dừng và giải phóng video nền (để tiết kiệm tài nguyên).
     */
    private void stopBackgroundVideo() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose();
        }
    }
}
