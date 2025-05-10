module com.example.spaceshooter {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;

    opens com.example.spaceshooter to javafx.fxml;
    exports com.example.spaceshooter;
}
