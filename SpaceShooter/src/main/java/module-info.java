module com.example.spaceshooter {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;
    requires javafx.graphics;

    opens com.example.spaceshooter to javafx.fxml;
    exports com.example.spaceshooter;
}
