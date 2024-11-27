package Model;

import services.UserService;

import java.util.ArrayList;
import java.util.List;

public abstract class User {

    protected int id;
    protected String fullName;  // Họ tên người dùng
    protected String userName;  // Tên tài khoản (username)
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

    //Hàm tìm sách theo tên
    public static ArrayList<Book> searchBooksByTitle(String title) {
        return UserService.searchBooksByTitle(title);
    }

    public static ArrayList<Thesis> searchThesisByTitle(String title) {
        return UserService.searchThesisByTitle(title);
    }

    // Hàm lấy danh sách sách từ cơ sở dữ liệu
    public static ArrayList<Book> getAllBooks() {
        return UserService.getAllBooks();
    }

    public static ArrayList<Thesis> getAllThesis() {
        return UserService.getAllThesis();
    }

    // Hàm lấy thông tin sách từ ISBN
    public static Book getBookByIsbn(String isbn) {
        return UserService.getBookByIsbn(isbn);
    }

    public static Thesis getThesisById(long id) {
        return UserService.getThesisById(id);
    }

    public static Document getDocumentById(String id) {
        return UserService.getDocumentById(id);
    }

    // Hàm tìm tổng số sách có trong database
    public static int getTotalBooks() {
        return UserService.getTotalBooks();
    }

    // Hàm tìm tổng số sách đã mượn
    public static int getTotalBorrowedBooks() {
        return UserService.getTotalBorrowedBooks();
    }

    // Hàm liệt kê 4 sách được mượn nhiều nhất
    public static List<Book> listTopBorrowedBooks() {
        return UserService.listTopBorrowedBooks();
    }

    // Hàm liệt kê Top 3 người mượn nhiều sách nhất
    public static List<Borrower> getTopBorrowers() {
        return UserService.getTopBorrowers();
    }
}