package Controller;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.application.Platform;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;

public class HomeController implements Initializable {

    private VBox content;
    private MenuController menuController;
    private HomeContentController homeContentController;

    @FXML
    private StackPane coverPane;

    @FXML
    private ScrollPane homePane;

    @FXML
    private VBox vbox;

    public void setMenuController(MenuController menuController) {
        this.menuController = menuController;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        homePane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        homePane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        loadScreen();
    }

    private void loadScreen() {
        Rectangle overlay = new Rectangle(coverPane.getWidth(), coverPane.getHeight(), Color.rgb(0, 0, 0, 0.1));
        overlay.setDisable(true); // Make sure the overlay doesn't block interactions

        ProgressIndicator loadingIndicator = new ProgressIndicator();
        loadingIndicator.setMaxSize(50, 50); // Adjust size if needed

        StackPane stackPane = new StackPane(overlay, loadingIndicator);
        stackPane.setAlignment(Pos.CENTER);
        coverPane.getChildren().add(stackPane);
        Task<VBox> loadContent = new Task<VBox>() {
            @Override
            protected VBox call() throws Exception {
                FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/View/HomeContent-View.fxml")));
                VBox box = loader.load();
                homeContentController = loader.getController();
                homeContentController.setMenuController(menuController);
                return box;
            }
        };

        loadContent.setOnSucceeded(event -> {
            coverPane.getChildren().remove(stackPane);
            content = loadContent.getValue();
            vbox.getChildren().add(content);
        });

        Thread thread = new Thread(loadContent);
        thread.setDaemon(true);
        thread.start();
    }
}
