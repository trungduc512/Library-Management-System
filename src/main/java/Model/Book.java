package Model;

import java.util.HashMap;
import java.util.Map;

/**
 * The {@code Book} class represents a book entity, which is a specialized type of {@code Document}.
 * It includes additional information such as an ISBN number.
 */
public class Book extends Document {
    /**
     * The International Standard Book Number (ISBN) of the book.
     */
    private String isbn;

    /**
     * Constructs a {@code Book} object with the specified details.
     *
     * @param title         the title of the book
     * @param author        the author of the book
     * @param isbn          the ISBN of the book
     * @param description   a brief description of the book
     * @param totalBooks    the total number of copies of the book
     * @param borrowedBooks the number of borrowed copies of the book
     * @param thumbnailURL  the URL of the book's thumbnail image
     */
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

    /**
     * Retrieves detailed information about the book.
     *
     * @return a {@code Map} containing key-value pairs of the book's details
     *         such as title, author, ISBN, total books, and borrowed books
     */
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

    /**
     * Prints detailed information about the book to the console.
     * The output includes the title, author, total books, borrowed books,
     * description, and thumbnail URL.
     */
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