package Controller;

import classes.Book;
import classes.Borrower;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.*;

public class AvailableBooksController implements Initializable {  // Implement Initializable
    private static final int ITEMS_PER_PAGE = 4;
    private static final int IMAGE_HEIGHT = 118;
    private static final int IMAGE_WIDTH = 80;
    private static final double SEARCH_CELL_HEIGHT = 25;

    private ObservableList<Book> allBooks = FXCollections.observableArrayList(); // All books
    private ObservableList<Book> currentPageBooks = FXCollections.observableArrayList(); // Books for current page
    private int currentPage = 1;
    private final Map<String, Image> imageCache = new HashMap<>();

    private MenuController menuController;

    @FXML
    private StackPane availableBooksPane;

    @FXML
    private ListView<Book> listView;

    @FXML
    private ListView<BookItem> suggestionListView;

    @FXML
    private TextField searchField;

    @FXML
    private Button prevButton;

    @FXML
    private Button nextButton;

    public void setMenuController(MenuController menuController) {
        this.menuController = menuController;
    }

    public static class BookItem {
        private String title;
        private String isbn;

        public BookItem(String title, String isbn) {
            this.isbn = isbn;
            this.title = title;
        }

        public String getTitle() {
            return title;
        }

        public String getIsbn() {
            return isbn;
        }
    }

    private static ObservableList<Book> bookObservableList = FXCollections.observableArrayList(Borrower.getAllBooks());
    private static ObservableList<BookItem> suggestions = FXCollections.observableArrayList();
    private static FilteredList<Book> filteredList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadBooksFromDatabase(); // Load books from database

