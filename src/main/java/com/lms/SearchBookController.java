package com.lms;

import classes.Borrower;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import classes.Book;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
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

    /**
    @FXML
    private TableView<Book> bookTableView;
    @FXML
    private TableColumn<Book, String> bookTitleTableColumn;
    @FXML
    private TableColumn<Book, String> bookAuthorTableColumn;
    @FXML
    private TableColumn<Book, String> bookISBNTableColumn;
    @FXML
    private TableColumn<Book, Integer> bookAvailableTableColumn;
    @FXML
    private TextField keywordTextField;
    @FXML
    private ListView<BookItem> suggestionListView;
    @FXML
    private Button searchButton;
    */

    @FXML
    private GridPane bookContainer;

    @FXML
    private VBox bookListVBox;
    @FXML
    private TextField keywordTextField;
    @FXML
    private ListView<BookItem> suggestionListView;

    ObservableList<Book> bookObservableList = FXCollections.observableArrayList(Borrower.getAllBooks());
    ObservableList<BookItem> suggestions = FXCollections.observableArrayList();
    FilteredList<Book> filteredList;

    @Override
    public void initialize(URL url, ResourceBundle resource) {

        /**
        bookTitleTableColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        bookAuthorTableColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
        bookISBNTableColumn.setCellValueFactory(new PropertyValueFactory<>("isbn"));
        bookAvailableTableColumn.setCellValueFactory(new PropertyValueFactory<>("totalBooks"));
        //     bookDescriptionTableColumn.setCellValueFactory(new PropertyValueFactory<>("description"));

         */

        /**
        keywordTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredList.setPredicate(book ->{

                if (newValue.isEmpty() || newValue.isBlank()) {
                    return true;
                }

                String keyword = newValue.toLowerCase();

                return book.getTitle().toLowerCase().contains(keyword) ||
                        book.getAuthor().toLowerCase().contains(keyword) ||
                        // book.getDescription().toLowerCase().contains(keyword) ||
                        book.getIsbn().toLowerCase().contains(keyword) ||
                        Integer.toString(book.getTotalBooks()).contains(keyword);
            });
        });
        SortedList<Book> sortedList = new SortedList<>(filteredList);
        sortedList.comparatorProperty().bind(bookTableView.comparatorProperty());
        bookTableView.setItems(sortedList);
        */
        filteredList = new FilteredList<>(bookObservableList, b -> true);

        // search when type in text field
        keywordTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            handleKeyReleased();
        });
        // search when press enter
        keywordTextField.setOnKeyPressed(event -> {
            if (Objects.requireNonNull(event.getCode()) == KeyCode.ENTER) {
                try {
                    handleSearch();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                suggestionListView.setVisible(false);
            }
        });

        populateSuggestions();
        suggestionListView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                BookItem selectedItem = suggestionListView.getSelectionModel().getSelectedItem();
                keywordTextField.setText(selectedItem.getTitle());
                suggestionListView.setVisible(false);
            }
        });
    }

    @FXML
    private void populateSuggestions() {
        for (Book book : bookObservableList) {
            suggestions.add(new BookItem(book.getTitle(), "/com/lms/images/image.png"));
        }
    }

    @FXML
    private void handleKeyReleased() {
        String searchKeyWord = keywordTextField.getText().toLowerCase();

        if (searchKeyWord.isEmpty()) {
            suggestionListView.setVisible(false);
        } else {
            ObservableList<BookItem> filteredSuggestionList = FXCollections.observableArrayList();

            // add titles that match with input
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
                        Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream(item.getIconPath())));
                        ImageView imageView = new ImageView(image);
                        imageView.setFitWidth(30);
                        imageView.setFitHeight(30);
                        hbox.getChildren().addAll(imageView, new Label(item.getTitle()));
                        hbox.setSpacing(10);
                        setGraphic(hbox);
                    }
                }
            });

            // update suggestion list view and show it
            boolean validListView = !filteredSuggestionList.isEmpty();
            suggestionListView.setItems(filteredSuggestionList);
            suggestionListView.setVisible(validListView);
        }
    }

    @FXML
    private void handleSearch() throws IOException {
        // turn off suggestion list view
        suggestionListView.setVisible(false);

        /**
        bookListVBox.getChildren().clear();
        String searchKeyWord = keywordTextField.getText().toLowerCase();

        filteredList.setPredicate(book -> {
            if (searchKeyWord.isEmpty() || searchKeyWord.isBlank()) {
                return true;
            }

            String[] keywords = searchKeyWord.split("\\s+");

            for (String keyword : keywords) {
                boolean matches = book.getTitle().toLowerCase().contains(keyword) ||
                        book.getAuthor().toLowerCase().contains(keyword) ||
                        book.getIsbn().toLowerCase().contains(keyword) ||
                        Integer.toString(book.getTotalBooks()).contains(keyword);
                if (matches) {
                    return true;
                }
            }

            return false;
        });

        for (Book book : filteredList) {
            HBox bookItem = new HBox();
            bookItem.setSpacing(30);

            // handle click
            bookItem.setOnMouseClicked(event-> {
                System.out.println("Clicked " + book.getTitle());
            });

            // add border
            bookItem.setStyle("-fx-border-color: #cccccc; -fx-border-width: 1; -fx-padding: 10; -fx-background-color: #f9f9f9;");

            ImageView imageView = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/lms/images/image.png"))));
            imageView.setFitWidth(50);
            imageView.setFitHeight(50);

            Label titleLabel = new Label(book.getTitle());
            titleLabel.setStyle("-fx-font-size: 16px;");
            Label authorLabel = new Label("by " + book.getAuthor());
            authorLabel.setStyle("-fx-font-size: 16px;");

            bookItem.getChildren().addAll(imageView, titleLabel, authorLabel);
            bookListVBox.setSpacing(10);
            bookListVBox.getChildren().add(bookItem);
        }

         */

        int column = 0;
        int row = 1;
        for (Book book : filteredList) {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("book-card.fxml"));
            HBox bookCard = fxmlLoader.load();
            BookCardController cardController = fxmlLoader.getController();
            cardController.setData(book);

            if (column == 2) {
                column = 0;
                row++;
            }

            bookContainer.add(bookCard, column++, row);
            GridPane.setMargin(bookCard, new Insets(10));
        }
    }
}
