package com.lms;

import classes.*;
import com.jfoenix.controls.JFXButton;
import dao.BookReviewDAO;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.util.Duration;
import javafx.scene.shape.Rectangle;
import services.GoogleBooksAPIClient;

import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

public class SearchController implements Initializable {


    private String bookTitle;
    private String authors;
    private String descriptionText;
    private String isbn_13;
    private String publisher;
    private String thumbnailURL;

    // book review properties
    private static final int ITEMS_PER_REVIEW_UPDATE_ATTEMPT = 3;
    private final GaussianBlur blurEffect = new GaussianBlur(10);

    private int totalReview = 0;
    private int rating = 0;
    private int totalTimeBookReviewUpdate = 1;
    private boolean isDisplayReview = false;
    private boolean isLoadingReview = false;

    @FXML 
    private VBox reviewBox;

    @FXML
    private VBox reviewContainer;

    @FXML
    private Label star1, star2, star3, star4, star5;

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
    private Spinner<Integer> numberSpinner1;

    @FXML
    private JFXButton reduceButton;

    @FXML
    private JFXButton addButton;

    @FXML
    private JFXButton borrowButton;

    // book review
    @FXML
    private JFXButton showReviewButton;

    @FXML
    private JFXButton addReviewButton;

    @FXML
    private JFXButton submitReviewButton;

    @FXML
    private VBox addReviewContainer;

    @FXML
    private TextArea reviewText;

    @FXML
    private Label addReviewStatusText;

    List<BookReview> bookReviewList;
    List<BookReview> currentBookReviewList;

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

        bookReviewList = new ArrayList<BookReview>();
        totalTimeBookReviewUpdate = 1;
        reviewContainer.setVisible(false);
        isDisplayReview = false;
        isLoadingReview = false;
        addReviewStatusText.setVisible(false);

        // Disable add button if visible
        if (LMS.getInstance().getCurrentUser() instanceof Librarian) {
            addButton.setDisable(true);
            reduceButton.setDisable(true);
        } else {
            borrowButton.setDisable(true);
            addReviewButton.setDisable(true);
        }

        // Disable button
        showReviewButton.setDisable(true);
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
                    reduceButton.setDisable(false);
                    numberSpinner.setDisable(false);
                    numberSpinner1.setDisable(false);

