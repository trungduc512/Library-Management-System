package com.lms;

import classes.*;
import com.jfoenix.controls.JFXButton;
import javafx.animation.PauseTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.util.Duration;
import jdk.jfr.Description;
import org.controlsfx.control.action.Action;
import javafx.scene.shape.Rectangle;
import services.GoogleBooksAPIClient;

import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.concurrent.Callable;

public class SearchController implements Initializable {


    private String bookTitle;
    private String authors;
    private String publisher;
    private String descriptionText;
    private String isbn_13;

    @FXML
    private StackPane searchScreen;

    @FXML
    private StackPane displayStackPane;

    @FXML
    private ScrollPane searchScrollPane;

    @FXML
    private Button searchButton;

    @FXML
    private VBox vbox;

    @FXML
    private TextField search;

    @FXML
    private ProgressBar progressBar;

    @FXML
    private Spinner<Integer> numberSpinner;

    @FXML
    private JFXButton addButton;

    @FXML
    private JFXButton borrowButton;

    @FXML
    private void clicked(ActionEvent event) {
        String isbn = search.getText();
        loadDataInBackground(isbn);
    }

    private void loadDataInBackground(String isbn) {
        Task<GoogleBooksAPIClient> task = new Task<GoogleBooksAPIClient>() {
            @Override
            protected GoogleBooksAPIClient call() throws Exception {
                // Fetch data from the API in the background
                return new GoogleBooksAPIClient(isbn);
            }
        };

        // Add progress indicator
        Rectangle overlay = new Rectangle(displayStackPane.getWidth(), displayStackPane.getHeight(), Color.rgb(0, 0, 0, 0.1));
        overlay.setDisable(true); // Make sure the overlay doesn't block interactions

        ProgressIndicator loadingIndicator = new ProgressIndicator();
        loadingIndicator.setMaxSize(50, 50); // Adjust size if needed

        StackPane stackPane = new StackPane(overlay, loadingIndicator);
        stackPane.setAlignment(Pos.CENTER);
        displayStackPane.getChildren().add(stackPane);

        // Disable add button if visible
        addButton.setDisable(true);

        // Disable search button
        searchButton.setDisable(true);

        task.setOnSucceeded(event -> {
            displayStackPane.getChildren().remove(stackPane); // Remove the overlay and indicator
            GoogleBooksAPIClient apiClient = task.getValue();
            if (apiClient.getISBN() == null || !Objects.equals(apiClient.getISBN(), search.getText())) {
                vbox.getChildren().clear();
                showNotFoundNotification();
            } else {
                Platform.runLater(() -> updateUI(apiClient));
                searchButton.setDisable(false);
                if (LMS.getInstance().getCurrentUser() instanceof Librarian) {
                    // Enable add button
                    addButton.setDisable(false);
                    numberSpinner.setDisable(false);
                    addButton.setVisible(true);
                    numberSpinner.setVisible(true);
                } else {
                    Book book = User.getBookByIsbn(search.getText());
                    if (book != null) {
                        // Enable borrow button
                        borrowButton.setVisible(true);
                        borrowButton.setDisable(false);
                    } else {
                        borrowButton.setVisible(false);
                        borrowButton.setDisable(true);
                    }
                }
            }
            searchButton.setDisable(false);
        });

        task.setOnFailed(event -> {
            Platform.runLater(() -> {
                vbox.getChildren().clear();
                showNotFoundNotification();
            });
            displayStackPane.getChildren().remove(stackPane); // Remove the overlay and indicator
            addButton.setVisible(false);
            addButton.setDisable(true);
            numberSpinner.setDisable(true);
            numberSpinner.setVisible(false);
            searchButton.setDisable(false);
            borrowButton.setVisible(false);
            borrowButton.setDisable(true);
        });

        Thread thread = new Thread(task);
        thread.setDaemon(true); // Allow the thread to terminate when the application exits
        thread.start();
    }

    @FXML
    private void addBookToDatabase(ActionEvent event) {
        if (LMS.getInstance().getCurrentUser() instanceof Librarian) {
            ((Librarian) LMS.getInstance().getCurrentUser()).addBook(bookTitle, authors, isbn_13, descriptionText, numberSpinner.getValue());
            showAddedBookNotification();
        }
    }

