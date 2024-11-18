package com.lms;

import classes.Book;
import classes.Borrower;
import classes.LMS;
import classes.Librarian;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.concurrent.Task;

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
    private StackPane borrowerListPane;

    @FXML
    private StackPane searchPane;

    @FXML
    private HBox headerBar;

    //sub feature: 840 x 510
    @FXML
    private void backToHome (ActionEvent event) throws Exception {
        //ControllerUtils.switchSceneWithinStage(coverPane, "Menu.fxml");
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

    private StackPane createBorrowerListPane() throws IOException {
        return FXMLLoader.load(Objects.requireNonNull(getClass().getResource("BorrowerList-View.fxml")));
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
    private void toBorrowerListScreen() throws IOException {
        createLoadingPane();

        // Create a Task to load the Borrower List screen in the background
        Task<StackPane> loadScreenTask = new Task<>() {
            @Override
            protected StackPane call() throws Exception {
                // Add a brief delay for better UX
                try {
                    Thread.sleep(500); // 500ms delay
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt(); // Restore the interrupted status
                }
                return createBorrowerListPane(); // Load the borrower list screen
            }
        };

        // Handle successful loading
        loadScreenTask.setOnSucceeded(event -> {
            StackPane borrowerListPane = loadScreenTask.getValue();
            Platform.runLater(() -> switchNode(borrowerListPane));
        });

        // Handle loading errors
        loadScreenTask.setOnFailed(event -> {
            Throwable exception = loadScreenTask.getException();
            exception.printStackTrace();
            // Optionally: Display an error message to the user
        });

        // Start the task in a separate thread
        Thread thread = new Thread(loadScreenTask);
        thread.setDaemon(true);
        thread.start();
    }



    @FXML
    private void useFeature1() {
        switchNode(feature1Pane);
    }

    @FXML
    public void toAvailableBooksScreen() {
        createLoadingPane();

        // Create a Task to load the screen in the background
        Task<StackPane> loadScreenTask = new Task<>() {
            @Override
            protected StackPane call() throws IOException {
                // Add a brief delay for better UX
                try {
                    Thread.sleep(500); // 500ms delay
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt(); // Restore the interrupted status
                }
                return createAvailableBooksScreen();
            }
        };

        // Handle successful loading
        loadScreenTask.setOnSucceeded(event -> {
            StackPane availableBooksPane = loadScreenTask.getValue();
            Platform.runLater(() -> switchNode(availableBooksPane));
        });

        // Handle loading errors
        loadScreenTask.setOnFailed(event -> {
            Throwable exception = loadScreenTask.getException();
            exception.printStackTrace();
            // Optionally: Display an error message to the user
        });

        // Start the task in a separate thread
        Thread thread = new Thread(loadScreenTask);
        thread.setDaemon(true);
        thread.start();
    }


    private void createLoadingPane() {
        // Create a ProgressIndicator
        ProgressIndicator progressIndicator = new ProgressIndicator();

        // Create a StackPane to center the ProgressIndicator
        StackPane stackPane = new StackPane();
        stackPane.getChildren().add(progressIndicator);

        // Apply optional styling if needed
        stackPane.setStyle("-fx-background-color: #f4f4f4;");
        stackPane.setPrefSize(600, 840); // Set a preferred size for the pane

        switchNode(stackPane);
    }


    @FXML
    private void useHistoryScreen() {
        // Create and add the loading pane on the JavaFX Application Thread
        createLoadingPane();

        // Create a Task to load the BorrowHistory screen in the background
        Task<StackPane> loadHistoryTask = new Task<>() {
            @Override
            protected StackPane call() throws IOException {
                // Add a brief delay for better UX
                try {
                    Thread.sleep(500); // 500ms delay
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt(); // Restore interrupted status
                }
                return createBorrowHistoryScreen(); // Load the borrow history screen
            }
        };

        // Handle successful loading
        loadHistoryTask.setOnSucceeded(event -> {
            StackPane borrowHistoryScreen = loadHistoryTask.getValue();

            // Remove the loading pane and switch to the borrow history screen on the JavaFX Application Thread
            Platform.runLater(() -> {
                switchNode(borrowHistoryScreen); // Switch to the loaded borrow history screen
            });
        });

        // Handle loading errors
        loadHistoryTask.setOnFailed(event -> {
            Throwable exception = loadHistoryTask.getException();
            exception.printStackTrace();
            // Optionally: Display an error message to the user
        });

        // Start the background task to load the screen
        Thread thread = new Thread(loadHistoryTask);
        thread.setDaemon(true);
        thread.start();
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

    public void toSearchScreen(String isbn) {
        switchNode(searchPane);
        searchController.showBookInfo(isbn);
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