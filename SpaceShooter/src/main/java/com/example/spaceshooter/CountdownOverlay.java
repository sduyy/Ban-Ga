package com.example.spaceshooter;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class CountdownOverlay extends StackPane {
    private int countdownTime = 3;
    private final Text countdownText;
    private Timeline timeline;
    private final Runnable onFinish;

    public CountdownOverlay(Runnable onFinish) {
        this.onFinish = onFinish;
        setAlignment(Pos.CENTER);
        setPrefSize(1280, 720); // Kích thước khớp với GameScene

        countdownText = new Text("3");
        countdownText.setFont(Font.loadFont(getClass().getResourceAsStream("/fonts/Pixel Emulator.otf"), 150));
        countdownText.setStyle("-fx-fill: white;");
        getChildren().add(countdownText);
    }

    public void start() {
        countdownTime = 3;
        countdownText.setText(String.valueOf(countdownTime));
        setVisible(true);

        timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            countdownTime--;
            if (countdownTime > 0) {
                countdownText.setText(String.valueOf(countdownTime));
            } else {
                timeline.stop();
                setVisible(false);
                onFinish.run();
            }
        }));
        timeline.setCycleCount(3);
        timeline.playFromStart();
    }
}

