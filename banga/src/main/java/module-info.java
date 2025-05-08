module com.banga {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;
    opens com.banga to javafx.fxml;
    exports com.banga;
}