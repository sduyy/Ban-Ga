package com.example.spaceshooter;

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

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Màn hình chính khi bắt đầu trò chơi. Hiển thị logo game, menu và nền video động.
 */
public class StartScreen {
    public static final Font FONT = Font.loadFont(StartScreen.class.getResourceAsStream("/fonts/Pixel Emulator.otf"), 20);
    public static final Font TITLE_FONT = Font.loadFont(StartScreen.class.getResourceAsStream("/fonts/Pixel Emulator.otf"), 50);

    private StackPane root;
    private StackPane uiLayer;
    private VBox menuBox;
    private static int currentItem = 0;
    private VBox instructionBox;
    private VBox creditsBox;
    private ScheduledExecutorService bgThread = Executors.newSingleThreadScheduledExecutor();

    /**
     * Hiển thị màn hình menu chính của game.
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
        // Video nền
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

        // Tiêu đề game với hiệu ứng chữ nhảy
        ContentFrame frame1 = new ContentFrame(createAnimatedTitle("SPACE SHOOTER"));

        // Các lựa chọn menu
        MenuItem newGame = new MenuItem("NEW GAME");
        newGame.setOnActivate(() -> {
            ChoosePlay choosePlay = new ChoosePlay(() -> {
                GameScene.isTwoPlayerMode = false;
                GameScene.resetGameState();
                GameScene.startGame();
            }, () -> {
                GameScene.isTwoPlayerMode = true;
                GameScene.resetGameState();
                GameScene.startGame();
            });

            Scene choosePlayScene = new Scene(choosePlay, 1280, 720);
            Main.mainStage.setScene(choosePlayScene);
        });

        MenuItem instructionsItem = new MenuItem("INSTRUCTIONS");
        instructionsItem.setOnActivate(() -> instructionBox.setVisible(true));

        MenuItem credits = new MenuItem("CREDITS");
        credits.setOnActivate(() -> creditsBox.setVisible(true));

        MenuItem exit = new MenuItem("EXIT");
        exit.setOnActivate(() -> {
            MenuItem current = getMenuItem(currentItem);
            current.flashOnce();

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

        // Menu tổng
        menuBox = new VBox(10, newGame, instructionsItem, credits, exit);
        menuBox.setAlignment(Pos.CENTER);
        getMenuItem(0).setActive(true);

        VBox layout = new VBox(50, frame1, menuBox);
        layout.setAlignment(Pos.CENTER);
        uiLayer = new StackPane(layout);
        uiLayer.setPickOnBounds(false);

        root = new StackPane();
        root.setPrefSize(1280, 720);
        root.getChildren().addAll(mediaView, uiLayer);

        buildInstructionBox();
        buildCreditsBox();

        root.getChildren().addAll(instructionBox, creditsBox);
        return root;
    }

    private void buildInstructionBox() {
        instructionBox = new VBox(10);
        instructionBox.setAlignment(Pos.CENTER);
        instructionBox.setStyle("-fx-background-color: rgba(0,0,0,0.85); -fx-padding: 40; -fx-background-radius: 15;");
        instructionBox.setVisible(false);

        Text title = new Text("HOW TO PLAY");
        title.setFont(TITLE_FONT);
        title.setFill(Color.WHITE);

        String[] lines = {
                "1 Player", "Move: WASD or Mouse", "Shoot: SPACE or Left Click, Missile: M or Middle Click", "",
                "2 Players", "Move: P1: WASD - P2: Arrow Keys",
                "Shoot: P1: SPACE / M | P2: ENTER / SHIFT", "",
                "Dodge bullets and eliminate all enemies", "", "Press ESC to close"
        };

        VBox content = new VBox(10);
        content.setAlignment(Pos.CENTER);
        content.getChildren().add(title);
        for (String line : lines) {
            Text t = new Text(line);
            t.setFont(FONT);
            t.setFill(Color.LIGHTGRAY);
            content.getChildren().add(t);
        }

        instructionBox.getChildren().add(content);
    }

    private void buildCreditsBox() {
        creditsBox = new VBox(10);
        creditsBox.setAlignment(Pos.CENTER);
        creditsBox.setStyle("-fx-background-color: rgba(0,0,0,0.85); -fx-padding: 40; -fx-background-radius: 15;");
        creditsBox.setVisible(false);

        Text title = new Text("PROJECT BY");
        title.setFont(TITLE_FONT);
        title.setFill(Color.WHITE);

        String[] credits = {
                "NGUYEN VIET DUC", "NGUYEN SON DUY", "NGUYEN DUY HUNG", "", "Press ESC to close"
        };

        VBox content = new VBox(10);
        content.setAlignment(Pos.CENTER);
        content.getChildren().add(title);
        for (String name : credits) {
            Text t = new Text(name);
            t.setFont(FONT);
            t.setFill(Color.LIGHTGRAY);
            content.getChildren().add(t);
        }

        creditsBox.getChildren().add(content);
    }

    private Node createAnimatedTitle(String title) {
        HBox letters = new HBox(0);
        letters.setAlignment(Pos.CENTER);
        for (int i = 0; i < title.length(); i++) {
            Text letter = new Text(String.valueOf(title.charAt(i)));
            letter.setFont(TITLE_FONT);
            letter.setFill(Color.WHITE);

            TranslateTransition tt = new TranslateTransition(Duration.seconds(2), letter);
            tt.setDelay(Duration.millis(i * 50));
            tt.setToY(-25);
            tt.setAutoReverse(true);
            tt.setCycleCount(TranslateTransition.INDEFINITE);
            tt.play();

            letters.getChildren().add(letter);
        }
        return letters;
    }

    private MenuItem getMenuItem(int index) {
        return (MenuItem) menuBox.getChildren().get(index);
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

    /**
     * Khởi chạy giao diện menu chính.
     */
    public void start(Stage primaryStage) {
        Scene scene = new Scene(createContent(), 1280, 720);

        scene.setOnKeyPressed(event -> {
            if (instructionBox.isVisible() && event.getCode() == KeyCode.ESCAPE) {
                instructionBox.setVisible(false);
                return;
            }
            if (creditsBox.isVisible() && event.getCode() == KeyCode.ESCAPE) {
                creditsBox.setVisible(false);
                return;
            }

            switch (event.getCode()) {
                case UP -> {
                    if (currentItem > 0) {
                        getMenuItem(currentItem).setActive(false);
                        getMenuItem(--currentItem).setActive(true);
                    }
                }
                case DOWN -> {
                    if (currentItem < menuBox.getChildren().size() - 1) {
                        getMenuItem(currentItem).setActive(false);
                        getMenuItem(++currentItem).setActive(true);
                    }
                }
                case ENTER -> {
                    SoundFX UIClick = new SoundFX("uiclick.wav");
                    UIClick.play();
                    MenuItem selected = getMenuItem(currentItem);
                    selected.flashOnce();
                    selected.activate();
                }
            }
        });

        primaryStage.setTitle("Space Shooter");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.setOnCloseRequest(e -> bgThread.shutdownNow());
        primaryStage.show();
    }
}
