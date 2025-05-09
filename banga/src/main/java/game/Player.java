package game;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Player {
    private ImageView imageView;
    private double moveDelta = 10;

    public Player(String imagePath) {
        Image image = new Image(getClass().getResourceAsStream(imagePath));
        imageView = new ImageView(image);

        imageView.setFitWidth(60);  // Chiều rộng mong muốn
        imageView.setFitHeight(60); // Chiều cao mong muốn
        imageView.setPreserveRatio(true); // Giữ tỷ lệ khung hình
    }
    // Trả về ImageView để add vào scene
    public ImageView getImageView() {
        return imageView;
    }

    // set  ImageView ở giữa
    public void setToBottomCenter(double sceneWidth, double sceneHeight) {
        double imageWidth = imageView.getFitWidth();
        double imageHeight = imageView.getFitHeight();

        imageView.setLayoutX((sceneWidth - imageWidth) / 2);
        imageView.setLayoutY(sceneHeight - imageHeight);
    }

    public void setPosition(double x, double y) {
        imageView.setLayoutX(x);
        imageView.setLayoutY(y);
    }

    public double getWidth() {
        return imageView.getFitWidth();
    }

    public double getHeight() {
        return imageView.getFitHeight();
    }



    // // Di chuyển dựa trên phím nhấn
    // public void move(KeyCode code) {
    //     switch (code) {
    //         case LEFT:
    //             imageView.setLayoutX(imageView.getLayoutX() - moveDelta);
    //             break;
    //         case RIGHT:
    //             imageView.setLayoutX(imageView.getLayoutX() + moveDelta);
    //             break;
    //         case UP:
    //             imageView.setLayoutY(imageView.getLayoutY() - moveDelta);
    //             break;
    //         case DOWN:
    //             imageView.setLayoutY(imageView.getLayoutY() + moveDelta);
    //             break;
    //         default:
    //             break;
    //     }
    // }
    
}
