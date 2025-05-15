package com.example.spaceshooter;

import javafx.animation.FadeTransition;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class MenuItem extends HBox {
    private static final Font FONT = Font.loadFont(MenuItem.class.getResourceAsStream("/fonts/Pixel Emulator.otf"), 20);
    private static FadeTransition blink;
    private static int currentItem = 0;

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

    // Hiệu ứng khi chọn.
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
        currentItem = ((VBox) getParent()).getChildren().indexOf(this);
        flashOnce();
        if (script != null)
            script.run();
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
}


