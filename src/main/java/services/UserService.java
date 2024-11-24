package services;

import Model.Book;
import Model.Borrower;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserService {

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

        return null;
    }

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

                books.add(
                        new Book(bookTitle, author, isbn, description, totalBook, borrowedBook, thumbnailURL));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return books;
    }

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

                books.add(
                        new Book(title, author, isbn, description, totalBooks, borrowedBooks, thumbnailURL));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return books;
    }

    public static int getTotalBooks() {
        String sql = "SELECT SUM(totalBooks) AS totalBooks FROM Books";
        int totalBooks = 0;
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                totalBooks = rs.getInt("totalBooks");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return totalBooks;
    }

    public static int getTotalBorrowedBooks() {
        String sql = "SELECT SUM(borrowedBooks) AS borrowedBooks FROM Books";
        int borrowedBooks = 0;

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                borrowedBooks = rs.getInt("borrowedBooks");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return borrowedBooks;
    }

    public static List<Book> listTopBorrowedBooks() {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM Books ORDER BY borrowedBooks DESC LIMIT 5";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                String title = rs.getString("title");
                String author = rs.getString("author");
                String isbn = rs.getString("isbn");
                String description = rs.getString("description");
                int totalBooks = rs.getInt("totalBooks");
                int borrowedBooks = rs.getInt("borrowedBooks");
                String thumbnailURL = rs.getString("thumbnailURL");
                books.add(
                        new Book(title, author, isbn, description, totalBooks, borrowedBooks, thumbnailURL));
                System.out.println(
                        "Title: " + title + ", author: " + author + ", borrowed books: " + borrowedBooks);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return books;
    }
    public static List<Borrower> getTopBorrowers() {
        String sql = """
                SELECT b.id, b.fullName, b.userName, b.password
                FROM Borrowers b
                JOIN BorrowedBookRecord r ON b.id = r.borrowerId
                GROUP BY b.id, b.fullName, b.userName, b.password
                ORDER BY SUM(r.quantity) DESC
                LIMIT 3
            """;
        List<Borrower> topBorrowers = new ArrayList<>();

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String fullName = rs.getString("fullName");
                String userName = rs.getString("userName");
                String password = rs.getString("password");

                // Tạo đối tượng Borrower và thêm vào danh sách
                Borrower borrower = new Borrower(id, fullName, userName, password);
                topBorrowers.add(borrower);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving top borrowers: " + e.getMessage());
        }

        // Nếu không tìm thấy, in thông báo
        if (topBorrowers.isEmpty()) {
            System.out.println("No borrowers found.");
        }

        return topBorrowers;
    }

}