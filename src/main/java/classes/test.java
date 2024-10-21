package classes;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class test extends Application {

    @Override
    public void start(Stage primaryStage) {
        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.setEditable(true);
        ObservableList<String> items = FXCollections.observableArrayList();
        // Fetch data from database and add to items list
        items.add("Item 1");
        items.add("Item 2");
        items.add("Item 3");
        comboBox.setItems(items);
        comboBox.show();

        VBox vbox = new VBox();
        vbox.getChildren().add(comboBox);

        Scene scene = new Scene(vbox, 300, 200);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