                    reduceButton.setVisible(true);
                    addButton.setVisible(true);
                    numberSpinner.setVisible(true);
                    numberSpinner1.setVisible(true);
                } else {
                    Book book = User.getBookByIsbn(search.getText());
                    if (book != null) {
                        // Enable borrow button
                        borrowButton.setVisible(true);
                        addReviewButton.setVisible(true);
                        borrowButton.setDisable(false);
                        addReviewButton.setDisable(false);
                    } else {
                        borrowButton.setVisible(false);
                        addReviewButton.setVisible(false);
                        borrowButton.setDisable(true);
                        addReviewButton.setDisable(true);
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

            if (LMS.getInstance().getCurrentUser() instanceof Librarian) {
                addButton.setVisible(false);
                addButton.setDisable(true);
                reduceButton.setVisible(false);
                reduceButton.setDisable(true);
                numberSpinner.setDisable(true);
                numberSpinner.setVisible(false);
                numberSpinner1.setDisable(true);
                numberSpinner1.setVisible(false);
            } else {
                borrowButton.setVisible(false);
                borrowButton.setDisable(true);
            }

            searchButton.setDisable(false);
        });

        Thread thread = new Thread(task);
        thread.setDaemon(true); // Allow the thread to terminate when the application exits
        thread.start();
    }

    @FXML
    private void addBookToDatabase(ActionEvent event) {
        if (LMS.getInstance().getCurrentUser() instanceof Librarian) {
            ((Librarian) LMS.getInstance().getCurrentUser()).addBook(bookTitle, authors, isbn_13, descriptionText, numberSpinner.getValue(), thumbnailURL);
            showAddedBookNotification();

            int bookAvailable = ((Librarian) LMS.getInstance().getCurrentUser()).getDeleteQuantity(this.isbn_13);
            SpinnerValueFactory.IntegerSpinnerValueFactory valueFactory1 =
                    new SpinnerValueFactory.IntegerSpinnerValueFactory(Math.min(1, bookAvailable),
                            bookAvailable, Math.min(1, bookAvailable));
            numberSpinner1.setValueFactory(valueFactory1);
        }
    }

    @FXML
    private void reduceBookFromDatabase(ActionEvent event) {
        if (LMS.getInstance().getCurrentUser() instanceof  Librarian) {
            ((Librarian) LMS.getInstance().getCurrentUser()).decreaseBook(this.isbn_13, numberSpinner1.getValue());
            showDeletedBookNotification();

            int bookAvailable = ((Librarian) LMS.getInstance().getCurrentUser()).getDeleteQuantity(this.isbn_13);
            SpinnerValueFactory.IntegerSpinnerValueFactory valueFactory1 =
                    new SpinnerValueFactory.IntegerSpinnerValueFactory(Math.min(1, bookAvailable),
                            bookAvailable, Math.min(1, bookAvailable));
            numberSpinner1.setValueFactory(valueFactory1);
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
        thumbnailURL = apiClient.getThumbnailURL();
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

        // Save thumbnail URL
        thumbnailURL = apiClient.getThumbnailURL();

        // update available quantity to delete
        if (LMS.getInstance().getCurrentUser() instanceof Librarian) {
            int bookAvailable = ((Librarian) LMS.getInstance().getCurrentUser()).getDeleteQuantity(this.isbn_13);
            SpinnerValueFactory.IntegerSpinnerValueFactory valueFactory1 =
                    new SpinnerValueFactory.IntegerSpinnerValueFactory(Math.min(1, bookAvailable),
                            bookAvailable, Math.min(1, bookAvailable));
            numberSpinner1.setValueFactory(valueFactory1);

            if (numberSpinner1.getValue() == 0) {
                reduceButton.setDisable(true);
            } else {
                reduceButton.setDisable(false);
            }
        }

        updateReviewBookBox();
    }
    
    private void updateReviewBookBox() {
        // Update book review list
        BookReviewDAO brDao = new BookReviewDAO();
        double avg_rate = brDao.getAverageRating(this.isbn_13);
        reviewBox.getChildren().clear();
        reviewContainer.getChildren().clear();

        Label reviewsSectionTitle = new Label("Reviews");
        reviewsSectionTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-padding: 30px 20px 0px 20px;");
        reviewBox.getChildren().add(reviewsSectionTitle);

        HBox avgRatingBox = new HBox();
        avgRatingBox.setSpacing(2); // Space between stars
        for (int i = 0; i < 5; i++) {
            Label star = new Label(i < (int) avg_rate ? "★" : "☆");
            star.setStyle("-fx-font-size: 20px; -fx-text-fill: gold;");
            avgRatingBox.getChildren().add(star);
        }
        Label rate = new Label(Double.toString(avg_rate) + " out of 5");
        rate.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-padding: 2px 20px 0px 10px;");
        avgRatingBox.getChildren().add(rate);
        avgRatingBox.setStyle("-fx-padding: 0px 0px 0px 20px;");
        reviewBox.getChildren().add(avgRatingBox);

        this.totalReview = brDao.getNumberOfReview(this.isbn_13);
        Label ratingNumber = new Label(Integer.toString(this.totalReview) + " ratings");
        ratingNumber.setStyle("-fx-font-size: 14px; -fx-padding: 0px 20px 30px 20px;");
        reviewBox.getChildren().add(ratingNumber);

        showReviewButton.setVisible(this.totalReview > 0 && !addReviewContainer.isVisible());
        showReviewButton.setDisable(!(this.totalReview > 0 && !addReviewContainer.isVisible()));
    }

    private void displayBookReview(List<BookReview> reviews) {
        if (reviews != null && !reviews.isEmpty()) {

            for (BookReview review : reviews) {
                VBox reviewContainer1 = new VBox();
                VBox.setMargin(reviewContainer1, new Insets(0, 10, 10, 10));
                reviewContainer1.setStyle("-fx-border-color: #ccc; -fx-border-width: 1; -fx-padding: 0px 0px 10px ; -fx-background-color: #f9f9f9; -fx-margin-bottom: 10px;");

                HBox user = new HBox();
                user.setSpacing(2);
                ImageView userIcon = new ImageView(new Image(String.valueOf(getClass().getResource("/com/lms/Images/user-top-icon.png"))));
                userIcon.setFitWidth(32); // Adjust the width as needed
                userIcon.setPreserveRatio(true);
                VBox imageContainer = new VBox(userIcon);
                user.getChildren().add(imageContainer);

                Label userNameLabel = new Label(review.getReviewerName());
                userNameLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
                user.getChildren().add(userNameLabel);

                reviewContainer1.getChildren().add(user);

                HBox ratingBox = new HBox();
                ratingBox.setSpacing(2); // Space between stars
                for (int i = 0; i < 5; i++) {
                    Label star = new Label(i < review.getRating() ? "★" : "☆");
                    star.setStyle("-fx-font-size: 14px; -fx-text-fill: gold;");
                    ratingBox.getChildren().add(star);
                }

                Label reviewTimeLabel = new Label("   Reviewed on " + new Date(review.getCreatedAt().getTime()));
                reviewTimeLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: gray;");

                ratingBox.getChildren().add(reviewTimeLabel);
                ratingBox.setStyle("-fx-padding: 0px 0px 0px 5px;");
                reviewContainer1.getChildren().add(ratingBox);

                Label reviewTextLabel = new Label(review.getReviewText());
                reviewTextLabel.setWrapText(true);
                reviewTextLabel.setStyle("-fx-font-size: 16px; -fx-padding: 5px 0px 0px 5px;");
                reviewContainer1.getChildren().add(reviewTextLabel);

                this.reviewContainer.getChildren().add(reviewContainer1);
            }
        }
    }

    private void updateCurrentBookReview() {

        int startIndex = (totalTimeBookReviewUpdate - 1) * ITEMS_PER_REVIEW_UPDATE_ATTEMPT;

        BookReviewDAO dao = new BookReviewDAO();

        if (startIndex < this.totalReview) {
            showReviewButton.setDisable(true);

            Rectangle overlay = new Rectangle(displayStackPane.getWidth(), 100, Color.rgb(0, 0, 0, 0.1));
            overlay.setStyle("-fx-padding: 0px 0px 10px 0px;");
            overlay.setDisable(true);

            ProgressIndicator loadingIndicator = new ProgressIndicator();
            loadingIndicator.setMaxSize(30, 30);

            StackPane stackPane = new StackPane(overlay, loadingIndicator);
            stackPane.setAlignment(Pos.CENTER);
            reviewContainer.getChildren().add(stackPane);

            Task<Void> loadReviewsTask = new Task<>() {

                @Override
                protected Void call() {
                    try {
                        Platform.runLater(() -> smoothScrollToBottom());

                        isLoadingReview = true;
                        currentBookReviewList = dao.getReviewsOffset(isbn_13, startIndex, ITEMS_PER_REVIEW_UPDATE_ATTEMPT);
                        bookReviewList.addAll(currentBookReviewList);

                        Thread.sleep(400);
                    } catch (InterruptedException | SQLException e) {
                        e.printStackTrace();
                    } finally {
                        isLoadingReview = false;
                    }
                    return null;
                }

                @Override
                protected void succeeded() {
                    super.succeeded();

                    reviewContainer.getChildren().remove(stackPane);
                    displayBookReview(currentBookReviewList);
                    showReviewButton.setDisable(false);
                    isLoadingReview = false;
                }

                @Override
                protected void failed() {
                    super.failed();
                    reviewContainer.getChildren().remove(stackPane);
                    showReviewButton.setDisable(false);
                    isLoadingReview = false;
                }
            };

            new Thread(loadReviewsTask).start();
        }
    }

    @FXML
    private void toggleReviewVisibility() {
        boolean currVisibility = reviewContainer.isVisible();

        if (currVisibility) {
            reviewContainer.getChildren().clear();
            isDisplayReview = false;
            reviewContainer.setVisible(false);
            showReviewButton.setText("Hide Review");
        } else {
            totalTimeBookReviewUpdate = 1;
            updateCurrentBookReview();
            isDisplayReview = true;
            reviewContainer.setVisible(true);
            showReviewButton.setText("Show Review");
        }
    }

    private void smoothScrollToBottom() {
        Timeline timeline = new Timeline();
        KeyValue keyValue = new KeyValue(searchScrollPane.vvalueProperty(), 1.0);
        KeyFrame keyFrame = new KeyFrame(Duration.millis(300), keyValue);
        timeline.getKeyFrames().add(keyFrame);
        timeline.play();
    }

    @FXML
    private void toggleAddReviewVisibility() {
        boolean isVisibleBefore = addReviewContainer.isVisible();
        addReviewContainer.setVisible(!isVisibleBefore);

        if (!isVisibleBefore) {
            ControllerUtils.slideTransitionY(addReviewContainer, -120, 0, 0.5);
            ControllerUtils.fadeTransition(addReviewContainer, 0, 1, 0.5);

            searchScrollPane.setDisable(true);
            searchScrollPane.setEffect(blurEffect);

            showReviewButton.setVisible(false);
            showReviewButton.setDisable(true);

            search.setVisible(false);
            searchButton.setVisible(false);

            if (numberSpinner != null) {
                numberSpinner.setVisible(false);
                numberSpinner1.setVisible(false);
                addButton.setVisible(false);
                reduceButton.setVisible(false);
            } else {
                borrowButton.setVisible(false);
                addReviewButton.setVisible(false);
            }
        } else {
            searchScrollPane.setDisable(false);
            searchScrollPane.setEffect(null);

            addReviewStatusText.setVisible(false);
            reviewText.clear();
            rating = 0;

            if (this.totalReview > 0) {
                showReviewButton.setVisible(true);
                showReviewButton.setDisable(false);
            }

            search.setVisible(true);
            searchButton.setVisible(true);
            if (numberSpinner != null) {
                numberSpinner.setVisible(true);
                numberSpinner1.setVisible(true);
                addButton.setVisible(true);
                reduceButton.setVisible(true);
            } else {
                borrowButton.setVisible(true);
                addReviewButton.setVisible(true);
            }
        }
    }

    @FXML
    private void submitReview() {
        String text = reviewText.getText();

        BookReviewDAO dao = new BookReviewDAO();

        if (text.isEmpty() || this.rating == 0) {
            addReviewStatusText.setText("Please finish your review!");
            addReviewStatusText.setStyle("-fx-text-fill: red; -fx-font-size: 14px;");
            addReviewStatusText.setVisible(true);
            return;
        }

        int reviewerId = LMS.getInstance().getCurrentUser().getId();
        String bookISBN = this.isbn_13;
        String reviewerName = LMS.getInstance().getCurrentUser().getFullName();
        Timestamp ts = new Timestamp(System.currentTimeMillis());

        BookReview newReview = new BookReview(reviewerId, bookISBN, this.rating, reviewerName, text, ts);
        BookReviewDAO.ReviewStatus result = dao.addReview(reviewerId, bookISBN, this.rating, reviewerName, text, ts);

        if (result == BookReviewDAO.ReviewStatus.APPROVED) {
            bookReviewList.add(newReview);

            // modify review box
            updateReviewBookBox();
            isDisplayReview = false;
            reviewContainer.setVisible(false);
            reviewContainer.getChildren().clear();

            addReviewStatusText.setStyle("-fx-text-fill: green; -fx-font-size: 14px;");
        } else {
            addReviewStatusText.setStyle("-fx-text-fill: red; -fx-font-size: 14px;");
        }
        addReviewStatusText.setText(result.getDescription());
        addReviewStatusText.setVisible(true);

        reviewText.clear();
        rating = 0;
        updateStar();
    }

    public void updateStar() {
        star1.setText(rating >= 1 ? "★" : "☆");
        star2.setText(rating >= 2 ? "★" : "☆");
        star3.setText(rating >= 3 ? "★" : "☆");
        star4.setText(rating >= 4 ? "★" : "☆");
        star5.setText(rating >= 5 ? "★" : "☆");
    }

    @FXML
    private void rateBook(MouseEvent event) {

        Label clickedStar = (Label) event.getSource();
        String starId = clickedStar.getId();

        switch (starId) {
            case "star1":
                rating = 1;
                break;
            case "star2":
                rating = 2;
                break;
            case "star3":
                rating = 3;
                break;
            case "star4":
                rating = 4;
                break;
            case "star5":
                rating = 5;
                break;
        }

        updateStar();
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

    private void showDeletedBookNotification() {
        // Create the notification label
        Label notificationLabel = new Label("Book deleted successfully!");
        notificationLabel.setPrefHeight(37.0);
        notificationLabel.setPrefWidth(195.0);
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
        slideIn.setToX(305); // Slide to the center

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

    public void showBookInfo(Book book) {
        search.setText(book.getIsbn());
        loadDataInBackground(book.getIsbn());
    }

    public void showBookInfo(String isbn) {
        search.setText(isbn);
        loadDataInBackground(isbn);
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        searchScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        searchScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        final double SPEED = 0.003;
        searchScrollPane.getContent().setOnScroll(scrollEvent -> {
            double deltaY = scrollEvent.getDeltaY() * SPEED;
            searchScrollPane.setVvalue(searchScrollPane.getVvalue() - deltaY);
        });

        searchScrollPane.vvalueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.doubleValue() == 1.0 && isDisplayReview && !isLoadingReview) {
                if ((totalTimeBookReviewUpdate * ITEMS_PER_REVIEW_UPDATE_ATTEMPT) < this.totalReview) {
                    totalTimeBookReviewUpdate++;
                    updateCurrentBookReview();
                }
            }
        });

        showReviewButton.setVisible(false);
        showReviewButton.setDisable(true);

        if (LMS.getInstance().getCurrentUser() instanceof Librarian) {
            SpinnerValueFactory.IntegerSpinnerValueFactory valueFactory =
                    new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 1); // Min: 1, Max: 100, Initial: 1
            numberSpinner.setValueFactory(valueFactory);

            SpinnerValueFactory.IntegerSpinnerValueFactory valueFactory1 =
                    new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 0, 0);
            numberSpinner1.valueProperty().addListener((obs, oldValue, newValue) -> {
                if (newValue == 0) {
                    reduceButton.setDisable(true);
                } else {
                    reduceButton.setDisable(false);
                }
            });

            addButton.setVisible(false);
            addButton.setDisable(true);
            reduceButton.setVisible(false);
            reduceButton.setDisable(true);
            numberSpinner.setDisable(true);
            numberSpinner.setVisible(false);
            numberSpinner1.setDisable(true);
            numberSpinner1.setVisible(false);
        } else {
            borrowButton.setDisable(true);
            borrowButton.setVisible(false);
            addReviewButton.setVisible(false);
            addReviewButton.setDisable(true);
        }

        searchButton.setDisable(true);

        search.textProperty().addListener((observable, oldValue, newValue) -> {
            searchButton.setDisable(newValue == null || newValue.length() != 13 || newValue.trim().isEmpty());
        });
    }
}
