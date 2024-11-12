package com.lms;

import classes.Book;
import classes.Borrower;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class SearchBookController implements Initializable {

    public static class BookItem {
        private String title;
        private String iconPath;

        public BookItem(String title, String iconPath) {
            this.title = title;
            this.iconPath = iconPath;
        }

        public String getTitle() {
            return title;
        }

        public String getIconPath() {
            return iconPath;
        }
    }

    @FXML
    private GridPane bookContainer;
    @FXML
    private TextField keywordTextField;

    @FXML
    private ListView<BookItem> suggestionListView;

    ObservableList<Book> bookObservableList = FXCollections.observableArrayList(Borrower.getAllBooks());
    ObservableList<BookItem> suggestions = FXCollections.observableArrayList();
    FilteredList<Book> filteredList;

    @Override
    public void initialize(URL url, ResourceBundle resource) {
        filteredList = new FilteredList<>(bookObservableList, b -> true);

        keywordTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            handleKeyReleased();
        });

        keywordTextField.setOnKeyPressed(event -> {
            if (Objects.requireNonNull(event.getCode()) == KeyCode.ENTER) {
                try {
                    handleSearch();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                suggestionListView.setVisible(false);
            }
        });

        populateSuggestions();
        suggestionListView.setOnMouseClicked(event -> {
            try {
                if (event.getClickCount() == 1) {
                    BookItem selectedItem = suggestionListView.getSelectionModel().getSelectedItem();
                    keywordTextField.setText(selectedItem.getTitle());
                    this.handleSearch();
                    suggestionListView.setVisible(false);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @FXML
    private void populateSuggestions() {
        for (Book book : bookObservableList) {
            suggestions.add(new BookItem(book.getTitle(), "/com/lms/images/search_loop.png"));
        }
    }

        if (searchKeyWord.isEmpty()) {
            suggestionListView.setVisible(false);
        } else {
            ObservableList<BookItem> filteredSuggestionList = FXCollections.observableArrayList();

            for (BookItem suggestion : suggestions) {
                if (suggestion.getTitle().toLowerCase().contains(searchKeyWord)) {
                    filteredSuggestionList.add(suggestion);
                }
            }

            suggestionListView.setCellFactory(param -> new ListCell<BookItem>() {
                @Override
                protected void updateItem(BookItem item, boolean empty) {
                    super.updateItem(item, empty);

                    if (empty || item == null) {
                        setGraphic(null);
                        setText(null);
                    } else {
                        HBox hbox = new HBox();
                        Image image = null;
                        try {
                            image = new Image(Objects.requireNonNull(getClass().getResourceAsStream(item.getIconPath())));
                        } catch (NullPointerException e) {
                            System.out.println("Icon path not found for " + item.getTitle());
                        }
                        ImageView imageView = new ImageView(image);
                        imageView.setFitWidth(15);
                        imageView.setFitHeight(15);
                        hbox.getChildren().addAll(imageView, new Label(item.getTitle()));
                        hbox.setSpacing(10);
                        setGraphic(hbox);
                    }
                }
            });

            boolean validListView = !filteredSuggestionList.isEmpty();
            suggestionListView.setItems(filteredSuggestionList);
            suggestionListView.setVisible(validListView);

            filteredList.setPredicate(book -> {
                if (searchKeyWord.isEmpty() || searchKeyWord.isBlank()) {
                    return false;
                }

                boolean matches = book.getTitle().toLowerCase().contains(searchKeyWord) ||
                        book.getAuthor().toLowerCase().contains(searchKeyWord) ||
                        book.getIsbn().toLowerCase().contains(searchKeyWord);

                return matches;
            });

            updateBookGrid();
        }
    }

    private void updateBookGrid() {
        bookContainer.getChildren().clear();
        int column = 0;
        int row = 1;
        for (Book book : filteredList) {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("book-card.fxml"));
            VBox bookCard;
            try {
                bookCard = fxmlLoader.load();
            } catch (IOException e) {
                e.printStackTrace();
                continue;
            }
            BookCardController cardController = fxmlLoader.getController();
            cardController.setData(book);

            if (column == 3) {
                column = 0;
                row++;
            }

            bookContainer.add(bookCard, column++, row);
            GridPane.setMargin(bookCard, new Insets(10));
        }

        if (filteredList.isEmpty()) {
            Label noResultsLabel = new Label("No books found.");
            noResultsLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: red;");
            GridPane.setConstraints(noResultsLabel, 0, 0, 1, 1);
            bookContainer.getChildren().add(noResultsLabel);
        }
    }

    @FXML
    private void handleSearch() throws Exception {
        suggestionListView.setVisible(false);

        String searchKeyWord = keywordTextField.getText().toLowerCase();

        filteredList.setPredicate(book -> {
            if (searchKeyWord.isEmpty() || searchKeyWord.isBlank()) {
                return false;
            }

            boolean matches = book.getTitle().toLowerCase().contains(searchKeyWord) ||
                    book.getAuthor().toLowerCase().contains(searchKeyWord) ||
                    book.getIsbn().toLowerCase().contains(searchKeyWord);

            return matches;
        });

        updateBookGrid();
    }
}
