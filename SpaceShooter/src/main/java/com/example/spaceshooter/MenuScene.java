package com.example.spaceshooter;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class MenuScene {
    public static void showMenu(Stage stage) {
        Label title = new Label("SPACE SHOOTER");
        title.setFont(new Font("Arial", 36));
        title.setTextFill(Color.WHITE);

        Button startButton = new Button("Start Game");
        startButton.setFont(new Font(18));

        ToggleButton toggleMouse = new ToggleButton("Mouse Control: ON");
        toggleMouse.setSelected(true);
        toggleMouse.setFont(new Font(14));

        toggleMouse.setOnAction(e -> {
            Main.useMouseControl = toggleMouse.isSelected();
            toggleMouse.setText("Mouse Control: " + (Main.useMouseControl ? "ON" : "OFF"));
        });

        startButton.setOnAction(e -> GameScene.startGame());

        VBox layout = new VBox(20, title, toggleMouse, startButton);
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color: black;");

        Scene menuScene = new Scene(layout, 800, 600);
        stage.setScene(menuScene);
        stage.setTitle("Main Menu");
        stage.show();
    }
}