    @FXML
    private void borrowBook(ActionEvent event) {
        if (LMS.getInstance().getCurrentUser() instanceof Borrower) {
            if (((Borrower) LMS.getInstance().getCurrentUser()).borrowBookByIsbn(isbn_13, 1)) {
                showBorrowedBookNotification();
            } else {
                showNotEnoughBookNotification();
            }
        }
    }

    private void updateUI(GoogleBooksAPIClient apiClient) {
        // Clear the VBox
        vbox.getChildren().clear();

        // Add Thumbnail on top
        String thumbnailURL = apiClient.getThumbnailURL();
        if (!thumbnailURL.isEmpty()) {
            Image thumbnail = new Image(thumbnailURL);
            ImageView thumbnailView = new ImageView(thumbnail);
            thumbnailView.setFitWidth(150); // Adjust the width as needed
            thumbnailView.setPreserveRatio(true);
            VBox imageContainer = new VBox(thumbnailView);
            imageContainer.setAlignment(Pos.CENTER);
            imageContainer.setStyle("-fx-padding: 10px 20px;"); // Added left and right padding
            vbox.getChildren().add(imageContainer);
        }

        // Add Title
        bookTitle = apiClient.getTitle();
        Label title = new Label(bookTitle);
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-alignment: center;");
        VBox titleContainer = new VBox(title);
        titleContainer.setAlignment(Pos.CENTER);
        titleContainer.setStyle("-fx-padding: 10px 20px; -fx-margin-bottom: 10px;");
        vbox.getChildren().add(titleContainer);

        // Add Authors label
        Label authorsLabelTitle = new Label("Authors:");
        authorsLabelTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-padding: 10px 20px 0 20px;");
        vbox.getChildren().add(authorsLabelTitle);

        // Add Authors content
        ArrayList<String> authorList = apiClient.getAuthors();
        if (!authorList.isEmpty()) {
            authors = String.join(", ", authorList);
            Label authorsLabel = new Label(authors);
            authorsLabel.setStyle("-fx-font-size: 16px; -fx-padding: 0 20px 10px 20px;"); // Adjusted padding for spacing
            vbox.getChildren().add(authorsLabel);
        }

        // Add Publisher label
        Label publisherLabelTitle = new Label("Publisher:");
        publisherLabelTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-padding: 10px 20px 0 20px;");
        vbox.getChildren().add(publisherLabelTitle);

        // Add Publisher content
        publisher = apiClient.getPublisher();
        Label publisherLabel = new Label(publisher);
        publisherLabel.setStyle("-fx-font-size: 16px; -fx-padding: 0 20px 10px 20px;"); // Adjusted padding for spacing
        vbox.getChildren().add(publisherLabel);

        // Add ISBN_13 label
        Label ISBNLabel = new Label("ISBN:");
        ISBNLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-padding: 10px 20px 0 20px;");
        vbox.getChildren().add(ISBNLabel);

        // Add ISBN_13 content
        isbn_13 = apiClient.getISBN();
        Label isbnContent = new Label(isbn_13);
        isbnContent.setStyle("-fx-font-size: 16px; -fx-padding: 0 20px 10px 20px;"); // Adjusted padding for spacing
        vbox.getChildren().add(isbnContent);

        // Add Description label
        Label descriptionLabelTitle = new Label("Description:");
        descriptionLabelTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-padding: 10px 20px 0 20px;");
        vbox.getChildren().add(descriptionLabelTitle);

        // Add Description content using TextFlow for justified text
        descriptionText = apiClient.getDescription();
        Text descriptionContent = new Text(descriptionText);
        descriptionContent.setStyle("-fx-font-size: 16px;");
        TextFlow descriptionFlow = new TextFlow(descriptionContent);
        descriptionFlow.setTextAlignment(TextAlignment.JUSTIFY);
        descriptionFlow.setMaxWidth(Double.MAX_VALUE); // Allow full width
        descriptionFlow.setStyle("-fx-padding: 0 20px 10px 20px;"); // Adjusted padding for spacing
        vbox.getChildren().add(descriptionFlow);
    }

