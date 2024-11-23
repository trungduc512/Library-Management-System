package Model;

import services.LibrarianService;
import services.UserService;

import java.util.ArrayList;
import java.util.List;

public class Librarian extends User {

    private List<Book> books;

    private Librarian() {
    }

    public Librarian(int id, String fullName, String userName, String password) {
        super(id, fullName, userName, password);
        this.books = new ArrayList<>();
    }

    public void save(Book book) {
        LibrarianService.save(book);
    }

    public void updateTotalBook(Book book) {
        LibrarianService.updateTotalBook(book);
    }

    public void delete(String isbn) {
        LibrarianService.delete(isbn);
    }

    public void addBook(String title, String author, String isbn, String description,
                        int totalBooks, String thumbnailURL) {
        Book findBook = UserService.getBookByIsbn(isbn);
        if (findBook != null) {
            System.out.println("Book already exists");
            updateBook(isbn, totalBooks);
            return;
        }
        Book newBook = new Book(title, author, isbn, description, totalBooks, 0, thumbnailURL);
        save(newBook);
        System.out.println("Added book: " + title + " successfully.");
    }

    public void updateBook(String isbn,
                           int increaseQuantity) {
        Book book = UserService.getBookByIsbn(isbn);
        if (book != null) {
            book.setTotalBooks(
                    book.getTotalBooks() + increaseQuantity);
            updateTotalBook(book);
            System.out.println("Updated book: " + book.getTitle());
        } else {
            System.out.println("Book not found.");
        }
    }

    public int getDeleteQuantity(String isbn) {
        Book book = UserService.getBookByIsbn(isbn);
        if (book != null) {
            return book.getTotalBooks() - book.getBorrowedBooks();
        } else {
            System.out.println("Book not found.");
        }
        return 0;
    }

    public void decreaseBook(String isbn, int decreaseQuantity) {
        Book book = UserService.getBookByIsbn(isbn);
        if (book != null) {
            book.setTotalBooks(
                    book.getTotalBooks() - decreaseQuantity);
            updateTotalBook(book);
            System.out.println("Updated book: " + book.getTitle());
        } else {
            System.out.println("Book not found.");
        }
    }

    public void deleteBook(String isbn) {
        Book book = UserService.getBookByIsbn(isbn);
        if (book != null) {
            deleteBook(book.getIsbn());
            System.out.println("Deleted book: " + book.getTitle());
        } else {
            System.out.println("Book not found.");
        }
    }

    public static List<Borrower> listBorrowers() {
        return LibrarianService.listBorrowers();
    }

    public static String returnStatus(int borrowerId) {
        return LibrarianService.returnStatus(borrowerId);
    }
}

