package Model;

import services.LMSService;

import java.util.List;

/**
 * The LMS (Library Management System) class serves as the central hub for managing the library's users and documents.
 * It follows the Singleton pattern to ensure there is only one instance of the system. It provides methods to handle
 * login and logout for borrowers and librarians, and to retrieve lists of users and books.
 */
public class LMS {
    private static LMS instance = null;
    private List<Borrower> borrowerList;
    private List<Librarian> librarianList;
    private List<Book> bookList;
    private User currentUser;

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

    // Get current user
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