    private void showAddedBookNotification() {
        // Create the notification label
        Label notificationLabel = new Label("Book added successfully!");
        notificationLabel.setPrefHeight(37.0);
        notificationLabel.setPrefWidth(175.0);
        notificationLabel.setStyle("-fx-background-color: #73B573; -fx-text-fill: white; -fx-padding: 10px; -fx-background-radius: 0.5em;");
        notificationLabel.setVisible(false);

        // Load the image and set it as the graphic
        Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/lms/Images/add-book-checked-icon.png")));
        ImageView imageView = new ImageView(image);
        imageView.setFitHeight(17.0);
        imageView.setFitWidth(17.0);
        imageView.setPickOnBounds(true);
        imageView.setPreserveRatio(true);
        notificationLabel.setGraphic(imageView);

        // Add the notification label to the scene
        searchScreen.getChildren().add(notificationLabel);
        notificationLabel.setVisible(true);

        // Animation for sliding in
        TranslateTransition slideIn = new TranslateTransition(Duration.seconds(0.5), notificationLabel);
        ControllerUtils.fadeTransition(notificationLabel, 0, 1, 0.5);
        slideIn.setFromX(400); // Start from the right
        slideIn.setFromY(-230); // Start above the screen
        slideIn.setToX(320); // Slide to the center

        slideIn.setOnFinished(slideInEvent -> {
            PauseTransition delay = new PauseTransition(Duration.seconds(0.3));
            delay.setOnFinished(delayEvent -> {
                // Animation for sliding up to disappear
                TranslateTransition slideUp = new TranslateTransition(Duration.seconds(0.8), notificationLabel);
                ControllerUtils.fadeTransition(notificationLabel, 1, 0, 0.8);
                slideUp.setToY(-250); // Slide up
                slideUp.setOnFinished(event1 -> searchScreen.getChildren().remove(notificationLabel));
                slideUp.play();
            });
            delay.play();
        });

        slideIn.play();
    }

    private void showBorrowedBookNotification() {
        // Create the notification label
        Label notificationLabel = new Label("Book borrowed successfully!");
        notificationLabel.setPrefHeight(37.0);
        notificationLabel.setPrefWidth(200.0);
        notificationLabel.setStyle("-fx-background-color: #73B573; -fx-text-fill: white; -fx-padding: 10px; -fx-background-radius: 0.5em;");
        notificationLabel.setVisible(false);

        // Load the image and set it as the graphic
        Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/lms/Images/add-book-checked-icon.png")));
        ImageView imageView = new ImageView(image);
        imageView.setFitHeight(17.0);
        imageView.setFitWidth(17.0);
        imageView.setPickOnBounds(true);
        imageView.setPreserveRatio(true);
        notificationLabel.setGraphic(imageView);

        // Add the notification label to the scene
        searchScreen.getChildren().add(notificationLabel);
        notificationLabel.setVisible(true);

        // Animation for sliding in
        TranslateTransition slideIn = new TranslateTransition(Duration.seconds(0.5), notificationLabel);
        ControllerUtils.fadeTransition(notificationLabel, 0, 1, 0.5);
        slideIn.setFromX(400); // Start from the right
        slideIn.setFromY(-230); // Start above the screen
        slideIn.setToX(300); // Slide to the center

        slideIn.setOnFinished(slideInEvent -> {
            PauseTransition delay = new PauseTransition(Duration.seconds(0.3));
            delay.setOnFinished(delayEvent -> {
                // Animation for sliding up to disappear
                TranslateTransition slideUp = new TranslateTransition(Duration.seconds(0.8), notificationLabel);
                ControllerUtils.fadeTransition(notificationLabel, 1, 0, 0.8);
                slideUp.setToY(-250); // Slide up
                slideUp.setOnFinished(event1 -> searchScreen.getChildren().remove(notificationLabel));
                slideUp.play();
            });
            delay.play();
        });

        slideIn.play();
    }