        // Initialize filteredList here
        filteredList = new FilteredList<>(bookObservableList, book -> true);  // Initially show all books
        suggestionListView.setItems(suggestions);

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            handleKeyReleased();
        });

        populateSuggestions();

        // Handle double-click on suggestionListView items
        suggestionListView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) { // Detect double-click
                BookItem selectedItem = suggestionListView.getSelectionModel().getSelectedItem();
                if (selectedItem != null) {
                    menuController.toSearchScreen(selectedItem.getIsbn());
                }
            }
        });

        // Set custom cell factory for ListView to display book info and images
        listView.setCellFactory(new Callback<ListView<Book>, ListCell<Book>>() {
            @Override
            public ListCell<Book> call(ListView<Book> param) {
                return new ListCell<>() {
                    private final ImageView imageView = new ImageView();
                    private final VBox vbox = new VBox();
                    private final Image placeholderImage = new Image(
                            getClass().getResource("/com/lms/Images/Image-not-found.png").toExternalForm(),
                            IMAGE_WIDTH, IMAGE_HEIGHT, false, false);

                    @Override
                    protected void updateItem(Book item, boolean empty) {
                        super.updateItem(item, empty);

                        if (empty || item == null) {
                            setGraphic(null);
                            setText(null);
                            imageView.setImage(null); // Clear image view
                            return;
                        }

                        // Reset to placeholder image at the start of each update
                        imageView.setImage(placeholderImage);

                        String imageUrl = item.getThumbnailURL();
                        if (imageUrl == null || imageUrl.trim().isEmpty()) {
                            imageView.setImage(placeholderImage); // No URL: Use the placeholder image
                        } else if (imageCache.containsKey(imageUrl)) {
                            imageView.setImage(imageCache.get(imageUrl)); // Use cached image if available
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
                        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 16)); // Make title bold

                        // Create labels for book details
                        VBox textContainer = new VBox(
                                titleLabel,
                                new Label("Author: " + item.getAuthor()),
                                new Label("ISBN: " + item.getIsbn()),
                                new Label("Total books: " + item.getTotalBooks()),
                                new Label("Borrowed books: " + item.getBorrowedBooks())
                        );
                        textContainer.setSpacing(5);

                        HBox cellContainer = new HBox(imageView, textContainer);
                        cellContainer.setSpacing(10);

                        // Set the entire cell graphic
                        setGraphic(cellContainer);
                    }
                };
            }
        });

        // Add mouse click listener for double-click on ListView items
        listView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) { // Detect double-click
                Book selectedBook = listView.getSelectionModel().getSelectedItem();
                if (selectedBook != null) {
                    try {
                        handleListViewDoubleClick(selectedBook);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });

        // Set actions for pagination buttons
        prevButton.setOnAction(this::loadPreviousPage);
        nextButton.setOnAction(this::loadNextPage);
    }

    private void handleListViewDoubleClick(Book selectedBook) throws IOException {
        menuController.toSearchScreen(selectedBook);
    }

    private void handleSuggestionListViewDoubleClick(BookItem selectedBook) throws IOException {
        menuController.toSearchScreen(selectedBook.getIsbn());
    }

    private void loadBooksFromDatabase() {
        String url = "jdbc:mysql://localhost:3306/library_db";
        String user = "root";
        String password = "123456";

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            String sql = "SELECT * FROM books"; // No LIMIT or OFFSET, load all books
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

            // Update the current page's books
            updateCurrentPageBooks();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateCurrentPageBooks() {
        // Calculate the start and end index for the current page
        int startIndex = (currentPage - 1) * ITEMS_PER_PAGE;
        int endIndex = Math.min(startIndex + ITEMS_PER_PAGE, allBooks.size());

        // Update the list of books to display on the current page
        currentPageBooks.setAll(allBooks.subList(startIndex, endIndex));
        listView.setItems(currentPageBooks);  // Update the ListView items
    }

    private void loadNextPage(ActionEvent event) {
        if ((currentPage * ITEMS_PER_PAGE) < allBooks.size()) {
            currentPage++;
            updateCurrentPageBooks();
        }
    }

    private void loadPreviousPage(ActionEvent event) {
        if (currentPage > 1) {
            currentPage--;
            updateCurrentPageBooks();
        }
    }

    @FXML
    private void handleKeyReleased() {
        String searchKeyWord = searchField.getText().toLowerCase();

        if (searchKeyWord.isEmpty()) {
            suggestionListView.setVisible(false);
        } else {
            ObservableList<BookItem> filteredSuggestionList = FXCollections.observableArrayList();
            Set<String> uniqueTitles = new HashSet<>(); // Set to keep track of unique titles

            // Filter suggestions based on the search keyword
            for (BookItem suggestion : suggestions) {
                String title = suggestion.getTitle().toLowerCase();
                if (title.contains(searchKeyWord) && uniqueTitles.add(title)) { // Only add if unique
                    filteredSuggestionList.add(suggestion);
                }
            }

            // Limit the number of results to between 5 and 7
            int maxResults = 7;
            int minResults = 0;

            if (filteredSuggestionList.size() > maxResults) {
                filteredSuggestionList = FXCollections.observableArrayList(filteredSuggestionList.subList(0, maxResults));
            } else if (filteredSuggestionList.size() < minResults) {
                while (filteredSuggestionList.size() < minResults) {
                    filteredSuggestionList.add(new BookItem("","")); // Add empty results
                }
            }

            // Set custom cell factory for suggestion list view
            suggestionListView.setCellFactory(param -> new ListCell<BookItem>() {
                @Override
                protected void updateItem(BookItem item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setGraphic(null);
                        setText(null);
                    } else {
                        HBox hbox = new HBox();
                        hbox.getChildren().addAll(new Label(item.getTitle()));
                        hbox.setSpacing(10);
                        setGraphic(hbox);
                    }
                }
            });

            // Set the filtered suggestions and make the suggestionListView visible
            suggestionListView.setItems(filteredSuggestionList);
            suggestionListView.setVisible(!filteredSuggestionList.isEmpty());

            double height = Math.min(filteredSuggestionList.size() * SEARCH_CELL_HEIGHT, 300);
            suggestionListView.setPrefHeight(height);
        }
    }


    @FXML
    private void populateSuggestions() {
        for (Book book : bookObservableList) {
            suggestions.add(new BookItem(book.getTitle(), book.getIsbn()));
        }
    }
}
