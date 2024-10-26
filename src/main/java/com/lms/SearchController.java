package com.lms;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import jdk.jfr.Description;
import org.controlsfx.control.action.Action;
import services.GoogleBooksAPIClient;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.concurrent.Callable;

public class SearchController implements Initializable {

    @FXML
    private ScrollPane searchScrollPane;

    @FXML
    private Button searchButton;

    @FXML
    private VBox vbox;

    @FXML
    private TextField search;

    @FXML
    private ProgressBar progressBar;

    @FXML
    private void clicked(ActionEvent event) {
        String isbn = search.getText();
        loadDataInBackground(isbn);
    }

    private void loadDataInBackground(String isbn) {
        Task<GoogleBooksAPIClient> task = new Task<GoogleBooksAPIClient>() {
            @Override
            protected GoogleBooksAPIClient call() throws Exception {
                // Fetch data from the API in the background
                return new GoogleBooksAPIClient(isbn);
            }
        };

        task.setOnSucceeded(event -> {
            GoogleBooksAPIClient apiClient = task.getValue();
            Platform.runLater(() -> updateUI(apiClient));
        });

        task.setOnFailed(event -> {
            Platform.runLater(() -> {
                // Handle the failure (show error message, etc.)
                vbox.getChildren().clear();
                Label errorLabel = new Label("Failed to load data.");
                vbox.getChildren().add(errorLabel);
            });
        });

        Thread thread = new Thread(task);
        thread.setDaemon(true); // Allow the thread to terminate when the application exits
        thread.start();
    }

    private void updateUI(GoogleBooksAPIClient apiClient) {
        // Clear the VBox
        vbox.getChildren().clear();

        // Add Thumbnail on top
        String thumbnailURL = apiClient.getThumbnailURL();
        if (!thumbnailURL.isEmpty()) {
            Image thumbnail = new Image(thumbnailURL);
            ImageView thumbnailView = new ImageView(thumbnail);
            thumbnailView.setFitWidth(150); // Adjust the width as needed
            thumbnailView.setPreserveRatio(true);
            VBox imageContainer = new VBox(thumbnailView);
            imageContainer.setAlignment(Pos.CENTER);
            imageContainer.setStyle("-fx-padding: 10px 20px;"); // Added left and right padding
            vbox.getChildren().add(imageContainer);
        }

        // Add Title
        Label title = new Label(apiClient.getTitle());
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-alignment: center;");
        VBox titleContainer = new VBox(title);
        titleContainer.setAlignment(Pos.CENTER);
        titleContainer.setStyle("-fx-padding: 10px 20px; -fx-margin-bottom: 10px;");
        vbox.getChildren().add(titleContainer);

        // Add Authors label
        Label authorsLabelTitle = new Label("Authors:");
        authorsLabelTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-padding: 10px 20px 0 20px;");
        vbox.getChildren().add(authorsLabelTitle);

        // Add Authors content
        ArrayList<String> authorList = apiClient.getAuthors();
        if (!authorList.isEmpty()) {
            Label authorsLabel = new Label(String.join(", ", authorList));
            authorsLabel.setStyle("-fx-font-size: 16px; -fx-padding: 0 20px 10px 20px;"); // Adjusted padding for spacing
            vbox.getChildren().add(authorsLabel);
        }

        // Add Publisher label
        Label publisherLabelTitle = new Label("Publisher:");
        publisherLabelTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-padding: 10px 20px 0 20px;");
        vbox.getChildren().add(publisherLabelTitle);

        // Add Publisher content
        Label publisherLabel = new Label(apiClient.getPublisher());
        publisherLabel.setStyle("-fx-font-size: 16px; -fx-padding: 0 20px 10px 20px;"); // Adjusted padding for spacing
        vbox.getChildren().add(publisherLabel);

        // Add Description label
        Label descriptionLabelTitle = new Label("Description:");
        descriptionLabelTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-padding: 10px 20px 0 20px;");
        vbox.getChildren().add(descriptionLabelTitle);

        // Add Description content using TextFlow for justified text
        String descriptionText = apiClient.getDescription();
        Text descriptionContent = new Text(descriptionText);
        descriptionContent.setStyle("-fx-font-size: 16px;");
        TextFlow descriptionFlow = new TextFlow(descriptionContent);
        descriptionFlow.setTextAlignment(TextAlignment.JUSTIFY);
        descriptionFlow.setMaxWidth(Double.MAX_VALUE); // Allow full width
        descriptionFlow.setStyle("-fx-padding: 0 20px 10px 20px;"); // Adjusted padding for spacing
        vbox.getChildren().add(descriptionFlow);
    }





    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        searchScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        searchScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        search.setOnAction(event -> {
            try {
                clicked(event);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }
}
