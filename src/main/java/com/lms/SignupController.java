package com.lms;

import com.jfoenix.controls.JFXButton;
import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class SignupController implements Initializable {

    public PasswordField signupPassword;
    public TextField signupUsername;
    public PasswordField confirmPassword;
    public JFXButton backToLogin;
    public JFXButton signupButton;
    @FXML
    private AnchorPane signupScreen;

    @FXML
    private void backToLogin (ActionEvent event) throws Exception {
        ControllerUtils.switchSceneWithinStage(backToLogin, "Login.fxml");
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ControllerUtils.fadeTransition(signupScreen, 0.8, 1, 0.1);
    }
}
