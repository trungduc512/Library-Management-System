package com.lms;

import classes.Borrower;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import classes.Book;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.ResourceBundle;

public class SearchBookController implements Initializable {
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
    private TableColumn<Book, String> bookDescriptionTableColumn;
    @FXML
    private TextField keywordTextField;
    @FXML
    private ListView<String> suggestionListView;
    @FXML
    private Button searchButton;

    ObservableList<Book> bookObservableList = FXCollections.observableArrayList(Borrower.getAllBooks());
    ObservableList<String> suggestions = FXCollections.observableArrayList();
    FilteredList<Book> filteredList;

    @Override
    public void initialize(URL url, ResourceBundle resource) {

        bookTitleTableColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        bookAuthorTableColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
        bookISBNTableColumn.setCellValueFactory(new PropertyValueFactory<>("isbn"));
        bookAvailableTableColumn.setCellValueFactory(new PropertyValueFactory<>("totalBooks"));
        bookDescriptionTableColumn.setCellValueFactory(new PropertyValueFactory<>("description"));

        filteredList = new FilteredList<>(bookObservableList, b -> true);

        SortedList<Book> sortedData = new SortedList<>(filteredList);
        sortedData.comparatorProperty().bind(bookTableView.comparatorProperty());
        bookTableView.setItems(sortedData);

        addSuggestions();

        suggestionListView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                String selectedItem = suggestionListView.getSelectionModel().getSelectedItem();
                keywordTextField.setText(selectedItem);
                suggestionListView.setVisible(false);
            }
        });
    }

    @FXML
    private void addSuggestions() {
        for (Book book : bookObservableList) {
            suggestions.add(book.getTitle() + " - " + book.getAuthor());
        }
    }

    @FXML
    private void handleKeyReleased() {
        String searchKeyWord = keywordTextField.getText().toLowerCase();

        if (searchKeyWord.isEmpty()) {
            suggestionListView.setVisible(false);
        } else {
            ObservableList<String> filteredSuggestionList = FXCollections.observableArrayList();

            // add titles that match with input
            for (String suggestion : suggestions) {
                if (suggestion.toLowerCase().contains(searchKeyWord)) {
                    filteredSuggestionList.add(suggestion);
                }
            }

            // update suggestion list view and show it
            boolean validListView = !filteredSuggestionList.isEmpty();
            suggestionListView.setItems(filteredSuggestionList);
            suggestionListView.setVisible(validListView);
        }
    }

    @FXML
    private void handleSearch() {
        // turn off suggestion list view
        suggestionListView.setVisible(false);
        String searchKeyWord = keywordTextField.getText().toLowerCase();

        filteredList.setPredicate(book -> {
            if (searchKeyWord.isEmpty() || searchKeyWord.isBlank()) {
                return true;
            }

            String[] keywords = searchKeyWord.split("\\s+");

            for (String keyword : keywords) {
                boolean matches = book.getTitle().toLowerCase().contains(keyword) ||
                        book.getAuthor().toLowerCase().contains(keyword) ||
                        book.getDescription().toLowerCase().contains(keyword) ||
                        book.getIsbn().toLowerCase().contains(keyword) ||
                        Integer.toString(book.getTotalBooks()).contains(keyword);
                if (matches) {
                    return true;
                }
            }

            return false;
        });
    }
}
