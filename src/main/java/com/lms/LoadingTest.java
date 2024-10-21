package com.lms;

import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class LoadingTest extends Application {

    @Override
    public void start(Stage primaryStage) {
        Label label = new Label("Loading data...");
        ProgressIndicator progressIndicator = new ProgressIndicator();

        VBox vbox = new VBox(label, progressIndicator);
        Scene scene = new Scene(vbox, 200, 150);

        primaryStage.setScene(scene);
        primaryStage.show();

        // Start data loading in a separate thread
        loadDataInBackground(label, progressIndicator);
    }

    private void loadDataInBackground(Label label, ProgressIndicator progressIndicator) {
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                // Simulate loading data
                for (int i = 0; i < 10; i++) {
                    Thread.sleep(300); // Simulate delay
                    updateProgress(i + 1, 10);
                }
                return null;
            }
        };

        // Update UI based on task progress
        task.setOnSucceeded(event -> {
            label.setText("Data loaded successfully!");
            progressIndicator.setVisible(false);
        });
        task.setOnFailed(event -> {
            label.setText("Failed to load data.");
            progressIndicator.setVisible(false);
        });

        progressIndicator.progressProperty().bind(task.progressProperty());

        Thread thread = new Thread(task);
        thread.setDaemon(true); // Allow the thread to terminate when the application exits
        thread.start();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
