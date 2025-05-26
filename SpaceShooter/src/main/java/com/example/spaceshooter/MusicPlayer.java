package com.example.spaceshooter;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

/**
 * Hỗ trợ phát nhạc nền (với khả năng lặp lại) cho trò chơi.
 */
public class MusicPlayer {
    /** Đối tượng MediaPlayer phát nhạc nền. */
    private MediaPlayer player;

    /**
     * Tạo trình phát nhạc nền từ tên tệp nhạc.
     * @param fileName tên tệp nhạc (trong thư mục /assets/music)
     */
    public MusicPlayer(String fileName) {
        String path = getClass().getResource("/assets/music/" + fileName).toExternalForm();
        Media media = new Media(path);
        player = new MediaPlayer(media);
        player.setCycleCount(MediaPlayer.INDEFINITE); // phát lặp vô hạn
        player.setVolume(0.2); // âm lượng mặc định
    }

    /**
     * Bắt đầu phát nhạc nền (từ đầu hoặc tiếp tục).
     */
    public void play() {
        player.play();
    }

    /**
     * Dừng nhạc nền (và đặt về thời điểm bắt đầu).
     */
    public void stop() {
        player.stop();
    }

    /**
     * Tạm dừng nhạc nền (có thể tiếp tục bằng play()).
     */
    public void pause() {
        player.pause();
    }

    /**
     * Thiết lập âm lượng phát nhạc nền.
     * @param volume giá trị từ 0.0 (tắt tiếng) đến 1.0 (tối đa)
     */
    public void setVolume(double volume) {
        player.setVolume(volume);
    }
}
