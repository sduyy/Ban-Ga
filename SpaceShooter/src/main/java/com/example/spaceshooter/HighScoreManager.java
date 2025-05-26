package com.example.spaceshooter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * Lớp quản lý việc lưu và đọc điểm cao nhất (High Score) của trò chơi.
 * Dữ liệu được lưu trong tệp văn bản đơn giản trên ổ đĩa.
 */
public class HighScoreManager {

    /** Đường dẫn đến tệp lưu điểm cao nhất. */
    private static final String FILE_PATH = "highscore.txt";

    /**
     * Đọc điểm cao nhất từ tệp lưu.
     * Nếu tệp không tồn tại hoặc có lỗi khi đọc/parsing, trả về 0.
     *
     * @return điểm cao nhất (hoặc 0 nếu không có hoặc lỗi)
     */
    public static int getHighScore() {
        try {
            if (!Files.exists(Paths.get(FILE_PATH))) {
                return 0;
            }
            String content = Files.readString(Paths.get(FILE_PATH)).trim();
            return Integer.parseInt(content);
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * Ghi điểm cao mới vào tệp.
     * Ghi đè nếu tệp đã tồn tại, tạo mới nếu chưa có.
     *
     * @param score điểm số cần lưu
     */
    public static void saveHighScore(int score) {
        try {
            Files.writeString(
                    Paths.get(FILE_PATH),
                    String.valueOf(score),
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
