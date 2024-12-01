package Controller;

import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.Objects;

/**
 * Utility class that provides helper methods for common controller operations
 * such as scene switching, fade transitions, and slide transitions.
 */
public class ControllerUtils {

    /**
     * Switches the scene within the current stage to the specified FXML file.
     *
     * @param sourceNode The source node used to retrieve the current stage.
     * @param fxmlFile   The path to the FXML file to load.
     * @throws IOException If the FXML file cannot be loaded.
     */
    public static void switchSceneWithinStage(Node sourceNode, String fxmlFile) throws IOException {
        Stage stage = (Stage) sourceNode.getScene().getWindow();
        Parent root = FXMLLoader.load(Objects.requireNonNull(ControllerUtils.class.getResource(fxmlFile)));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();
    }

    /**
     * Applies a fade transition effect to a specified node.
     *
     * @param node        The node to apply the fade effect to.
     * @param fromOpacity The starting opacity value (0.0 to 1.0).
     * @param toOpacity   The ending opacity value (0.0 to 1.0).
     * @param duration    The duration of the fade transition in seconds.
     */
    public static void fadeTransition(Node node, double fromOpacity, double toOpacity, double duration) {
        FadeTransition transition = new FadeTransition(Duration.seconds(duration), node);
        transition.setFromValue(fromOpacity);
        transition.setToValue(toOpacity);
        transition.setCycleCount(1);
        transition.play();
    }

    /**
     * Applies a horizontal slide transition to a specified node.
     *
     * @param node   The node to apply the slide transition to.
     * @param fromX  The starting X-coordinate for the transition.
     * @param toX    The ending X-coordinate for the transition.
     * @param second The duration of the slide transition in seconds.
     */
    public static void slideTransitionX(Node node, double fromX, double toX, double second) {
        node.setTranslateX(fromX);
        TranslateTransition slide = new TranslateTransition();
        slide.setDuration(Duration.seconds(second));
        slide.setNode(node);
        slide.setToX(toX);
        slide.play();
    }

    /**
     * Applies a vertical slide transition to a specified node.
     *
     * @param node   The node to apply the slide transition to.
     * @param fromY  The starting Y-coordinate for the transition.
     * @param toY    The ending Y-coordinate for the transition.
     * @param second The duration of the slide transition in seconds.
     */
    public static void slideTransitionY(Node node, double fromY, double toY, double second) {
        node.setTranslateY(fromY);
        TranslateTransition slide = new TranslateTransition();
        slide.setDuration(Duration.seconds(second));
        slide.setNode(node);
        slide.setToY(toY);
        slide.play();
    }
}
