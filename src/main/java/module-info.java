module com.lms {
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
<<<<<<< HEAD
    requires com.jfoenix;
    requires jdk.xml.dom;
    requires json.simple;
    requires org.json;
    requires jdk.jfr;
=======
    requires java.sql;
>>>>>>> dev-main-feature


    opens classes to javafx.fxml;
    exports classes;
}