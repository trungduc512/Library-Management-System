package classes;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class test extends Application {
    public static class Person {
        private String name;
        private int age;

        public Person(String name, int age) {
            this.name = name;
            this.age = age;
        }

        public String getName() {
            return name;
        }

        public int getAge() {
            return age;
        }
    }

    @Override
    public void start(Stage stage) {
        TableView<Person> tableView = new TableView<>();

        TableColumn<Person, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Person, Integer> ageColumn = new TableColumn<>("Age");
        ageColumn.setCellValueFactory(new PropertyValueFactory<>("age"));

        tableView.getColumns().add(nameColumn);
        tableView.getColumns().add(ageColumn);

        tableView.getItems().add(new Person("Alice", 25));
        tableView.getItems().add(new Person("Bob", 30));

        tableView.setRowFactory(tv -> {
            TableRow<Person> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty()) {
                    Person person = row.getItem();
                    System.out.println("Clicked on: " + person.getName() + ", " + person.getAge());
                }
            });
            return row;
        });

        StackPane root = new StackPane(tableView);
        Scene scene = new Scene(root, 400, 300);
        stage.setScene(scene);
        stage.setTitle("Clickable TableView Example");
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
