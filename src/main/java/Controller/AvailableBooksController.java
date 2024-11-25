package Controller;

import Model.*;
import com.jfoenix.controls.JFXButton;
import javafx.animation.PauseTransition;
import javafx.animation.TranslateTransition;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Callback;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.*;

public class AvailableBooksController implements Initializable {  // Implement Initializable
    private static final int ITEMS_PER_PAGE = 4;
    private static final int IMAGE_HEIGHT = 118;
    private static final int IMAGE_WIDTH = 80;
    private static final double SEARCH_CELL_HEIGHT = 25;
    private final GaussianBlur blurEffect = new GaussianBlur(10);

    private ObservableList<Document> allDocument = FXCollections.observableArrayList(); // All books
    private ObservableList<Document> currentPageDocument = FXCollections.observableArrayList(); // Books for current page
    private int currentPage = 1;
    private final Map<String, Image> imageCache = new HashMap<>();

    private MenuController menuController;

    @FXML
    private StackPane availableBooksPane;

    @FXML
    private ListView<Document> listView;

    @FXML
    private ListView<DocumentItem> suggestionListView;

    @FXML
    private TextField searchField;

    @FXML
    private Button prevButton;

    @FXML
    private Button nextButton;

    @FXML
    private ChoiceBox<String> documentShowingChoiceBox;

    // add document properties
    @FXML
    private VBox addDocumentContainer;
    @FXML
    private ChoiceBox<String> documentTypeChoiceBox;
    @FXML
    private TextField titleField;
    @FXML
    private TextField authorField;
    @FXML
    private TextField universityField;
    @FXML
    private TextArea descriptionField;
    @FXML
    private TextField totalDocumentField;
    @FXML
    private TextField thumbnailURLField;
    @FXML
    private JFXButton addDocButton;
    @FXML
    private JFXButton showDocumentFormButton;
    @FXML
    private Label addDocumentStatusText;

    public void setMenuController(MenuController menuController) {
        this.menuController = menuController;
    }

    public static class DocumentItem {
        private String title;
        private String id;

        public DocumentItem(String title, String id) {
            this.title = title;
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public String getId() {return id;}
    }

    private static ObservableList<Document> bookObservableList = FXCollections.observableArrayList(User.getAllBooks());
    private static ObservableList<DocumentItem> suggestions = FXCollections.observableArrayList();
    private static FilteredList<Document> filteredList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        documentShowingChoiceBox.setValue("Book");
        documentTypeChoiceBox.setValue("Thesis");

