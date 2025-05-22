
package com.example.spaceshooter;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

public class StartScreen {
    private static final Font FONT = Font.loadFont(StartScreen.class.getResourceAsStream("/fonts/Pixel Emulator.otf"), 20);
    private static final Font TITLE_FONT = Font.loadFont(StartScreen.class.getResourceAsStream("/fonts/Pixel Emulator.otf"), 50);

    private StackPane root;
    private StackPane uiLayer;
    private VBox menuBox;
    private static int currentItem = 0;
    private VBox instructionBox;
    private ScheduledExecutorService bgThread = Executors.newSingleThreadScheduledExecutor();

    /**
     * Show the animated menu screen.
     */
    public static void showMenu(Stage stage) {
        try {
            StartScreen screen = new StartScreen();
            screen.start(stage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Parent createContent() {
        // Video background.
        String videoPath = getClass().getResource("/assets/video/space_pixel_background.mp4").toExternalForm();
        Media media = new Media(videoPath);
        MediaPlayer mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        mediaPlayer.setMute(true);
        mediaPlayer.play();
        MediaView mediaView = new MediaView(mediaPlayer);
        mediaView.setFitWidth(1280);
        mediaView.setFitHeight(720);
        mediaView.setPreserveRatio(false);

        // Frame chứa tên game.
        ContentFrame frame1 = new ContentFrame(createMiddleContent());

        MenuItem newGame = new MenuItem("NEW GAME");
        newGame.setOnActivate(() -> {
            GameScene.resetGameState();
            GameScene.startGame();       
        });

        // Lựa chọn thoát game, có hiệu ứng thoát các kiểu.
        MenuItem itemExit = new MenuItem("EXIT");
        itemExit.setOnActivate(() -> {
            MenuItem thisItem = getMenuItem(currentItem);
            thisItem.flashOnce();

            FadeTransition fadeUI = new FadeTransition(Duration.seconds(1.0), uiLayer);
            fadeUI.setFromValue(1.0);
            fadeUI.setToValue(0.0);

            fadeUI.setOnFinished(e -> {
                Rectangle blackFade = new Rectangle(1280, 720, Color.BLACK);
                blackFade.setOpacity(0);
                root.getChildren().add(blackFade);

                FadeTransition fadeToBlack = new FadeTransition(Duration.seconds(1.5), blackFade);
                fadeToBlack.setFromValue(0);
                fadeToBlack.setToValue(1);
                fadeToBlack.setOnFinished(ev -> System.exit(0));
                fadeToBlack.play();
            });

            fadeUI.play();
        });

        // Hộp Instruction.
        MenuItem instructionsItem = new MenuItem("INSTRUCTIONS");
        instructionsItem.setOnActivate(() -> {
            instructionBox.setVisible(true);
        });

        // Menu lựa chọn.
        menuBox = new VBox(10,
                newGame,
                new MenuItem("CONTINUE GAME"),
                instructionsItem,
                new MenuItem("CREDITS"),
                itemExit);
        menuBox.setAlignment(Pos.CENTER);

        getMenuItem(0).setActive(true);

        VBox layout = new VBox(50, frame1, menuBox);
        layout.setAlignment(Pos.CENTER);

        uiLayer = new StackPane(layout);
        uiLayer.setPickOnBounds(false);

        root = new StackPane();
        root.setPrefSize(1280, 720);

        Rectangle bg = new Rectangle(1280, 720);
        bg.setFill(Color.BLACK);
        bg.widthProperty().bind(root.widthProperty());
        bg.heightProperty().bind(root.heightProperty());

        // Instructions
        instructionBox = new VBox(10);
        instructionBox.setAlignment(Pos.CENTER);
        instructionBox.setStyle("-fx-background-color: rgba(0,0,0,0.85); -fx-padding: 40; -fx-background-radius: 15;");
        instructionBox.setVisible(false);

        Text title = new Text("HOW TO PLAY");
        title.setFont(TITLE_FONT);
        title.setFill(Color.WHITE);

        Text line1 = new Text("Use arrow keys or mouse to move the ship");
        Text line2 = new Text("SPACE or Left Click to shoot");
        Text line3 = new Text("Dodge bullets and eliminate all hostile enemies");
        Text line4 = new Text("");
        Text line5 = new Text("Press ESCAPE to close Instructions");

        for (Text line : new Text[]{line1, line2, line3, line4, line5}) {
            line.setFont(FONT);
            line.setFill(Color.LIGHTGRAY);
        }

        VBox content = new VBox(10, title, line1, line2, line3, line4, line5);
        content.setAlignment(Pos.CENTER);

        instructionBox.getChildren().addAll(content);
        uiLayer.getChildren().add(instructionBox);

        root.getChildren().addAll(mediaView, uiLayer);
        return root;
    }

    private Node createMiddleContent() {
        String title = "SPACE SHOOTER";
        HBox letters = new HBox(0);
        letters.setAlignment(Pos.CENTER);
        for (int i = 0; i < title.length(); i++) {
            Text letter = new Text(title.charAt(i) + "");
            letter.setFont(TITLE_FONT);
            letter.setFill(Color.WHITE);
            letters.getChildren().add(letter);

            TranslateTransition tt = new TranslateTransition(Duration.seconds(2), letter);
            tt.setDelay(Duration.millis(i * 50));
            tt.setToY(-25);
            tt.setAutoReverse(true);
            tt.setCycleCount(TranslateTransition.INDEFINITE);
            tt.play();
        }

        return letters;
    }

    private MenuItem getMenuItem(int index) {
        return (MenuItem)menuBox.getChildren().get(index);
    }

    private static class ContentFrame extends StackPane {
        public ContentFrame(Node content) {
            setAlignment(Pos.CENTER);

            Rectangle frame = new Rectangle(200, 200);
            frame.setArcWidth(25);
            frame.setArcHeight(25);
            frame.setStroke(Color.TRANSPARENT);

            getChildren().addAll(frame, content);
        }
    }

    public void start(Stage primaryStage) throws Exception {
        Scene scene = new Scene(createContent(), 1280, 720);

        scene.setOnKeyPressed(event -> {
            if (instructionBox.isVisible()) {
                if (event.getCode() == KeyCode.ESCAPE) {
                    instructionBox.setVisible(false);
                }
                return;
            }

            if (event.getCode() == KeyCode.UP) {
                if (currentItem > 0) {
                    getMenuItem(currentItem).setActive(false);
                    getMenuItem(--currentItem).setActive(true);
                }
            }

            if (event.getCode() == KeyCode.DOWN) {
                if (currentItem < menuBox.getChildren().size() - 1) {
                    getMenuItem(currentItem).setActive(false);
                    getMenuItem(++currentItem).setActive(true);
                }
            }

            if (event.getCode() == KeyCode.ENTER) {
                SoundFX UIClick = new SoundFX("uiclick.wav");
                UIClick.play();
                MenuItem selected = getMenuItem(currentItem);
                selected.flashOnce();
                selected.activate();
            }
        });

        primaryStage.setTitle("Space Shooter");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.setOnCloseRequest(event -> {
            bgThread.shutdownNow();
        });
        primaryStage.show();
    }
}
