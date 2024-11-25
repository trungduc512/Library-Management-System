package Model;

import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class Thesis extends Document {
    private long id;
    private String university;

    public Thesis() {
    }

    public Thesis(String university) {
        this.university = university;
    }

    public Thesis(String title, String author, String description, int totalTheses,
                  int borrowedTheses, String thumbnailURL, String university) {
        super(title, author, description, thumbnailURL, totalTheses, borrowedTheses);
        this.university = university;
    }

    @Override
    public VBox getInfo() {

        Label titleLabel = new Label(this.getTitle());
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 16));

        VBox textContainer = new VBox(
                titleLabel,
                new Label("Author: " + this.getAuthor()),
                new Label("University: " + this.getUniversity()),
                new Label("Total theses: " + this.getTotalDocument()),
                new Label("Borrowed theses: " + this.getBorrowedDocument())
        );
        textContainer.setSpacing(5);

        return textContainer;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUniversity() {
        return university;
    }

    public void setUniversity(String university) {
        this.university = university;
    }


    @Override
    public void printDetails() {
        String details = "Thesis{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", university='" + university + '\'' +
                ", description='" + description + '\'' +
                ", totalTheses=" + totalDocument + '\'' +
                ", borrowedTheses=" + borrowedDocument + '\'' +
                ", thumbnailURL='" + thumbnailURL + '\'' +
                '}';
        System.out.println(details);
    }
}
