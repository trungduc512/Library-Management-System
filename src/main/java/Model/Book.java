package Model;

import java.util.HashMap;
import java.util.Map;

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
    public Map<String, String> getInfo() {
        Map<String, String> info = new HashMap<>();
        info.put("Title", this.getTitle());
        info.put("Author", this.getAuthor());
        info.put("ISBN", this.getIsbn());
        info.put("Total books", String.valueOf(this.getTotalDocument()));
        info.put("Borrowed books", String.valueOf(this.getBorrowedDocument()));

        return info;
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

