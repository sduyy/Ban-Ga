package com.example.spaceshooter;

import java.io.*;
import java.nio.file.*;

public class HighScoreManager {
    private static final String FILE_PATH = "highscore.txt";

    public static int getHighScore() {
        try {
            if (!Files.exists(Paths.get(FILE_PATH))) return 0;
            return Integer.parseInt(Files.readString(Paths.get(FILE_PATH)).trim());
        } catch (IOException | NumberFormatException e) {
            return 0;
        }
    }

    public static void saveHighScore(int score) {
        try {
            Files.writeString(Paths.get(FILE_PATH), String.valueOf(score), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
