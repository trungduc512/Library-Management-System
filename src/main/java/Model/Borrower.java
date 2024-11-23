package Model;

import services.AuthenticationService;
import services.BorrowerService;
import services.DatabaseHelper;
import services.UserService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Borrower extends User {

    private List<BorrowedBookRecord> borrowedBooks;
    private List<Book> searchedBooks;

    public Borrower(int id, String fullName, String userName, String password) {
        super(id, fullName, userName, password);
        this.borrowedBooks = new ArrayList<>();
        this.searchedBooks = new ArrayList<>();
    }

    public void updateProfile(String fullName) {
        this.setFullName(fullName);
        BorrowerService.updateProfile(fullName, id);
    }

    private void incrementBorrowedBooks(String isbn, int quantity) {
        BorrowerService.incrementBorrowedBooks(isbn, quantity);
    }

    private void reduceBorrowedBooks(String isbn, int quantity) {
        BorrowerService.reduceBorrowedBooks(isbn, quantity);
    }

    public void searchBooks(String title) {
        searchedBooks = searchBooksByTitle(title);
        if (searchedBooks.isEmpty()) {
            System.out.println("Not found: " + title);
        } else {
            System.out.println("Found results: ");
            for (Book book : searchedBooks) {
                System.out.println(
                        book.getTitle() + " - " + book.getAuthor() + " (ISBN: " + book.getIsbn() + ")");
            }
        }
    }

    public boolean borrowBookByIsbn(String isbn, int quantity) {
        Book book = getBookByIsbn(isbn);
        assert book != null;
        if (quantity <= book.getTotalBooks() - book.getBorrowedBooks()) {
            System.out.println("Borrowed: " + book.getTitle());
            if (addBorrowedBookRecord(this.getId(), book.getTitle(), book.getIsbn(), quantity)) {
                incrementBorrowedBooks(book.getIsbn(), quantity);
            }
            return true;
        } else {
            System.out.println("Not enough books. In stock: " + book.getTotalBooks());
            return false;
        }
    }

    private boolean addBorrowedBookRecord(int borrowerId, String title, String isbn, int quantity) {
        return BorrowerService.addBorrowedBookRecord(borrowerId, title, isbn, quantity);
    }

    public List<BorrowedBookRecord> listBorrowedBooks() {
        return BorrowerService.listBorrowedBooksOfUserID(id);
    }

    public boolean returnBook(int recordId) {
        return BorrowerService.returnBook(recordId);
    }

    public static boolean register(String fullName, String userName, String password) {
        return AuthenticationService.registerUser(fullName, userName, password);
    }

    public String returnStatus() {
        return BorrowerService.returnStatusOfUserId(id);
    }
}


