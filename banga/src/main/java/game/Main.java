package game;

import javafx.application.Application;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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
            Image image = new Image(getClass().getResourceAsStream("/player.png"));
            ImageView iView = new ImageView(image);
            root.setCenter(iView);
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
