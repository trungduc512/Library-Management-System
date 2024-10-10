package com.lms;

import com.jfoenix.controls.JFXButton;
import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.w3c.dom.html.HTMLAnchorElement;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class MenuController implements Initializable {

    @FXML
    private BorderPane coverPane;

    @FXML
    private BorderPane slidePane;

    @FXML
    private BorderPane mainBorderPane;

    @FXML
    private AnchorPane feature1Pane;

    @FXML
    private AnchorPane feature2Pane;

    @FXML
    private AnchorPane feature3Pane;

    //sub feature: 840 x 510
    @FXML
    private void backToHome (ActionEvent event) throws Exception {
        ControllerUtils.switchSceneWithinStage(coverPane, "Menu.fxml");
    }

    @FXML
    private void logOutUser (ActionEvent event) throws Exception {
        ControllerUtils.switchSceneWithinStage(coverPane, "Login.fxml");
    }

    private AnchorPane createFeature1Pane() throws IOException {
        return FXMLLoader.load(Objects.requireNonNull(getClass().getResource("Feature1.fxml")));
    }

    private AnchorPane createFeature2Pane() throws IOException {
        return FXMLLoader.load(Objects.requireNonNull(getClass().getResource("Feature2.fxml")));
    }

    private AnchorPane createFeature3Pane() throws IOException {
        return FXMLLoader.load(Objects.requireNonNull(getClass().getResource("Feature3.fxml")));
    }

    public void useFeature1() {
        switchNode(feature1Pane);
    }

    public void useFeature2() {
        switchNode(feature2Pane);
    }

    public void useFeature3() {
        switchNode(feature3Pane);
    }

    // Switch Layout Panes in Center of BorderPane
    public void switchNode(Node node) {
        mainBorderPane.setCenter(node);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ControllerUtils.fadeTransition(slidePane,0.6, 1, 0.5);
        ControllerUtils.slideTransition(slidePane, -100, 0, 0.3);
        try {
            feature1Pane = createFeature1Pane();
            feature2Pane = createFeature2Pane();
            feature3Pane = createFeature3Pane();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}