    private void showNotEnoughBookNotification() {
        // Create the notification label
        Label notificationLabel = new Label("Not enough books available!");
        notificationLabel.setPrefHeight(37.0);
        notificationLabel.setPrefWidth(200.0);
        notificationLabel.setStyle("-fx-background-color: #FE7156; -fx-text-fill: white; -fx-padding: 10px; -fx-background-radius: 0.5em;");
        notificationLabel.setVisible(false);

        // Load the image and set it as the graphic
        Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/lms/Images/not-found-icon.png")));
        ImageView imageView = new ImageView(image);
        imageView.setFitHeight(17.0);
        imageView.setFitWidth(17.0);
        imageView.setPickOnBounds(true);
        imageView.setPreserveRatio(true);
        notificationLabel.setGraphic(imageView);

        // Add the notification label to the scene
        searchScreen.getChildren().add(notificationLabel);
        notificationLabel.setVisible(true);

        // Animation for sliding in
        TranslateTransition slideIn = new TranslateTransition(Duration.seconds(0.5), notificationLabel);
        ControllerUtils.fadeTransition(notificationLabel, 0, 1, 0.5);
        slideIn.setFromX(400); // Start from the right
        slideIn.setFromY(-230); // Start above the screen
        slideIn.setToX(300); // Slide to the center

        slideIn.setOnFinished(slideInEvent -> {
            PauseTransition delay = new PauseTransition(Duration.seconds(0.3));
            delay.setOnFinished(delayEvent -> {
                // Animation for sliding up to disappear
                TranslateTransition slideUp = new TranslateTransition(Duration.seconds(0.8), notificationLabel);
                ControllerUtils.fadeTransition(notificationLabel, 1, 0, 0.8);
                slideUp.setToY(-250); // Slide up
                slideUp.setOnFinished(event1 -> searchScreen.getChildren().remove(notificationLabel));
                slideUp.play();
            });
            delay.play();
        });

        slideIn.play();
    }


    private void showNotFoundNotification() {
        // Create the notification label with text and styling
        Label notFoundNotification = new Label("Cannot find book with provided ISBN.");
        notFoundNotification.setPrefHeight(37.0);
        notFoundNotification.setPrefWidth(244.0);
        notFoundNotification.setStyle("-fx-background-color: #FE7156; -fx-text-fill: white; -fx-padding: 10px; -fx-background-radius: 0.5em;");
        notFoundNotification.setVisible(false); // Initially not visible

        // Load the image and create an ImageView
        Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/lms/Images/not-found-icon.png")));
        ImageView imageView = new ImageView(image);
        imageView.setFitHeight(17.0);
        imageView.setFitWidth(17.0);
        imageView.setPickOnBounds(true);
        imageView.setPreserveRatio(true);

        // Set the ImageView as the graphic for the label
        notFoundNotification.setGraphic(imageView);

        // Add the label to the scene
        searchScreen.getChildren().add(notFoundNotification);
        notFoundNotification.setVisible(true); // Make the label visible

        // Create and configure the slide-in animation
        TranslateTransition slideIn = new TranslateTransition(Duration.seconds(0.5), notFoundNotification);
        ControllerUtils.fadeTransition(notFoundNotification, 0, 1, 0.5); // Apply fade transition
        slideIn.setFromX(330); // Start from the right
        slideIn.setFromY(-230); // Start above the screen
        slideIn.setToX(280); // Slide to the center

        // Set the action to perform after the slide-in animation is finished
        slideIn.setOnFinished(slideInEvent -> {
            PauseTransition delay = new PauseTransition(Duration.seconds(0.3));
            delay.setOnFinished(delayEvent -> {
                // Create and configure the slide-up animation
                TranslateTransition slideUp = new TranslateTransition(Duration.seconds(0.5), notFoundNotification);
                ControllerUtils.fadeTransition(notFoundNotification, 1, 0, 0.5); // Apply fade transition
                slideUp.setToY(-250); // Slide up
                slideUp.setOnFinished(event1 -> {
                    // Remove the label from the scene
                    searchScreen.getChildren().remove(notFoundNotification);
                });
                slideUp.play(); // Play the slide-up animation
            });
            delay.play(); // Play the delay
        });

        slideIn.play(); // Play the slide-in animation
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        searchScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        searchScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        SpinnerValueFactory.IntegerSpinnerValueFactory valueFactory =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 1); // Min: 1, Max: 100, Initial: 1
        numberSpinner.setValueFactory(valueFactory);
        addButton.setVisible(false);
        addButton.setDisable(true);
        numberSpinner.setDisable(true);
        numberSpinner.setVisible(false);
        searchButton.setDisable(true);
        borrowButton.setDisable(true);
        borrowButton.setVisible(false);
        search.textProperty().addListener((observable, oldValue, newValue) -> {
            searchButton.setDisable(newValue == null || newValue.length() != 13 || newValue.trim().isEmpty());
        });
    }
}
