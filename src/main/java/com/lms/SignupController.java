package com.lms;

import classes.Borrower;
import classes.LMS;
import classes.Librarian;
import com.jfoenix.controls.JFXButton;
import javafx.animation.FadeTransition;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.DialogPane;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class SignupController implements Initializable {


    @FXML
    private AnchorPane signupScreen;

    @FXML
    private JFXButton backToLogin;

    @FXML
    private JFXButton signupButton;

    @FXML
    private TextField usernameField;

    @FXML
    private TextField fullnameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private void backToLogin(ActionEvent event) throws Exception {
        ControllerUtils.switchSceneWithinStage(backToLogin, "Login.fxml");
    }

    @FXML
    private void signUpUser(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();
        String confirmedPassword = confirmPasswordField.getText();
        String fullname = fullnameField.getText();
        if (!confirmedPassword.equals(password)) {
            ControllerUtils.showErrorAlert("Password does not match.", "Please make sure your password match.");
            return;
        }
        if (password.length() < 8) {
            ControllerUtils.showErrorAlert("Invalid password.", "Password must be at least 8 character long.");
            return;
        }
        Borrower.register(fullname, username, password);
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ControllerUtils.fadeTransition(signupScreen, 0.6, 1, 0.3);

        //set sign up button unusable until all fields get filled
        BooleanBinding fieldsFilled = Bindings.createBooleanBinding(
                () -> !usernameField.getText().isEmpty() && !passwordField.getText().isEmpty()
                        && !confirmPasswordField.getText().isEmpty() && !fullnameField.getText().isEmpty(),
                usernameField.textProperty(),
                passwordField.textProperty(),
                confirmPasswordField.textProperty(),
                fullnameField.textProperty()
        );
        signupButton.disableProperty().bind(fieldsFilled.not());
    }
}
