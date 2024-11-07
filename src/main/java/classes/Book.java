package classes;

public class Book {

    private String title;
    private String author;
    private String isbn;
    private String description;
    private int totalBooks;
    private int borrowedBooks;
    private String thumbnailURL;

    // Constructor
    public Book(String title, String author, String isbn, String description, int totalBooks,
        int borrowedBooks, String thumbnailURL) {
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.description = description;
        this.totalBooks = totalBooks;
        this.borrowedBooks = borrowedBooks;
        this.thumbnailURL = thumbnailURL;
    }

    // Getters và Setters
    public String getTitle() {
      return title;
    }

    public void setTitle(String title) {
      this.title = title;
    }

    public String getAuthor() {
      return author;
    }

    public void setAuthor(String author) {
      this.author = author;
    }

    public String getIsbn() {
      return isbn;
    }

    public void setIsbn(String isbn) {
      this.isbn = isbn;
    }

    public String getDescription() {
      return description;
    }

    public void setDescription(String description) {
      this.description = description;
    }

    public int getTotalBooks() {
      return totalBooks;
    }

    public void setTotalBooks(int totalBooks) {
      this.totalBooks = totalBooks;
    }

    public int getBorrowedBooks() {
      return borrowedBooks;
    }

    public void setBorrowedBooks(int borrowedBooks) {
      this.borrowedBooks = borrowedBooks;
    }

    public String getThumbnailURL() {
        return thumbnailURL;
    }

    public void setThumbnailURL(String thumbnailURL) {
        this.thumbnailURL = thumbnailURL;
    }

    // Hàm in ra thông tin sách
    public void printDetails() {
        System.out.println("Title: " + title);
        System.out.println("Author: " + author);
        System.out.println("ISBN: " + isbn);
        System.out.println("Description: " + description);
        System.out.println("Total Books: " + totalBooks);
        System.out.println("Borrowed Books: " + borrowedBooks);
    }
}

