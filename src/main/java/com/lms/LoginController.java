package com.lms;

import classes.LMS;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import javafx.animation.FadeTransition;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
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
    private JFXButton loginButton;

    @FXML
    private HBox loginScreen;

    @FXML
    private TextField usernameField;

    @FXML
    private TextField passwordField;

    @FXML
    private JFXCheckBox adminCheckBox;

    @FXML
    private void toSignupScreen(ActionEvent event) throws Exception {
        ControllerUtils.switchSceneWithinStage(toSignupButton, "Signup.fxml");
    }

    private void toMainMenu() throws IOException {
        ControllerUtils.switchSceneWithinStage(toSignupButton, "Menu.fxml");
    }

    @FXML
    private void loginUser() throws IOException {
        String password = passwordField.getText();
        String username = usernameField.getText();
        if (password.length() < 8) {
            ControllerUtils.showErrorAlert("Invalid password.", "Password must be at least 8 character long.");
            return;
        }
        if (!adminCheckBox.isSelected()) {
            if (!LMS.getInstance().loginBorrower(username, password)) {
                ControllerUtils.showErrorAlert("Invalid information", "Username or password is invalid.");
            } else {
                toMainMenu();
            }
        } else {
            if (!LMS.getInstance().loginLibrarian(username, password)) {
                ControllerUtils.showErrorAlert("Invalid information", "Username or password is invalid.");
            } else {
                toMainMenu();
            }
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ControllerUtils.fadeTransition(loginScreen, 0.6, 1, 0.3);

        //set login button unusable until all fields get filled
        BooleanBinding fieldsFilled = Bindings.createBooleanBinding(
                () -> !usernameField.getText().isEmpty() && !passwordField.getText().isEmpty(),
                usernameField.textProperty(),
                passwordField.textProperty()
        );
        loginButton.disableProperty().bind(fieldsFilled.not());
    }
}