package com.lms;

import classes.LMS;
import classes.Librarian;
import com.jfoenix.controls.JFXButton;
import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.w3c.dom.html.HTMLAnchorElement;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.Stack;

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

    @FXML
    private StackPane searchPane;

    @FXML
    private HBox headerBar;

    //sub feature: 840 x 510
    @FXML
    private void backToHome (ActionEvent event) throws Exception {
        ControllerUtils.switchSceneWithinStage(coverPane, "Menu.fxml");
    }

    @FXML
    private void logOutUser (ActionEvent event) throws Exception {
        LMS.getInstance().logoutCurrentUser();
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

    private StackPane createSearchScreen() throws IOException {
        return FXMLLoader.load(Objects.requireNonNull(getClass().getResource("SearchBook.fxml")));
    }

    private Label createWelcomeLabel() {
        String fullName = LMS.getInstance().getCurrentUser().getFullName();

        Text welcomeText = new Text("Welcome, ");
        Text fullNameText = new Text(fullName);
        fullNameText.setFont(Font.font("System", FontWeight.BOLD, 12)); // Bold and set font size
        Text exclamationText = new Text("!");
        TextFlow textFlow = new TextFlow(welcomeText, fullNameText, exclamationText);
        return new Label("", textFlow);
    }


    @FXML
    private void useFeature1() {
        switchNode(feature1Pane);
    }

    @FXML
    private void useFeature2() {
        switchNode(feature2Pane);
    }

    private void useFeature3() {
        switchNode(feature3Pane);
    }

    @FXML
    private void toSearchScreen() {
        switchNode(searchPane);
    }

    public void toHomeScreen() {

    };

    // Switch Layout Panes in Center of BorderPane
    public void switchNode(Node node) {
        mainBorderPane.setCenter(node);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ControllerUtils.fadeTransition(slidePane,0.6, 1, 0.5);
        ControllerUtils.slideTransition(slidePane, -100, 0, 0.3);
        headerBar.getChildren().add(createWelcomeLabel());
        headerBar.setAlignment(Pos.CENTER_RIGHT);
        headerBar.setPadding(new Insets(10));
        try {
            feature1Pane = createFeature1Pane();
            feature2Pane = createFeature2Pane();
            feature3Pane = createFeature3Pane();
            searchPane = createSearchScreen();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if(LMS.getInstance().getCurrentUser() instanceof Librarian) {
            System.out.println("Admin using");
        } else {
            System.out.println("Borrower using");
        }
    }
}