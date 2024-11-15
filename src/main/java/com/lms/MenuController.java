package com.lms;

import classes.Book;
import classes.Borrower;
import classes.LMS;
import classes.Librarian;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class MenuController implements Initializable {

    private AvailableBooksController availableBooksController;
    private SearchController searchController;

    @FXML
    private BorderPane coverPane;

    @FXML
    private BorderPane slidePane;

    @FXML
    private BorderPane mainBorderPane;

    @FXML
    private AnchorPane feature1Pane;

    @FXML
    private StackPane availableBooks;

    @FXML
    private AnchorPane feature3Pane;

    @FXML
    private StackPane borrowHistoryPane;

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

    private StackPane createAvailableBooksScreen() throws IOException {
        FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("AvailableBooks-view.fxml")));
        StackPane pane = loader.load();
        availableBooksController = loader.getController();
        availableBooksController.setMenuController(this);
        return pane;
    }

    private StackPane createSearchScreen() throws IOException {
        FXMLLoader loader;
        if (LMS.getInstance().getCurrentUser() instanceof Borrower) {
            loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("Borrower-SearchView.fxml")));
        } else {
            loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("Admin-SearchView.fxml")));
        }

        StackPane pane = loader.load();
        searchController = loader.getController();
        return pane;
    }

    private StackPane createBorrowHistoryScreen() throws IOException {
        return FXMLLoader.load(Objects.requireNonNull(getClass().getResource("BorrowHistory.fxml")));
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
    public void useFeature2() throws IOException {
        availableBooks = createAvailableBooksScreen();
        switchNode(availableBooks);
    }

    @FXML
    private void useHistoryScreen() throws IOException {
        borrowHistoryPane = createBorrowHistoryScreen();
        switchNode(borrowHistoryPane);
    }

    private void useFeature3() {
        switchNode(feature3Pane);
    }

    @FXML
    public void toSearchScreen() throws IOException {
        searchPane = createSearchScreen();
        switchNode(searchPane);
    }

    public void toSearchScreen(Book book) {
        switchNode(searchPane);
        searchController.showBookInfo(book);
    }

    public void toHomeScreen() {

    };

    // Switch Layout Panes in Center of BorderPane
    public void switchNode(Node node) {
        mainBorderPane.setCenter(node);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //ControllerUtils.fadeTransition(slidePane,0.6, 1, 0.5);
        //ControllerUtils.slideTransition(slidePane, -100, 0, 0.3);
        headerBar.getChildren().add(createWelcomeLabel());
        headerBar.setAlignment(Pos.CENTER_RIGHT);
        headerBar.setPadding(new Insets(10));
        try {
            feature1Pane = createFeature1Pane();
            availableBooks = createAvailableBooksScreen();

            searchPane = createSearchScreen();
            borrowHistoryPane = createBorrowHistoryScreen();
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