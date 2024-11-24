package services;

import Model.Book;
import Model.BorrowedBookRecord;
import Model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class BorrowerService {
    public static void incrementBorrowedBooks(String isbn, int quantity) {
        Book book = UserService.getBookByIsbn(isbn);
        if (book != null) {
            String sql = "UPDATE Books SET borrowedBooks = ? WHERE isbn = ?";
            try (Connection conn = DatabaseHelper.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                // Thêm borrowed_books
                stmt.setInt(1, book.getBorrowedBooks() + quantity);
                stmt.setString(2, isbn);
                stmt.executeUpdate();
                System.out.println("classes.Book updated in database.");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Books not updated.");
        }
    }

    public static void reduceBorrowedBooks(String isbn, int quantity) {
        Book book = User.getBookByIsbn(isbn);
        if (book != null) {
            String sql = "UPDATE Books SET borrowedBooks = ? WHERE isbn = ?";
            try (Connection conn = DatabaseHelper.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                // Trừ borrowed_books
                stmt.setInt(1, book.getBorrowedBooks() - quantity);
                stmt.setString(2, isbn);
                stmt.executeUpdate();
                System.out.println("classes.Book updated in database.");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Books not updated.");
        }
    }

    public static void updateProfile(String fullName, int id) {
        String sql = "UPDATE Users SET fullName = ? WHERE id = ?";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, fullName);
            stmt.setInt(2, id);
            stmt.executeUpdate();
            System.out.println("Profile updated in database.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean addBorrowedBookRecord(int borrowerId, String title, String isbn, int quantity) {
        String sql = "INSERT INTO BorrowedBookRecord (borrowerId, title, isbn, quantity, borrowedDate, returnDate) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            LocalDate borrowDate = LocalDate.now();
            LocalDate returnDate = borrowDate.plusWeeks(1);

            stmt.setInt(1, borrowerId);
            stmt.setString(2, title);
            stmt.setString(3, isbn);
            stmt.setInt(4, quantity);
            stmt.setDate(5, java.sql.Date.valueOf(borrowDate));
            stmt.setDate(6, java.sql.Date.valueOf(returnDate));

            stmt.executeUpdate();
            System.out.println("Book borrowed and saved to the database successfully!");
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static List<BorrowedBookRecord> listBorrowedBooksOfUserID(int id) {

        List<BorrowedBookRecord> records = new ArrayList<>();
        String sql = "SELECT * FROM BorrowedBookRecord WHERE borrowerId = ?";

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            System.out.println("Borrowed books:");

            while (rs.next()) {
                int recordId = rs.getInt("id"); // ID tự động tạo
                String title = rs.getString("title");
                String isbn = rs.getString("isbn");
                int quantity = rs.getInt("quantity");
                LocalDate borrowedDate = rs.getDate("borrowedDate").toLocalDate();
                LocalDate returnDate = rs.getDate("returnDate").toLocalDate();
                records.add(new BorrowedBookRecord(recordId, title, isbn, quantity, borrowedDate, returnDate));
                System.out.println(
                        "Record ID: " + recordId + ", ISBN: " + isbn + ", quantity: " + quantity);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return records;
    }

    private static BorrowedBookRecord findBorrowedRecordById(int recordId) {
        String sql = "SELECT * FROM BorrowedBookRecord WHERE id = ?";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, recordId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String title = rs.getString("title");
                String isbn = rs.getString("isbn");
                int quantity = rs.getInt("quantity");
                LocalDate borrowedDate = rs.getDate("borrowedDate").toLocalDate();
                LocalDate returnDate = rs.getDate("returnDate").toLocalDate();
                return new BorrowedBookRecord(recordId, title, isbn, quantity, borrowedDate, returnDate);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static boolean deleteBorrowedBookRecord(int recordId) {
        String sql = "DELETE FROM BorrowedBookRecord WHERE id = ?";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, recordId);
            stmt.executeUpdate();
            System.out.println("Return book successfully.");
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean returnBook(int recordId) {
        BorrowedBookRecord record = findBorrowedRecordById(recordId);
        if (record == null) {
            System.out.println("Borrow record ID not found.");
            return false;
        }
        Book book = UserService.getBookByIsbn(record.getIsbn());
        if (book != null) {
            reduceBorrowedBooks(book.getIsbn(), record.getQuantity());
        }
        return deleteBorrowedBookRecord(recordId);
    }

    public static String returnStatusOfUserId(int id) {
        String sql = "SELECT returnDate FROM BorrowedBookRecord WHERE borrowerId = ?";
        LocalDate today = LocalDate.now();
        boolean hasBorrowed = false;
        boolean hasOverdue = false;

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
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
            return "No active loans";
        }
        if (hasOverdue) {
            return "Has overdue loans";
        }
        return "In good standing";
    }
}
