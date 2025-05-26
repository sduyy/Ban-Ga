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

/**
 * Mục lựa chọn trong menu (ví dụ: "NEW GAME", "EXIT", "CONTINUE"...).
 * Hỗ trợ hiệu ứng highlight, nhấp nháy khi được chọn và hành động khi kích hoạt.
 */
public class MenuItem extends HBox {

    /** Font chữ cho văn bản menu. */
    private static final Font FONT = Font.loadFont(MenuItem.class.getResourceAsStream("/fonts/Pixel Emulator.otf"), 20);

    /** Hiệu ứng nhấp nháy (fade) áp dụng cho menu đang được chọn. */
    private static FadeTransition blink;

    /** Âm thanh khi click chọn mục menu. */
    private static final SoundFX UIClick = new SoundFX("uiclick.wav");

    /** Ngôi sao bên trái. */
    private final Star s1 = new Star();
    /** Ngôi sao bên phải. */
    private final Star s2 = new Star();
    /** Văn bản hiển thị tên mục menu. */
    private final Text text;
    /** Hành động thực thi khi mục được chọn. */
    private Runnable script;

    /**
     * Khởi tạo một MenuItem với tên hiển thị.
     * @param name tên hiển thị trên menu
     */
    public MenuItem(String name) {
        super(15);
        setAlignment(Pos.CENTER);

        text = new Text(name);
        text.setFont(FONT);
        text.setEffect(new GaussianBlur(2));

        getChildren().addAll(s1, text, s2);
        setActive(false);

        setOnActivate(() -> System.out.println(name + " activated"));

        setOnMouseEntered(e -> {
            for (Node node : ((VBox) getParent()).getChildren()) {
                if (node instanceof MenuItem mi) {
                    mi.setActive(false);
                }
            }
            setActive(true);
        });

        setOnMouseClicked(e -> {
            UIClick.play();
            activate();
        });
    }

    /**
     * Thiết lập hành động khi mục được kích hoạt.
     * @param r đoạn mã thực thi
     */
    public void setOnActivate(Runnable r) {
        script = r;
    }

    /**
     * Kích hoạt mục menu: thực thi hành động kèm hiệu ứng nhấp nháy.
     */
    public void activate() {
        flashOnce();
        if (script != null) script.run();
    }

    /**
     * Bật/tắt trạng thái được chọn của mục menu.
     * @param b true nếu được chọn (highlight), false nếu không
     */
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
     * Tạo hiệu ứng nhấp nháy mạnh 1 lần khi mục được chọn.
     */
    public void flashOnce() {
        FadeTransition flash = new FadeTransition(Duration.seconds(0.1), text);
        flash.setFromValue(1.0);
        flash.setToValue(0.2);
        flash.setCycleCount(6);
        flash.setAutoReverse(true);
        flash.play();
    }

    /**
     * Hình sao hiển thị bên cạnh văn bản menu khi mục được chọn.
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
                double angle = Math.toRadians(i * 36);
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
