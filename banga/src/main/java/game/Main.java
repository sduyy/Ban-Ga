package game;

import javafx.application.Application;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Main extends Application{
    @Override
    public void start(Stage primaryStage) {
        try {
            Stage stage = new Stage(StageStyle.DECORATED);
            stage.setTitle("Game Ban Ga");
            BorderPane root = new BorderPane();
            Scene scene = new Scene(root, 800, 500, Color.BLACK);
            Player player = new Player("/player.png");
            root.getChildren().add(player.getImageView());
            player.setToBottomCenter(scene.getWidth(), scene.getHeight());
            
           scene.setOnMouseMoved(event -> {
            // Lấy tọa độ chuột
            double mouseX = event.getX();
            double mouseY = event.getY();

            // Căn giữa hình ảnh theo chuột
            player.setPosition(mouseX - player.getWidth() / 2, mouseY - player.getHeight() / 2);
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
