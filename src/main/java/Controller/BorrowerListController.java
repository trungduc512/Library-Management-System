package Controller;

import Model.Borrower;
import Model.Librarian;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BorrowerListController {

    @FXML
    private TableView<Borrower> borrowerTable;
    @FXML
    private TableColumn<Borrower, Integer> borrowerIdColumn;
    @FXML
    private TableColumn<Borrower, String> fullNameColumn;
    @FXML
    private TableColumn<Borrower, String> userNameColumn;
    @FXML
    private TableColumn<Borrower, String> statusColumn;
    @FXML
    private Label noBorrowersLabel;

    // Cache for borrower statuses
    private final Map<Integer, String> statusCache = new HashMap<>();

    @FXML
    public void initialize() {
        // Set up columns
        borrowerIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        fullNameColumn.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        userNameColumn.setCellValueFactory(new PropertyValueFactory<>("userName"));

        // Add a property for sorting based on the cached status
        statusColumn.setCellValueFactory(cellData -> {
            Borrower borrower = cellData.getValue();
            String status = statusCache.getOrDefault(borrower.getId(), "Loading");
            return new SimpleStringProperty(status);
        });

        // Set a custom cell factory for styling
        statusColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);

                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setText(null);
                    setStyle("");
                } else {
                    Borrower borrower = (Borrower) getTableRow().getItem();

                    // Reset to placeholder text if status is not cached
                    if (statusCache.containsKey(borrower.getId())) {
                        String cachedStatus = statusCache.get(borrower.getId());
                        setText(cachedStatus);
                        applyStyle(cachedStatus);
                    } else {
                        setText("Loading");
                        setStyle("-fx-text-fill: gray;");

                        // Fetch status asynchronously if not cached
                        Task<String> loadStatusTask = new Task<>() {
                            @Override
                            protected String call() {
                                return determineStatusLazy(borrower);
                            }
                        };

                        loadStatusTask.setOnSucceeded(event -> {
                            String fetchedStatus = loadStatusTask.getValue();
                            // Update cache once the status is fetched
                            statusCache.put(borrower.getId(), fetchedStatus);
                            setText(fetchedStatus);
                            applyStyle(fetchedStatus);

                            // Trigger a re-sort after updating
                            borrowerTable.sort();
                        });

                        loadStatusTask.setOnFailed(event -> {
                            setText("Error");
                            setStyle("-fx-text-fill: red;");
                        });

                        new Thread(loadStatusTask).start();
                    }
                }
            }

            private void applyStyle(String status) {
                if ("In good standing".equals(status)) {
                    setStyle("-fx-text-fill: green;");
                } else if (status.contains("Has overdue loans")) {
                    setStyle("-fx-text-fill: red;");
                } else {
                    setStyle("-fx-text-fill: black;");
                }
            }
        });


        // Custom comparator for sorting
        statusColumn.setComparator((status1, status2) -> {
            if (status1 == null || status2 == null) {
                return 0;
            }
            // Prioritize statuses in a specific order
            if ("In good standing".equals(status1)) return -1;
            if ("In good standing".equals(status2)) return 1;
            if (status1.contains("Has overdue loans") && !status2.contains("Has overdue loans")) return -1;
            if (status2.contains("Has overdue loans") && !status1.contains("Has overdue loans")) return 1;
            return status1.compareTo(status2); // Alphabetical order for others
        });

        // Load data into the table
        List<Borrower> borrowers = loadBorrowers();
        if (borrowers.isEmpty()) {
            noBorrowersLabel.setVisible(true);
            borrowerTable.setVisible(false);
        } else {
            noBorrowersLabel.setVisible(false);
            borrowerTable.getItems().addAll(borrowers);
        }
    }

    /**
     * Lazily determines the status of a Borrower.
     * Simulates a potentially expensive operation, such as a database query.
     */
    private String determineStatusLazy(Borrower borrower) {
        try {
            // Simulate delay for fetching status
            Thread.sleep(1000); // Simulate delay (e.g., database query)
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return borrower.returnStatus();
    }

    private List<Borrower> loadBorrowers() {
        // Replace with actual database query logic to fetch Borrowers
        return Librarian.listBorrowers();
    }
}
