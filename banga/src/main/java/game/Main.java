package game;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class Main extends Application{
    @Override
    public void start(Stage primaryStage) {
        BorderPane root = new BorderPane();
        Scene scene = new Scene(root, 800, 500);
        root.setCenter(new Button("HUNG GAY"));
        primaryStage.setScene(scene);
        primaryStage.setTitle("Game Ban Ga");
        primaryStage.show();

    }
    public static void main(String[] args) {
        launch(args);
    }
}
