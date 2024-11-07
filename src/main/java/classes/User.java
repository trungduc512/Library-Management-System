package classes;

import java.sql.*;
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

    //Hàm tìm sách theo tên
    public static ArrayList<Book> searchBooksByTitle(String title) {
        ArrayList<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM Books WHERE title LIKE ?";

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + title + "%");
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String bookTitle = rs.getString("title");
                String author = rs.getString("author");
                String isbn = rs.getString("isbn");
                String description = rs.getString("description");
                int totalBook = rs.getInt("totalBooks");
                int borrowedBook = rs.getInt("borrowedBooks");
                String thumbnailURL = rs.getString("thumbnailURL");

                books.add(new Book(bookTitle, author, isbn, description, totalBook, borrowedBook, thumbnailURL));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return books;
    }

    // Hàm lấy danh sách sách từ cơ sở dữ liệu
    public static ArrayList<Book> getAllBooks() {
        ArrayList<Book> books = new ArrayList<>();
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

        return books;
    }

    // Hàm lấy thông tin sách từ ISBN
    public static Book getBookByIsbn(String isbn) {
        String sql = "SELECT * FROM Books WHERE isbn = ?";

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, isbn);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String title = rs.getString("title");
                String author = rs.getString("author");
                String description = rs.getString("description");
                String thumbnailURL = rs.getString("thumbnailURL");
                int totalBooks = rs.getInt("totalBooks");
                int borrowedBooks = rs.getInt("borrowedBooks");

                return new Book(title, author, isbn, description, totalBooks, borrowedBooks, thumbnailURL);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;  // Nếu không tìm thấy sách
    }
}


