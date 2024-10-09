package com.lms;

import javafx.animation.FadeTransition;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.Objects;

public class ControllerUtils {

    public static void switchSceneWithinStage(Node sourceNode, String fxmlFile) throws IOException {
        Stage stage = (Stage) sourceNode.getScene().getWindow();
        Parent root = FXMLLoader.load(Objects.requireNonNull(ControllerUtils.class.getResource(fxmlFile)));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();
    }

    public static void fadeTransition(Node node, double fromOpacity, double toOpacity, double second) {
        FadeTransition loginScreenTransition = new FadeTransition(Duration.seconds(second), node);
        loginScreenTransition.setFromValue(fromOpacity);
        loginScreenTransition.setToValue(toOpacity);
        loginScreenTransition.setCycleCount(1);
        loginScreenTransition.play();
    }

}
