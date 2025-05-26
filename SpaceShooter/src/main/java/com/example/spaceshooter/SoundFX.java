package com.example.spaceshooter;

import javafx.scene.media.AudioClip;

/**
 * Lớp hỗ trợ phát hiệu ứng âm thanh ngắn trong trò chơi (ví dụ: bắn, nổ, nhặt vật phẩm).
 */
public class SoundFX {
    /** Đối tượng âm thanh được tải và sử dụng để phát. */
    private AudioClip clip;

    /**
     * Tạo hiệu ứng âm thanh từ tên tệp trong thư mục /assets/sounds/.
     * @param fileName tên tệp âm thanh, ví dụ: "laser.wav"
     */
    public SoundFX(String fileName) {
        try {
            String path = getClass().getResource("/assets/sounds/" + fileName).toExternalForm();
            clip = new AudioClip(path);
        } catch (Exception e) {
            System.err.println("Failed to load sound: " + fileName);
            e.printStackTrace();
        }
    }

    /**
     * Phát âm thanh một lần (không lặp).
     */
    public void play() {
        if (clip != null) {
            clip.play();
        }
    }
}
