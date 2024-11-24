package classes;

import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class Book extends Document {
    private String isbn;

    // Constructor
    public Book(String title, String author, String isbn, String description, int totalBooks,
        int borrowedBooks, String thumbnailURL) {
        super(title, author, description, thumbnailURL, totalBooks, borrowedBooks);
        this.isbn = isbn;
    }

    public Book() {
    }

    public String getIsbn() {
      return isbn;
    }

    public void setIsbn(String isbn) {
      this.isbn = isbn;
    }

    @Override
    public VBox getInfo() {
        Label titleLabel = new Label(this.getTitle());
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 16));

        VBox textContainer = new VBox(
                titleLabel,
                new Label("Author: " + this.getAuthor()),
                new Label("ISBN: " + this.getIsbn()),
                new Label("Total books: " + this.getTotalDocument()),
                new Label("Borrowed books: " + this.getBorrowedDocument())
        );
        textContainer.setSpacing(5);

        return textContainer;
    }

    // Hàm in ra thông tin sách
    public void printDetails() {
        String details = "Document{" +
                "title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", totalBooks='" + totalDocument + '\'' +
                ", borrowedBooks='" + borrowedDocument + '\'' +
                ", description='" + description + '\'' +
                ", thumbnailURL='" + thumbnailURL + '\'' +
                '}';
        System.out.println(details);
    }
}

