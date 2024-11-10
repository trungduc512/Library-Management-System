package com.lms;

import classes.Book;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Test extends Application {
    private static final int ITEMS_PER_PAGE = 5;
    private static final int IMAGE_HEIGHT = 110;
    private static final int IMAGE_WIDTH = 80;
    private ObservableList<Book> allBooks = FXCollections.observableArrayList(); // All books
    private ObservableList<Book> currentPageBooks = FXCollections.observableArrayList(); // Books for current page
    private int currentPage = 1;
    private int totalItems = 0;
    private final Map<String, Image> imageCache = new HashMap<>();
    private StackPane overlay;

    @Override
    public void start(Stage primaryStage) {
        // Initialize the overlay with a progress indicator
        overlay = new StackPane();
        ProgressIndicator progressIndicator = new ProgressIndicator();
        progressIndicator.setMaxSize(100, 100);
        overlay.getChildren().add(progressIndicator);
        overlay.setStyle("-fx-background-color: rgba(255, 255, 255, 0.5);");  // Semi-transparent background
        overlay.setVisible(false);  // Initially hidden

        // Create the ListView and load books from the database
        ListView<Book> listView = new ListView<>(currentPageBooks);
        loadBooksFromDatabase(); // Load all books from the database

        // Set a custom cell factory to display book information with an image
        listView.setCellFactory(new Callback<>() {
            @Override
            public ListCell<Book> call(ListView<Book> param) {
                return new ListCell<>() {
                    private final ImageView imageView = new ImageView();
                    private final VBox vbox = new VBox();
                    private final Image placeholderImage = new Image(
                            Objects.requireNonNull(getClass().getResource("/com/lms/Images/Image-not-found.png")).toExternalForm(),
                            IMAGE_WIDTH, IMAGE_HEIGHT, false, false);

                    @Override
                    protected void updateItem(Book item, boolean empty) {
                        super.updateItem(item, empty);

                        if (empty || item == null) {
                            setGraphic(null);
                            setText(null);
                            imageView.setImage(null);  // Clear image view
                            return;
                        }

                        // Reset to placeholder image at the start of each update
                        imageView.setImage(placeholderImage);

                        String imageUrl = item.getThumbnailURL();
                        if (imageUrl == null || imageUrl.trim().isEmpty()) {
                            imageView.setImage(placeholderImage);  // No URL: Use the placeholder image
                        } else if (imageCache.containsKey(imageUrl)) {
                            imageView.setImage(imageCache.get(imageUrl));  // Use cached image if available
                        } else {
                            Image bookImage = new Image(imageUrl, IMAGE_WIDTH, IMAGE_HEIGHT, false, false, true);
                            imageView.setImage(placeholderImage);
                            bookImage.progressProperty().addListener((observable, oldValue, newValue) -> {
                                if (newValue.doubleValue() == 1.0 && item.getThumbnailURL().equals(imageUrl)) {
                                    imageCache.put(imageUrl, bookImage);
                                    imageView.setImage(bookImage);
                                }
                            });
                            bookImage.errorProperty().addListener((obs, oldError, newError) -> {
                                if (newError) {
                                    imageView.setImage(placeholderImage); // Set placeholder if load fails
                                }
                            });
                        }

                        // Create labels for book details
                        Label titleLabel = new Label(item.getTitle());
                        titleLabel.setStyle("-fx-font-size: 15px; -fx-font-weight: bold;"); // Larger, bold font for the title

                        Label authorLabel = new Label("Author: " + item.getAuthor());
                        Label isbnLabel = new Label("ISBN: " + item.getIsbn());
                        Label totalBooksLabel = new Label("Total books: " + item.getTotalBooks());
                        Label borrowedBooksLabel = new Label("Borrowed books: " + item.getBorrowedBooks());

                        // Arrange labels in a VBox
                        VBox textContainer = new VBox(titleLabel, authorLabel, isbnLabel, totalBooksLabel, borrowedBooksLabel); // Add isbnLabel to VBox
                        textContainer.setSpacing(5);

                        HBox cellContainer = new HBox(imageView, textContainer);
                        cellContainer.setSpacing(10);

                        // Set the entire cell graphic
                        setGraphic(cellContainer);
                        setText(null);
                    }

                };
            }
        });

        // Create pagination controls
        Button prevButton = new Button("Previous");
        Button nextButton = new Button("Next");

        prevButton.setOnAction(e -> loadPreviousPage());
        nextButton.setOnAction(e -> loadNextPage());

        HBox paginationControls = new HBox(10, prevButton, nextButton);
        paginationControls.setAlignment(Pos.CENTER);

        // Stack the listView and the overlay (with progress indicator)
        StackPane mainContent = new StackPane(listView, overlay);
        BorderPane root = new BorderPane(mainContent);
        root.setBottom(paginationControls);

        primaryStage.setScene(new Scene(root, 840, 600));
        primaryStage.setTitle("Load All Items at Once");
        primaryStage.show();
    }

    private void loadBooksFromDatabase() {
        String url = "jdbc:mysql://localhost:3306/library_db";
        String user = "root";
        String password = "123456";

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            // Show overlay (loading indicator) while fetching data
            overlay.setVisible(true);

            String sql = "SELECT * FROM books";  // No LIMIT or OFFSET, load all books
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            allBooks.clear();
            while (rs.next()) {
                String title = rs.getString("title");
                String author = rs.getString("author");
                String imageUrl = rs.getString("thumbnailURL");
                String description = rs.getString("description");
                String isbn = rs.getString("isbn");
                int totalBooks = rs.getInt("totalBooks");
                int borrowBooks = rs.getInt("borrowedBooks");
                allBooks.add(new Book(title, author, isbn, description, totalBooks, borrowBooks, imageUrl));
            }

            // Hide overlay once loading is complete
            overlay.setVisible(false);

            // Update the current page's books
            updateCurrentPageBooks();
        } catch (SQLException e) {
            e.printStackTrace();
            overlay.setVisible(false);  // Hide overlay in case of error
        }
    }

    private void updateCurrentPageBooks() {
        // Calculate the start and end index for the current page
        int startIndex = (currentPage - 1) * ITEMS_PER_PAGE;
        int endIndex = Math.min(startIndex + ITEMS_PER_PAGE, allBooks.size());

        // Update the list of books to display on the current page
        currentPageBooks.setAll(allBooks.subList(startIndex, endIndex));
    }

    private void loadNextPage() {
        if ((currentPage * ITEMS_PER_PAGE) < allBooks.size()) {
            currentPage++;
            updateCurrentPageBooks();
        }
    }

    private void loadPreviousPage() {
        if (currentPage > 1) {
            currentPage--;
            updateCurrentPageBooks();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
