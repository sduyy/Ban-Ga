package game;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;

public class Main extends Application {

    private static final Font FONT = Font.loadFont(Main.class.getResourceAsStream("/fonts/Pixel Emulator.otf"), 20); // Font chữ bthuong.
    private static final Font TITLE_FONT = Font.loadFont(Main.class.getResourceAsStream("/fonts/Pixel Emulator.otf"), 50); // Font chữ cho tên game.

    private VBox menuBox; // Cái này để các lựa chọn.
    private static int currentItem = 0; // Lựa chọn hiện tại.

    private static FadeTransition blink; // Nhấp nháy.

    private ScheduledExecutorService bgThread = Executors.newSingleThreadScheduledExecutor();

    /**
     * Phần này để tạo tất cả các cái có trong menu.
     */
    private Parent createContent() {
        StackPane root = new StackPane();
        root.setPrefSize(1280, 720); // Resolution.

        Rectangle bg = new Rectangle(1280, 720); // Background.
        bg.setFill(Color.BLACK);
        // Bind kích thước background theo root.
        bg.widthProperty().bind(root.widthProperty());
        bg.heightProperty().bind(root.heightProperty());

        String videoPath = getClass().getResource("/videos/space_pixel_background.mp4").toExternalForm();
        Media media = new Media(videoPath);
        MediaPlayer mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE); // Lặp vô hạn
        mediaPlayer.setMute(true); // Nếu không cần âm thanh
        mediaPlayer.play();

        MediaView mediaView = new MediaView(mediaPlayer);
        mediaView.setFitWidth(1280);
        mediaView.setFitHeight(720);
        mediaView.setPreserveRatio(false);

        // Frame chứa tên game.
        ContentFrame frame1 = new ContentFrame(createMiddleContent());

        // Thoát game.
        MenuItem itemExit = new MenuItem("EXIT");
        itemExit.setOnActivate(() -> System.exit(0));

        // Menu lựa chọn.
        menuBox = new VBox(10,
                new MenuItem("NEW GAME"),
                new MenuItem("CONTINUE GAME"),
                new MenuItem("INSTRUCTIONS"),
                new MenuItem("CREDITS"),
                itemExit);
        menuBox.setAlignment(Pos.CENTER);

        // Cho cái lựa chọn đầu tiên sáng.
        getMenuItem(0).setActive(true);

        // Gộp frame và menuBox vào VBox để xếp dọc.
        VBox layout = new VBox(50, frame1, menuBox); // 50 là khoảng cách giữa 2 phần.
        layout.setAlignment(Pos.CENTER);

        root.getChildren().addAll(mediaView, layout);
        return root;
    }

    /**
     * Khung tên game.
     */
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

    /**
     * Khung chứa tên game.
     */
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
     * Các lựa chọn.
     */
    private static class MenuItem extends HBox {
        private Star s1 = new Star(), s2 = new Star();
        private Text text;
        private Runnable script;

        public MenuItem(String name) {
            super(15);
            setAlignment(Pos.CENTER);

            text = new Text(name);
            text.setFont(FONT);
            text.setEffect(new GaussianBlur(2));

            getChildren().addAll(s1, text, s2);
            setActive(false);
            setOnActivate(() -> System.out.println(name + " activated"));

            // Hover bằng chuột sáng lên.
            setOnMouseEntered(e -> {
                for (Node node : ((VBox) getParent()).getChildren()) {
                    ((MenuItem) node).setActive(false);
                }
                setActive(true);
                currentItem = ((VBox) getParent()).getChildren().indexOf(this);
            });

            // Click chuột.
            setOnMouseClicked(e -> activate());
        }

        public void setActive(boolean b) {
            s1.setVisible(b);
            s2.setVisible(b);

            if (b) {
                text.setFill(Color.WHITE);

                blink = new FadeTransition(Duration.seconds(1.75), text);
                blink.setFromValue(1.0);    
                blink.setToValue(0.7);
                blink.setCycleCount(FadeTransition.INDEFINITE);
                blink.setAutoReverse(true);
                blink.play();
            } else {
                if (blink != null) {
                    blink.stop();
                    text.setOpacity(1.0);
                }
                text.setFill(Color.GREY);
            }
        }

        /**
         * Nhấp nháy mạnh khi được chọn.
         */
        public void flashOnce() {
            FadeTransition flash = new FadeTransition(Duration.seconds(0.1), text);
            flash.setFromValue(1.0);
            flash.setToValue(0.2);
            flash.setCycleCount(6);
            flash.setAutoReverse(true);
            flash.play();
        }

        public void setOnActivate(Runnable r) {
            script = r;
        }

        public void activate() {
            if (script != null)
                script.run();
        }
    }

    /**
     * Hình sao bên cạnh lựa chọn.
     */
    private static class Star extends Parent {
        public Star() {
            Polygon star = new Polygon();
            star.setFill(Color.WHITE);

            double centerX = 0;
            double centerY = 0;
            double radiusOuter = 6;
            double radiusInner = 2.5;

            for (int i = 0; i < 10; i++) {
                double angle = Math.toRadians(i * 36); // 360/10 = 36
                double radius = (i % 2 == 0) ? radiusOuter : radiusInner;
                double x = centerX + radius * Math.cos(angle - Math.PI / 2);
                double y = centerY + radius * Math.sin(angle - Math.PI / 2);
                star.getPoints().addAll(x, y);
            }

            setEffect(new GaussianBlur(2));
            getChildren().add(star);
        }
    }

    /**
     * Start.
     */
    public void start(Stage primaryStage) throws Exception {
        Scene scene = new Scene(createContent(), 1280, 720);

        // Đoạn này là dùng phím lên xuống enter để chọn lựa chọn các thứ
        scene.setOnKeyPressed(event -> {
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

    /**
     * Main launch.
     */
    public static void main(String[] args) {
        launch(args);
    }
}
