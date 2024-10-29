package com.lms;

import classes.Borrower;
import classes.LMS;
import classes.Librarian;
import com.jfoenix.controls.JFXButton;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
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
    private Label signupMessageLabel;

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

        // Show loading indicator
        ProgressIndicator loadingIndicator = new ProgressIndicator();
        loadingIndicator.setMaxSize(20, 20); // Set the size for better placement
        signupMessageLabel.setText(""); // Clear any previous text
        signupMessageLabel.setGraphic(loadingIndicator);

        if (!confirmedPassword.equals(password)) {
            signupMessageLabel.setText("Password does not match.");
            signupMessageLabel.setTextFill(Color.web("#E4404E"));
            signupMessageLabel.setGraphic(null); // Hide loading indicator
            return;
        }
        if (password.length() < 8) {
            signupMessageLabel.setText("Password must be at least 8 characters.");
            signupMessageLabel.setTextFill(Color.web("#E4404E"));
            signupMessageLabel.setGraphic(null); // Hide loading indicator
            return;
        }

        new Thread(() -> {
            boolean success = Borrower.register(fullname, username, password);
            // Update the UI based on the registration result
            Platform.runLater(() -> {
                if (!success) {
                    signupMessageLabel.setText("Account already exists.");
                    signupMessageLabel.setTextFill(Color.web("#E4404E")); // Set message color to red when failed
                } else {
                    signupMessageLabel.setText("Your registration has been successful!");
                    signupMessageLabel.setTextFill(Color.web("#8CC24A")); // Set message color to green when successful
                }
                signupMessageLabel.setGraphic(null); // Hide loading indicator
            });
        }).start();
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
