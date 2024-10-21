package com.lms;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ProgressBar;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;

public class LoadController implements Initializable {

    @FXML
    private ProgressBar progressBar;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO, event -> progressBar.setProgress(0)),
                new KeyFrame(Duration.seconds(10), event -> progressBar.setProgress(1))
        );
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }
}