        loadDocumentFromDatabase(); // Load books from database

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
                DocumentItem selectedItem = suggestionListView.getSelectionModel().getSelectedItem();
                if (selectedItem != null) {
                    menuController.toSearchScreen(selectedItem.getId());
                }
            }
        });

        // Set custom cell factory for ListView to display book info and images
        listView.setCellFactory(new Callback<ListView<Document>, ListCell<Document>>() {
            @Override
            public ListCell<Document> call(ListView<Document> param) {
                return new ListCell<>() {
                    private final ImageView imageView = new ImageView();
                    private final VBox vbox = new VBox();
                    private final Image placeholderImage = new Image(
                            getClass().getResource("/View/Images/Image-not-found.png").toExternalForm(),
                            IMAGE_WIDTH, IMAGE_HEIGHT, false, false);

                    @Override
                    protected void updateItem(Document item, boolean empty) {
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
                            // Case 1: No URL provided
                            imageView.setImage(placeholderImage); // Show placeholder
                        } else if (imageCache.containsKey(imageUrl)) {
                            // Case 2: URL exists in cache
                            imageView.setImage(imageCache.get(imageUrl)); // Use cached image
                        } else {
                            try {
                                // Attempt to load the image from the provided URL
                                Image bookImage = new Image(imageUrl, IMAGE_WIDTH, IMAGE_HEIGHT, false, false, true);

                                // Temporarily show the placeholder image while the main image loads
                                imageView.setImage(placeholderImage);

                                // Listen for loading progress
                                bookImage.progressProperty().addListener((observable, oldValue, newValue) -> {
                                    if (newValue.doubleValue() == 1.0) { // Fully loaded
                                        if (item.getThumbnailURL().equals(imageUrl)) { // Verify URL matches
                                            imageCache.put(imageUrl, bookImage); // Cache the loaded image
                                            imageView.setImage(bookImage); // Display the loaded image
                                        }
                                    }
                                });

                                // Listen for errors during image loading
                                bookImage.errorProperty().addListener((obs, oldError, newError) -> {
                                    if (newError) {
                                        // Set the placeholder if loading fails
                                        imageView.setImage(placeholderImage);
                                    }
                                });
                            } catch (IllegalArgumentException e) {
                                // Handle invalid URL or inaccessible resource
                                imageView.setImage(placeholderImage); // Show placeholder immediately
                            }
                        }


                        VBox textContainer = item.getInfo();

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
                Document selectedDoc = listView.getSelectionModel().getSelectedItem();
                if (selectedDoc != null) {
                    try {
                        handleListViewDoubleClick(selectedDoc);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });

        documentShowingChoiceBox.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                loadDocumentFromDatabase();
            }
        });

        if (LMS.getInstance().getCurrentUser() instanceof Librarian) {
            showDocumentFormButton.setDisable(false);
            showDocumentFormButton.setVisible(true);
        } else {
            showDocumentFormButton.setDisable(true);
            showDocumentFormButton.setVisible(false);
        }

        // Set actions for pagination buttons
        prevButton.setOnAction(this::loadPreviousPage);
        nextButton.setOnAction(this::loadNextPage);
    }

    @FXML
    private void toggleAddDocumentVisibility() {
        boolean isVisibleBefore = addDocumentContainer.isVisible();
        addDocumentContainer.setVisible(!isVisibleBefore);

        if (!isVisibleBefore) {
            ControllerUtils.slideTransitionY(addDocumentContainer, -120, 0, 0.5);
            ControllerUtils.fadeTransition(addDocumentContainer, 0, 1, 0.5);

            listView.setDisable(true);
            listView.setEffect(blurEffect);

            prevButton.setVisible(false);
            nextButton.setVisible(false);
            searchField.setVisible(false);
            showDocumentFormButton.setVisible(false);
            documentShowingChoiceBox.setVisible(false);
        } else {
            listView.setDisable(false);
            listView.setEffect(null);

            prevButton.setVisible(true);
            nextButton.setVisible(true);
            searchField.setVisible(true);
            showDocumentFormButton.setVisible(true);
            documentShowingChoiceBox.setVisible(true);
        }
    }

    @FXML
    private void addNewDocument() {
        String selectedType = documentTypeChoiceBox.getValue();

        String title = titleField.getText();
        String author = authorField.getText();
        String description = descriptionField.getText();
        String thumbnailURL = thumbnailURLField.getText();

        if (selectedType.equals("Thesis")) {
            String university = universityField.getText();

            if (title.isEmpty() || author.isEmpty() || description.isEmpty() ||
                    university.isEmpty() || totalDocumentField == null ||
                    !totalDocumentField.getText().matches("\\d+")) {
                addDocumentStatusText.setText("Please finish your document!");
                addDocumentStatusText.setStyle("-fx-text-fill: red; -fx-font-size: 14px;");
                addDocumentStatusText.setVisible(true);
                return;
            }

            int totalDoc = Integer.parseInt(totalDocumentField.getText());
            Thesis newThesis = new Thesis(title, author, description, totalDoc, 0, thumbnailURL, university);

            ((Librarian) LMS.getInstance().getCurrentUser()).addThesis(title, author, university, description, totalDoc, thumbnailURL);

            showAddedDocumentNotification();
            addDocumentStatusText.setVisible(false);
        }

        clearForm();
    }

    private void clearForm() {
        titleField.clear();
        authorField.clear();
        universityField.clear();
        descriptionField.clear();
        totalDocumentField.clear();
        thumbnailURLField.clear();
        addDocumentStatusText.setText("");
    }

    private void handleListViewDoubleClick(Document selectedDoc) throws IOException {
        menuController.toSearchScreen(selectedDoc);
    }

    private void handleSuggestionListViewDoubleClick(DocumentItem selectedBook) throws IOException {
        menuController.toSearchScreen(selectedBook.getId());
    }

    private void loadDocumentFromDatabase() {
        allDocument.clear();
        bookObservableList.clear();
        currentPage = 1;

        String selectedType = documentShowingChoiceBox.getValue();
        if (selectedType == null) {
            System.out.println("No document type selected");
            return;
        }

        if (documentShowingChoiceBox.getValue().equals("Book")) {
            ArrayList<Book> list = User.getAllBooks();
            allDocument.setAll(list);
            bookObservableList.setAll(list);
        } else {
            ArrayList<Thesis> list = User.getAllThesis();
            allDocument.setAll(User.getAllThesis());
            bookObservableList.setAll(list);
        }

        populateSuggestions();
        updateCurrentPageBooks();
    }

    private void updateCurrentPageBooks() {
        // Calculate the start and end index for the current page
        int startIndex = (currentPage - 1) * ITEMS_PER_PAGE;
        int endIndex = Math.min(startIndex + ITEMS_PER_PAGE, allDocument.size());

        // Update the list of books to display on the current page
        currentPageDocument.setAll(allDocument.subList(startIndex, endIndex));
        listView.setItems(currentPageDocument);  // Update the ListView items
    }

    private void loadNextPage(ActionEvent event) {
        if ((currentPage * ITEMS_PER_PAGE) < allDocument.size()) {
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
            ObservableList<DocumentItem> filteredSuggestionList = FXCollections.observableArrayList();
            Set<String> uniqueTitles = new HashSet<>(); // Set to keep track of unique titles

            // Filter suggestions based on the search keyword
            for (DocumentItem suggestion : suggestions) {
                String title = suggestion.getTitle().toLowerCase();
                if (title.contains(searchKeyWord) && uniqueTitles.add(title)) { // Only add if unique
                    filteredSuggestionList.add(suggestion);
                }
            }

            // Limit the number of results to between 0 and 7
            int maxResults = 7;
            int minResults = 0;

            if (filteredSuggestionList.size() > maxResults) {
                filteredSuggestionList = FXCollections.observableArrayList(filteredSuggestionList.subList(0, maxResults));
            } else if (filteredSuggestionList.size() < minResults) {
                while (filteredSuggestionList.size() < minResults) {
                    filteredSuggestionList.add(new DocumentItem("","")); // Add empty results
                }
            }

            // Set custom cell factory for suggestion list view
            suggestionListView.setCellFactory(param -> new ListCell<DocumentItem>() {
                @Override
                protected void updateItem(DocumentItem item, boolean empty) {
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
        suggestions.clear();

        for (Document doc : bookObservableList) {
            String id;
            if (doc instanceof Book) {
                id = ((Book) doc).getIsbn();
            } else {
                id = Long.toString(((Thesis) doc).getId());
            }
            suggestions.add(new DocumentItem(doc.getTitle(), id));
        }
    }

    private void showAddedDocumentNotification() {
        // Create the notification label
        Label notificationLabel = new Label("Document added successfully!");
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
        availableBooksPane.getChildren().add(notificationLabel);
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
                slideUp.setOnFinished(event1 -> availableBooksPane.getChildren().remove(notificationLabel));
                slideUp.play();
            });
            delay.play();
        });

        slideIn.play();
    }
}
