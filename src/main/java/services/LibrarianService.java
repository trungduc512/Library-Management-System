package services;

import Model.Book;
import Model.Borrower;
import Model.Librarian;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class LibrarianService {

    public static void delete(String isbn) {
        String sql = "DELETE FROM Books WHERE isbn = ?";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, isbn);
            stmt.executeUpdate();
            System.out.println("Delete book successfully");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static String returnStatus(int borrowerId) {
        String sql = "SELECT returnDate FROM BorrowedBookRecord WHERE borrowerId = ?";
        LocalDate today = LocalDate.now();
        boolean hasBorrowed = false;
        boolean hasOverdue = false;

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, borrowerId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                hasBorrowed = true;
                LocalDate returnDate = rs.getDate("returnDate").toLocalDate();
                if (returnDate.isBefore(today)) {
                    hasOverdue = true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (!hasBorrowed) {
            return "No Borrowed";
        }
        if (hasOverdue) {
            return "Overdue";
        }
        return "Good Borrower";
    }

    public static void save(Book book) {
        String sql = "INSERT INTO Books (title, author, isbn, description, totalBooks, borrowedBooks, thumbnailURL) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, book.getTitle());
            stmt.setString(2, book.getAuthor());
            stmt.setString(3, book.getIsbn());
            stmt.setString(4, book.getDescription());
            stmt.setInt(5, book.getTotalBooks());
            stmt.setInt(6, book.getBorrowedBooks());
            stmt.setString(7, book.getThumbnailURL());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void updateTotalBook(Book book) {
        String sql = "UPDATE Books SET totalBooks = ? WHERE isbn = ?";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, book.getTotalBooks());
            stmt.setString(2, book.getIsbn());
            stmt.executeUpdate();
            System.out.println("Update book successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<Borrower> listBorrowers() {
        List<Borrower> borrowers = new ArrayList<>();
        String sql = "SELECT * FROM Borrowers";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                int id = rs.getInt("id");
                String fullName = rs.getString("fullName");
                String userName = rs.getString("userName");
                String password = rs.getString("password");
                borrowers.add(new Borrower(id, fullName, userName, password));
                System.out.println("ID: " + id + ", full name: " + fullName + ", user name: " + userName);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return borrowers;
    }

    public static boolean userExists(String userName) {
        String sql = "SELECT * FROM Librarians WHERE userName = ?";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, userName);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
