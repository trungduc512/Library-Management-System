package com.lms;

import com.jfoenix.controls.JFXButton;
import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    @FXML
    private JFXButton toSignupButton;

    @FXML
    private HBox loginScreen;

    @FXML
    private void toSignupScreen(ActionEvent event) throws Exception {
        ControllerUtils.switchSceneWithinStage(toSignupButton, "Signup.fxml");
    }

    @FXML
    private void toMainMenu(ActionEvent event) throws IOException {
        ControllerUtils.switchSceneWithinStage(toSignupButton, "Menu.fxml");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ControllerUtils.fadeTransition(loginScreen, 0.6, 1, 0.3);
    }
}