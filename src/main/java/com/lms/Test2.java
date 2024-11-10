package com.lms;

import classes.Book;
import classes.Borrower;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.util.List;

public class Test2 extends Application {

    private ObservableList<String> suggestionList = FXCollections.observableArrayList();
    private StackPane overlay;
    private ListView<String> suggestionListView;  // Declare the ListView at the class level

    @Override
    public void start(Stage primaryStage) {
        // Initialize the overlay (you can keep this for loading indication)
        overlay = new StackPane();
        ProgressIndicator progressIndicator = new ProgressIndicator();
        overlay.getChildren().add(progressIndicator);
        overlay.setStyle("-fx-background-color: rgba(255, 255, 255, 0.5);");
        overlay.setVisible(false);

        // Create a TextField for the search query
        TextField searchField = new TextField();
        searchField.setPromptText("Search for a book...");

        // Create a ListView to show search suggestions
        suggestionListView = new ListView<>(suggestionList);  // Use class-level variable
        suggestionListView.setVisible(false);  // Initially hidden
        suggestionListView.setMaxHeight(300);  // Optional: set maximum height
        suggestionListView.setMaxWidth(300);  // Optional: set maximum width

        suggestionListView.setOnMouseClicked(event -> {
            String selectedTitle = suggestionListView.getSelectionModel().getSelectedItem();
            if (selectedTitle != null) {
                searchField.setText(selectedTitle);
                suggestionListView.setVisible(false);
            }
        });

        // Listener for the search field to show suggestions as user types
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.trim().isEmpty()) {
                fetchSuggestionsFromDatabase(newValue);  // Fetch book titles from DB
                suggestionListView.setVisible(true);
            } else {
                suggestionList.clear();  // Clear suggestions if search is empty
                suggestionListView.setVisible(false);
            }
        });

        // Layout setup
        BorderPane root = new BorderPane();
        root.setTop(searchField);
        root.setCenter(suggestionListView);  // Place the suggestion ListView below the search bar
        root.setBottom(overlay);  // Add the overlay to the layout

        // Set up and show the scene
        Scene scene = new Scene(root, 840, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Book Search with Suggestions");
        primaryStage.show();
    }

    // Method to fetch book suggestions from Borrower's getAllBooks() method
    private void fetchSuggestionsFromDatabase(String query) {
        // Show overlay while fetching data
        overlay.setVisible(true);

        // Clear previous suggestions
        suggestionList.clear();

        // Get all books from Borrower's getAllBooks() method (assuming it's static or accessible)
        List<Book> allBooks = Borrower.getAllBooks();  // Fetch all books

        // Filter books that match the search query (case-insensitive)
        for (Book book : allBooks) {
            String title = book.getTitle();
            if (title.toLowerCase().contains(query.toLowerCase())) {
                suggestionList.add(title);  // Add matching book title to the suggestions list
            }
        }

        // Dynamically adjust the ListView height based on the number of results
        if (!suggestionList.isEmpty()) {
            // Set ListView height based on the number of items (limit to a maximum height)
            int itemCount = suggestionList.size();
            suggestionListView.setPrefHeight(Math.min(itemCount * 30, 300));  // 30px per item, limit height to 300px
        }

        // Hide overlay once data is fetched
        overlay.setVisible(false);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
