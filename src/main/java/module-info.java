module com.lms {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires java.sql;
    requires json.simple;
    requires junit;

    opens com.lms to javafx.fxml;
    exports com.lms;

    opens classes to javafx.fxml;
    exports classes;
}