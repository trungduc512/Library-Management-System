package Model;

import java.util.ArrayList;
import java.util.List;

import services.BorrowerService;

public class Borrower extends User {

    private List<BorrowedBookRecord> borrowedBooks;

    public Borrower() {
    }

    // Constructor
    public Borrower(int id, String fullName, String userName, String password) {
        super(id, fullName, userName, password);
        this.borrowedBooks = new ArrayList<>();
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

    public static boolean register(String fullName, String userName, String password) {
        if (BorrowerService.registerUser(fullName, userName, password)) {
            System.out.println("Registration successful.");
            return true;
        }
        System.out.println("User already exists.");
        return false;
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

    public String returnStatus() {
        return BorrowerService.getBorrowerStatus(this.getId());
    }
}


