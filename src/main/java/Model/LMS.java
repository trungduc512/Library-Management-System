package Model;

import services.DatabaseHelper;
import services.LMSService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LMS {
    private List<Borrower> borrowerList;
    private List<Librarian> librarianList;
    private List<Book> bookList;
    private User currentUser;

    private static LMS instance = null;

    private LMS() {
        borrowerList = null;
        bookList = null;
        librarianList = null;
        currentUser = null;
    }

    // Get the singleton instance of LMS
    public static LMS getInstance() {
        if (instance == null) {
            instance = new LMS();
        }
        return instance;
    }

    //get current user
    public User getCurrentUser() {
        return currentUser;
    }

    // Getter for borrower list
    public List<Borrower> getBorrowerList() {
        borrowerList = LMSService.setBorrowerList();
        return borrowerList;
    }

    // Getter for librarian list
    public List<Librarian> getLibrarianList() {
        librarianList = LMSService.setLibrarianList();
        return librarianList;
    }

    // Getter for book list
    public List<Book> getBookList() {
        bookList = LMSService.setBookList();
        return bookList;
    }

    public boolean loginBorrower(String userName, String password) {
        currentUser = new Borrower();

        return currentUser.login(userName, password);
    }

    public boolean loginLibrarian(String userName, String password) {
        currentUser = new Librarian();

        return currentUser.login(userName, password);
    }

    // Hàm đăng xuất (logout)
    public void logoutBorrower(Borrower borrower) {
        System.out.println("Log out successfully.");
        if (borrower != null) {
            borrower = null;
        }
    }

    public void logoutLibrarian(Librarian librarian) {
        System.out.println("Log out successfully.");
        if (librarian != null) {
            librarian = null;
        }
    }

    public void logoutCurrentUser() {
        currentUser = null;
    }
}
