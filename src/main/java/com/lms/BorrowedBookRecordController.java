package com.lms;

import classes.BorrowedBookRecord;
import classes.Borrower;
import classes.LMS;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TableCell;
import javafx.scene.control.Button;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.Label;
import javafx.util.Callback;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class BorrowedBookRecordController implements Initializable {

    @FXML
    private Label titleLabel;

    @FXML
    private Label noBooksLabel;

    @FXML
    private TableView<BorrowedBookRecord> borrowHistoryTable;

    @FXML
    private TableColumn<BorrowedBookRecord, String> borrowerIdColumn;

    @FXML
    private TableColumn<BorrowedBookRecord, String> titleColumn;

    @FXML
    private TableColumn<BorrowedBookRecord, String> isbnColumn;

    @FXML
    private TableColumn<BorrowedBookRecord, Integer> quantityColumn;

    @FXML
    private TableColumn<BorrowedBookRecord, String> borrowedDateColumn;

    @FXML
    private TableColumn<BorrowedBookRecord, Void> returnColumn;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Set the column resize policy
        borrowHistoryTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        borrowerIdColumn.setCellValueFactory(new PropertyValueFactory<>("borrowerId"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        isbnColumn.setCellValueFactory(new PropertyValueFactory<>("isbn"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        borrowedDateColumn.setCellValueFactory(new PropertyValueFactory<>("borrowedDate"));

        // Create a custom cell for the returnColumn
        returnColumn.setCellFactory(new Callback<TableColumn<BorrowedBookRecord, Void>, TableCell<BorrowedBookRecord, Void>>() {
            @Override
            public TableCell<BorrowedBookRecord, Void> call(TableColumn<BorrowedBookRecord, Void> param) {
                return new TableCell<>() {
                    private final Button returnButton = new Button("Return");

                    {
                        returnButton.setOnAction(event -> {
                            BorrowedBookRecord data = getTableView().getItems().get(getIndex());
                            // Handle the return book logic here
                            Borrower borrower = (Borrower) LMS.getInstance().getCurrentUser();
                            borrower.returnBook(data.getBorrowerId());
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
        Borrower borrower = (Borrower) LMS.getInstance().getCurrentUser();
        List<BorrowedBookRecord> borrowedBooks = borrower.listBorrowedBooks();
        ObservableList<BorrowedBookRecord> data = FXCollections.observableArrayList(borrowedBooks);
        borrowHistoryTable.setItems(data);

        // Show or hide components based on data
        if (borrowedBooks.isEmpty()) {
            borrowHistoryTable.setVisible(false);
            noBooksLabel.setVisible(true);
        } else {
            borrowHistoryTable.setVisible(true);
            noBooksLabel.setVisible(false);
        }

        // Adjust column widths proportionally
        borrowHistoryTable.widthProperty().addListener((observable, oldValue, newValue) -> {
            double tableWidth = borrowHistoryTable.getWidth();
            borrowerIdColumn.setPrefWidth(tableWidth * 0.1); // 15%
            titleColumn.setPrefWidth(tableWidth * 0.25); // 25%
            isbnColumn.setPrefWidth(tableWidth * 0.2); // 20%
            quantityColumn.setPrefWidth(tableWidth * 0.1); // 10%
            borrowedDateColumn.setPrefWidth(tableWidth * 0.2); // 20%
            returnColumn.setPrefWidth(tableWidth * 0.15); // 10%
        });
    }
}
