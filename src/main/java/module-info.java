module com.lms {
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.jfoenix;
    requires jdk.xml.dom;
    requires json.simple;
    requires org.json;
    requires jdk.jfr;
    requires java.sql;
    requires java.management;

    opens com.lms to javafx.fxml;
    exports com.lms;

    opens classes to javafx.fxml;
    exports classes;

    opens Application to javafx.fxml;
    exports Application;

    opens Controller to javafx.fxml;
    exports Controller;

    opens Model to javafx.fxml;
    exports Model;
}