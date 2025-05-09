package game;

import javafx.application.Application;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class Main extends Application{

        @Override
    public void start(Stage primaryStage) {
        showMainMenu(primaryStage);
    }

    private void showMainMenu(Stage stage) {
        MainMenu menu = new MainMenu(() -> showGame(stage));
        stage.setScene(menu.getScene());
        stage.setTitle("Game Bắn Gà - Menu");
        stage.show();
    }

    
    public void showGame(Stage stage) {
        try {
            BorderPane root = new BorderPane();
            Scene scene = new Scene(root, 800, 500, Color.BLACK);
            Player player = new Player("/player.png");
            root.getChildren().add(player.getImageView());
            player.setToBottomCenter(scene.getWidth(), scene.getHeight());

            PlayerController playerController = new PlayerController(player);
            
           scene.setOnMouseMoved(event -> {
                playerController.setTarget(event.getX(), event.getY());
            });


            scene.setCursor(Cursor.CROSSHAIR);
            stage.setScene(scene);
            stage.show();

        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
        launch(args);
    }
}
