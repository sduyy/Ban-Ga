package game;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

public class MainMenu {
    private Scene scene;

    // Đây là constructor cần thiết
    public MainMenu(Runnable onStartGame) {
        VBox menuLayout = new VBox(20);
        menuLayout.setStyle("-fx-background-color: black;");
        menuLayout.setPrefSize(800, 500);
        menuLayout.setAlignment(Pos.CENTER);

        Button startButton = new Button("Bắt đầu chơi");
        Button exitButton = new Button("Thoát");
        
        startButton.setStyle("-fx-font-size: 18px; -fx-padding: 10 20;");
        startButton.setOnAction(e -> onStartGame.run()); // Gọi lại khi bấm
        menuLayout.getChildren().add(startButton);
        exitButton.setOnAction(e -> System.exit(0)); // Thoát game
        exitButton.setStyle("-fx-font-size: 18px; -fx-padding: 10 20;");
        menuLayout.getChildren().add(exitButton);

        
        scene = new Scene(menuLayout);
    }

    public Scene getScene() {
        return scene;
    }
}


