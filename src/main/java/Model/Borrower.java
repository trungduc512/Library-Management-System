package Model;

import services.BorrowerService;

import java.util.ArrayList;
import java.util.List;

/**
 * The Borrower class represents a user who borrows books from the library system.
 * It extends the User class and includes additional functionality for managing borrowed books.
 */
public class Borrower extends User {

    private List<BorrowedBookRecord> borrowedBooks;

    public Borrower() {
    }

    // Constructor
    public Borrower(int id, String fullName, String userName, String password) {
        super(id, fullName, userName, password);
        this.borrowedBooks = new ArrayList<>();
    }

    public static boolean register(String fullName, String userName, String password) {
        if (BorrowerService.registerUser(fullName, userName, password)) {
            System.out.println("Registration successful.");
            return true;
        }
        System.out.println("User already exists.");
        return false;
    }

    @Override
    public boolean login(String userName, String password) {
        Borrower borrower = BorrowerService.loginUser(userName, password);
        if (borrower != null) {
            System.out.println("Login successfully.");
            this.id = borrower.id;
            this.fullName = borrower.fullName;
            this.userName = borrower.userName;
            this.password = borrower.password;

            return true;
        }
        System.out.println("Login failed.");
        return false;
    }

    public void updateProfile(String fullName) {
        this.setFullName(fullName);
        BorrowerService.updateProfile(fullName, id);
    }

    public List<BorrowedBookRecord> listBorrowedBooks() {
        return BorrowerService.listBorrowedBooks(this.getId());
    }

    public boolean borrowDocumentById(String documentId, int quantity) {
        return BorrowerService.borrowDocument(this, documentId, quantity);
    }

    public boolean returnBook(int recordId) {
        return BorrowerService.returnBook(recordId);
    }

    /**
     * Retrieves the current status of the borrower, such as the overdue ,...
     *
     * @return the borrower's status
     */
    public String returnStatus() {
        return BorrowerService.getBorrowerStatus(this.getId());
    }
}


