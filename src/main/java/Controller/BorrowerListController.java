package Controller;

import Model.Borrower;
import Model.Librarian;
import com.jfoenix.controls.JFXButton;
import javafx.animation.PauseTransition;
import javafx.animation.TranslateTransition;
import javafx.beans.property.SimpleStringProperty;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import javafx.util.Duration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class BorrowerListController {

    private final GaussianBlur blurEffect = new GaussianBlur(10);
    // Cache for borrower statuses
    private final Map<Integer, String> statusCache = new HashMap<>();
    @FXML
    private StackPane borrowerListStackPane;
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
    // change information properties
    @FXML
    private TableColumn<Borrower, Void> changeInformationColumn;
    @FXML
    private VBox changeUserInformationBox;
    @FXML
    private TextField userNameField;
    private Borrower currentBorrower;

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
                    Borrower borrower = getTableRow().getItem();

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

        changeInformationColumn.setCellFactory(new Callback<TableColumn<Borrower, Void>, TableCell<Borrower, Void>>() {

            @Override
            public TableCell<Borrower, Void> call(TableColumn<Borrower, Void> param) {

                return new TableCell<>() {
                    private final JFXButton changeButton = new JFXButton();
                    private final ImageView imageView = new ImageView(
                            new Image(Objects.requireNonNull(getClass().getResourceAsStream("/View/Images/edit_icon.png"))));

                    {
                        imageView.setFitHeight(20);
                        imageView.setFitWidth(20);
                        changeButton.setGraphic(imageView);
                        changeButton.setOnAction(event -> {
                            currentBorrower = getTableView().getItems().get(getIndex());
                            toggleChangeBoxVisibility();
                        });
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);

                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(changeButton);
                        }
                    }
                };
            }
        });

        borrowerTable.widthProperty().addListener((observable, oldValue, newValue) -> {
            double tableWidth = borrowerTable.getWidth();
            borrowerIdColumn.setPrefWidth(tableWidth * 0.05);
            fullNameColumn.setPrefWidth(tableWidth * 0.32);
            userNameColumn.setPrefWidth(tableWidth * 0.32);
            statusColumn.setPrefWidth(tableWidth * 0.26);
            changeInformationColumn.setPrefWidth(tableWidth * 0.05);
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

    @FXML
    private void toggleChangeBoxVisibility() {
        boolean isVisibleBefore = changeUserInformationBox.isVisible();

        if (!isVisibleBefore) {
            ControllerUtils.slideTransitionY(changeUserInformationBox, -70, 0, 0.5);
            ControllerUtils.fadeTransition(changeUserInformationBox, 0, 1, 0.5);

            borrowerTable.setDisable(true);
            borrowerTable.setEffect(blurEffect);
            userNameField.setText(currentBorrower.getFullName());
        } else {
            borrowerTable.setDisable(false);
            borrowerTable.setEffect(null);
        }

        changeUserInformationBox.setVisible(!isVisibleBefore);
    }

    // submit user information change
    @FXML
    private void submitChanges() {
        String name = userNameField.getText();

        if (currentBorrower != null) {
            int rowIndex = borrowerTable.getItems().indexOf(currentBorrower);
            currentBorrower.setFullName(name);
            currentBorrower.updateProfile(name);

            showChangeInfoNotification();
            borrowerTable.getItems().set(rowIndex, currentBorrower);
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

    private void showChangeInfoNotification() {
        // Create the notification label
        Label notificationLabel = new Label("Change successfully!");
        notificationLabel.setPrefHeight(37.0);
        notificationLabel.setPrefWidth(175.0);
        notificationLabel.setStyle("-fx-background-color: #73B573; -fx-text-fill: white; -fx-padding: 10px; -fx-background-radius: 0.5em;");
        notificationLabel.setVisible(false);

        // Load the image and set it as the graphic
        Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/View/Images/add-book-checked-icon.png")));
        ImageView imageView = new ImageView(image);
        imageView.setFitHeight(17.0);
        imageView.setFitWidth(17.0);
        imageView.setPickOnBounds(true);
        imageView.setPreserveRatio(true);
        notificationLabel.setGraphic(imageView);

        // Add the notification label to the scene
        borrowerListStackPane.getChildren().add(notificationLabel);
        notificationLabel.setVisible(true);

        // Animation for sliding in
        TranslateTransition slideIn = new TranslateTransition(Duration.seconds(0.5), notificationLabel);
        ControllerUtils.fadeTransition(notificationLabel, 0, 1, 0.5);
        slideIn.setFromX(400); // Start from the right
        slideIn.setFromY(-230); // Start above the screen
        slideIn.setToX(320); // Slide to the center

        slideIn.setOnFinished(slideInEvent -> {
            PauseTransition delay = new PauseTransition(Duration.seconds(0.3));
            delay.setOnFinished(delayEvent -> {
                // Animation for sliding up to disappear
                TranslateTransition slideUp = new TranslateTransition(Duration.seconds(0.8), notificationLabel);
                ControllerUtils.fadeTransition(notificationLabel, 1, 0, 0.8);
                slideUp.setToY(-250); // Slide up
                slideUp.setOnFinished(event1 -> borrowerListStackPane.getChildren().remove(notificationLabel));
                slideUp.play();
            });
            delay.play();
        });

        slideIn.play();
    }
}