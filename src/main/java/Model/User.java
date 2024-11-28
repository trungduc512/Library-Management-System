package Model;

import services.UserService;

import java.util.ArrayList;
import java.util.List;

/**
 * The User class is an abstract representation of a user in the library system.
 * It contains common attributes and methods for both borrowers and librarians.
 */
public abstract class User {

    protected int id;
    protected String fullName;
    protected String userName;
    protected String password;

    // Constructor
    protected User() {
    }

    protected User(int id, String fullName, String userName, String password) {
        this.id = id;
        this.fullName = fullName;
        this.userName = userName;
        this.password = password;
    }

    // Getters và Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public abstract boolean login(String userName, String password);

    public static ArrayList<Book> searchBooksByTitle(String title) {
        return UserService.searchBooksByTitle(title);
    }

    public static ArrayList<Thesis> searchThesisByTitle(String title) {
        return UserService.searchThesisByTitle(title);
    }

    public static ArrayList<Book> getAllBooks() {
        return UserService.getAllBooks();
    }

    public static ArrayList<Thesis> getAllThesis() {
        return UserService.getAllThesis();
    }

    public static Book getBookByIsbn(String isbn) {
        return UserService.getBookByIsbn(isbn);
    }

    public static Thesis getThesisById(long id) {
        return UserService.getThesisById(id);
    }

    public static Document getDocumentById(String id) {
        return UserService.getDocumentById(id);
    }

    public static int getTotalBooks() {
        return UserService.getTotalBooks();
    }

    public static int getTotalBorrowedBooks() {
        return UserService.getTotalBorrowedBooks();
    }

    public static List<Book> listTopBorrowedBooks() {
        return UserService.listTopBorrowedBooks();
    }

    public static List<Borrower> getTopBorrowers() {
        return UserService.getTopBorrowers();
    }
}