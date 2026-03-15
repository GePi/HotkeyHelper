module com.hotkeyhelper {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires com.sun.jna;
    requires com.sun.jna.platform;
    requires com.google.gson;

    exports com.hotkeyhelper;

    opens com.hotkeyhelper.model to com.google.gson;
    opens com.hotkeyhelper.win to com.sun.jna;
}
