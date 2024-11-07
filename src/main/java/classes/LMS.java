package classes;

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
        if (this.borrowerList == null) {
            this.setBorrowerList();
        }
        return borrowerList;
    }

    // Getter for librarian list
    public List<Librarian> getLibrarianList() {
        return librarianList;
    }

    // Getter for book list
    public List<Book> getBookList() {
        setBookList();
        return bookList;
    }

    // Hàm liệt kê danh sách borrower
    private void setBorrowerList() {

        List<Borrower> borrowers = new ArrayList<>();
        String sql = "SELECT * FROM Borrowers";
        try {
            Connection conn = DatabaseHelper.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String fullName = rs.getString("fullName");
                String userName = rs.getString("userName");
                String password = rs.getString("password");
                borrowers.add(new Borrower(id, fullName, userName, password));
                System.out.println("ID: " + id + ", fullname: " + fullName + ", username: " + userName);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        borrowerList = borrowers;
    }

    public void setBookList() {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM Books";

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String title = rs.getString("title");
                String author = rs.getString("author");
                String isbn = rs.getString("isbn");
                String description = rs.getString("description");
                String thumbnailURL = rs.getString("thumbnailURL");
                int totalBooks = rs.getInt("totalBooks");
                int borrowedBooks = rs.getInt("borrowedBooks");

                books.add(new Book(title, author, isbn, description, totalBooks, borrowedBooks, thumbnailURL));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        this.bookList = books;
    }

    public void setLibrarianList() {
        List<Librarian> librarians = new ArrayList<>();
        String sql = "SELECT * FROM Librarians";

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String fullName = rs.getString("fullName");
                String userName = rs.getString("userName");
                String password = rs.getString("password");
                librarians.add(new Librarian(id, fullName, userName, password));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        this.librarianList = librarians;
    }

    public boolean loginBorrower(String userName, String password) {
        String sql = "SELECT * FROM Borrowers WHERE userName = ?";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, userName);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String storedPassword = rs.getString("password");

                if (password.equals(storedPassword)) {
                    int userId = rs.getInt("id");
                    String fullName = rs.getString("fullName");
                    System.out.println("Login successfully.");
                    currentUser = new Borrower(userId, fullName, userName, storedPassword);
                    return true;  // Đăng nhập thành công
                } else {
                    System.out.println("Wrong password.");
                }
            } else {
                System.out.println("User not exists.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;  // Đăng nhập thất bại
    }

    public boolean loginLibrarian(String userName, String password) {
        String sql = "SELECT * FROM Librarians WHERE userName = ?";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, userName);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String storedPassword = rs.getString("password");

                // Kiểm tra mật khẩu có khớp hay không
                if (password.equals(storedPassword)) {
                    int userId = rs.getInt("id");
                    String fullName = rs.getString("userName");
                    System.out.println("Login successfully.");
                    currentUser = new Librarian(userId, fullName, userName, storedPassword);
                    return true; // Đăng nhập thành công
                } else {
                    System.out.println("Wrong password.");
                }
            } else {
                System.out.println("User not exists.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;  // Đăng nhập thất bại
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
