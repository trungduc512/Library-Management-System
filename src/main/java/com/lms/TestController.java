package com.lms;

import classes.Book;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class TestController {

    private static final int ITEMS_PER_PAGE = 5;
    private static final int IMAGE_WIDTH = 80;
    private static final int IMAGE_HEIGHT = 120;

    private ObservableList<Book> books = FXCollections.observableArrayList();
    private final Map<String, Image> imageCache = new HashMap<>();
    private int currentPage = 1;
    private int totalItems = 0;

    @FXML
    private TableView<Book> bookTableView;
    @FXML
    private TableColumn<Book, String> titleColumn;
    @FXML
    private TableColumn<Book, String> authorColumn;
    @FXML
    private TableColumn<Book, String> isbnColumn;
    @FXML
    private TableColumn<Book, String> descriptionColumn;
    @FXML
    private TableColumn<Book, ImageView> imageColumn;

    @FXML
    private Button prevButton, nextButton;

    @FXML
    public void initialize() {
        configureTableColumns();
        loadBooksFromDatabase();

        // Pagination buttons
        prevButton.setOnAction(e -> loadPreviousPage());
        nextButton.setOnAction(e -> loadNextPage());
    }

    private void configureTableColumns() {
        titleColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTitle()));
        authorColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAuthor()));
        isbnColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getIsbn()));
        descriptionColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDescription()));

        // Lazy loading image for each book in the table
        imageColumn.setCellValueFactory(cellData -> {
            ImageView imageView = new ImageView();
            String imageUrl = cellData.getValue().getThumbnailURL();
            if (imageUrl != null && !imageUrl.trim().isEmpty()) {
                Image image = imageCache.computeIfAbsent(imageUrl, url -> new Image(url, IMAGE_WIDTH, IMAGE_HEIGHT, false, false, true));
                imageView.setImage(image);
            } else {
                Image placeholder = new Image(getClass().getResource("/com/lms/Images/Image-not-found.png").toExternalForm(), IMAGE_WIDTH, IMAGE_HEIGHT, false, false);
                imageView.setImage(placeholder);
            }
            return new javafx.beans.property.SimpleObjectProperty<>(imageView);
        });

        bookTableView.setItems(books);
    }

    private void loadBooksFromDatabase() {
        String url = "jdbc:mysql://localhost:3306/library_db";
        String user = "root";
        String password = "123456";

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            String countSql = "SELECT COUNT(*) FROM books";
            PreparedStatement countStmt = conn.prepareStatement(countSql);
            ResultSet countRs = countStmt.executeQuery();
            if (countRs.next()) {
                totalItems = countRs.getInt(1);
            }

            int remainingItems = totalItems - (currentPage - 1) * ITEMS_PER_PAGE;
            int itemsToLoad = Math.min(ITEMS_PER_PAGE, remainingItems);

            String sql = "SELECT * FROM books LIMIT ? OFFSET ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, itemsToLoad);
            stmt.setInt(2, (currentPage - 1) * ITEMS_PER_PAGE);

            ResultSet rs = stmt.executeQuery();
            books.clear();

            while (rs.next()) {
                books.add(new Book(rs.getString("title"), rs.getString("author"),
                        rs.getString("isbn"), rs.getString("description"),
                        rs.getInt("totalBooks"), rs.getInt("borrowedBooks"), rs.getString("thumbnailURL")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadNextPage() {
        if ((currentPage - 1) * ITEMS_PER_PAGE + books.size() < totalItems) {
            currentPage++;
            loadBooksFromDatabase();
        }
    }

    private void loadPreviousPage() {
        if (currentPage > 1) {
            currentPage--;
            loadBooksFromDatabase();
        }
    }
}
