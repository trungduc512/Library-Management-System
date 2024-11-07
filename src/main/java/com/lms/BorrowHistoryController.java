package com.lms;

import classes.BorrowedBookRecord;
import classes.Borrower;
import classes.LMS;
import com.jfoenix.controls.JFXButton;
import javafx.animation.PauseTransition;
import javafx.animation.TranslateTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TableCell;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.util.Callback;
import javafx.util.Duration;

import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

public class BorrowHistoryController implements Initializable {

    @FXML
    private StackPane borrowHistoryPane;

    @FXML
    private TableView<BorrowedBookRecord> borrowHistoryTable;

    @FXML
    private TableColumn<BorrowedBookRecord, String> titleColumn;

    @FXML
    private TableColumn<BorrowedBookRecord, String> isbnColumn;

    @FXML
    private TableColumn<BorrowedBookRecord, String> borrowedDateColumn;

    @FXML
    private TableColumn<BorrowedBookRecord, String> returnDateColumn;

    @FXML
    private TableColumn<BorrowedBookRecord, Void> returnColumn;

    @FXML
    private TableColumn<BorrowedBookRecord, String> statusColumn;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Set the column resize policy
        borrowHistoryTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        isbnColumn.setCellValueFactory(new PropertyValueFactory<>("isbn"));
        borrowedDateColumn.setCellValueFactory(new PropertyValueFactory<>("borrowedDate"));
        returnDateColumn.setCellValueFactory(new PropertyValueFactory<>("returnDate"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        statusColumn.setCellFactory(column -> new TableCell<BorrowedBookRecord, String>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(status);
                    switch (status) {
                        case "Borrowing":
                            setStyle("-fx-text-fill: green;");
                            break;
                        case "Overdue":
                            setStyle("-fx-text-fill: #a60f19;");
                            break;
                        default:
                            setStyle("-fx-text-fill: black;");
                            break;
                    }
                }
            }
        });

        // Create a custom cell for the returnColumn with an image
        returnColumn.setCellFactory(new Callback<TableColumn<BorrowedBookRecord, Void>, TableCell<BorrowedBookRecord, Void>>() {
            @Override
            public TableCell<BorrowedBookRecord, Void> call(TableColumn<BorrowedBookRecord, Void> param) {
                return new TableCell<>() {
                    private final JFXButton returnButton = new JFXButton();
                    private final ImageView imageView = new ImageView(
                            new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/lms/Images/return-book-icon.png"))));
                    {
                        imageView.setFitHeight(20);
                        imageView.setFitWidth(20);
                        returnButton.setGraphic(imageView);
                        returnButton.setOnAction(event -> {
                            BorrowedBookRecord data = getTableView().getItems().get(getIndex());
                            // Handle the return book logic here
                            Borrower borrower = (Borrower) LMS.getInstance().getCurrentUser();
                            borrower.returnBook(data.getBorrowerId());
                            showReturnBookNotification();
                            System.out.println("Returning book: " + data.getTitle());
                            getTableView().getItems().remove(data);
                        });
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(returnButton);
                        }
                    }
                };
            }
        });

        // Populate the TableView with data
        populateTableView();
    }

    private void populateTableView() {
        Borrower borrower = (Borrower) LMS.getInstance().getCurrentUser();
        List<BorrowedBookRecord> borrowedBooks = borrower.listBorrowedBooks();
        ObservableList<BorrowedBookRecord> borrowList = FXCollections.observableArrayList(borrowedBooks);
        borrowHistoryTable.setItems(borrowList);
    }

    private void showReturnBookNotification() {
        // Create the notification label
        Label notificationLabel = new Label("Return book added successfully!");
        notificationLabel.setPrefHeight(37.0);
        notificationLabel.setPrefWidth(230.0);
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
        borrowHistoryPane.getChildren().add(notificationLabel);
        notificationLabel.setVisible(true);

        // Animation for sliding in
        TranslateTransition slideIn = new TranslateTransition(Duration.seconds(0.5), notificationLabel);
        ControllerUtils.fadeTransition(notificationLabel, 0, 1, 0.5);
        slideIn.setFromX(400); // Start from the right
        slideIn.setFromY(-230); // Start above the screen
        slideIn.setToX(280); // Slide to the center

        slideIn.setOnFinished(slideInEvent -> {
            PauseTransition delay = new PauseTransition(Duration.seconds(0.3));
            delay.setOnFinished(delayEvent -> {
                // Animation for sliding up to disappear
                TranslateTransition slideUp = new TranslateTransition(Duration.seconds(0.8), notificationLabel);
                ControllerUtils.fadeTransition(notificationLabel, 1, 0, 0.8);
                slideUp.setToY(-250); // Slide up
                slideUp.setOnFinished(event1 -> borrowHistoryPane.getChildren().remove(notificationLabel));
                slideUp.play();
            });
            delay.play();
        });

        slideIn.play();
    }
}
