package com.lms;

import classes.Borrower;
import classes.LMS;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
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
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
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
    private Label loginMessageLabel;

    @FXML
    private void toSignupScreen(ActionEvent event) throws Exception {
        ControllerUtils.switchSceneWithinStage(toSignupButton, "Signup.fxml");
    }

    private void toMainMenu() throws IOException {
        if (LMS.getInstance().getCurrentUser() instanceof Borrower) {
            ControllerUtils.switchSceneWithinStage(toSignupButton, "Borrower-View.fxml");
        } else {
            ControllerUtils.switchSceneWithinStage(toSignupButton, "Admin-View.fxml");
        }
    }

    @FXML
    private void loginUser() throws IOException {
        String password = passwordField.getText();
        String username = usernameField.getText();

        // Show loading indicator
        ProgressIndicator loadingIndicator = new ProgressIndicator();
        loadingIndicator.setMaxSize(20, 20);
        loginMessageLabel.setText("");
        loginMessageLabel.setGraphic(loadingIndicator);

        // Run in a background thread
        new Thread(() -> {
            if (password.length() < 8) {
                // Update UI on the JavaFX Application Thread
                Platform.runLater(() -> {
                    loginMessageLabel.setText("Password must be at least 8 characters.");
                    loginMessageLabel.setTextFill(Color.web("#E4404E"));
                    loginMessageLabel.setGraphic(null); // Hide loading indicator
                });
                return;
            }
            boolean loginSuccess;
            if (!adminCheckBox.isSelected()) {
                loginSuccess = LMS.getInstance().loginBorrower(username, password);
            } else {
                loginSuccess = LMS.getInstance().loginLibrarian(username, password);
            }

            // Update UI on the JavaFX Application Thread
            Platform.runLater(() -> {
                if (!loginSuccess) {
                    loginMessageLabel.setText("Wrong password or username.");
                    loginMessageLabel.setTextFill(Color.web("#E4404E"));
                } else {
                    try {
                        toMainMenu();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                loginMessageLabel.setGraphic(null); // Hide loading indicator
            });
        }).start();
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