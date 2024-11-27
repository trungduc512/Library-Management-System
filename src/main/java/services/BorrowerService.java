package services;

import Model.*;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class BorrowerService {

  public static boolean registerUser(String fullName, String userName, String password) {
    // Kiểm tra nếu tên tài khoản đã tồn tại
    if (classes.Borrower.userExists(userName)) {
      System.out.println("User already exists");
      return false;
    }

    String sql = "INSERT INTO Borrowers (fullName, userName, password) VALUES (?, ?, ?)";
    try (Connection conn = classes.DatabaseHelper.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql,
            PreparedStatement.RETURN_GENERATED_KEYS)) {
      stmt.setString(1, fullName);
      stmt.setString(2, userName);
      stmt.setString(3, password);
      stmt.executeUpdate();
      System.out.println("Register successfully!");

      return true;
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return false;
  }

  public static Borrower loginUser(String userName, String password) {
    String sql = "SELECT * FROM Borrowers WHERE userName = ?";
    try (Connection conn = classes.DatabaseHelper.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setString(1, userName);
      ResultSet rs = stmt.executeQuery();

      if (rs.next()) {
        String storedPassword = rs.getString("password");

        if (password.equals(storedPassword)) {
          int userId = rs.getInt("id");
          String fullName = rs.getString("fullName");
          System.out.println("Login successfully.");

          return new Borrower(userId, fullName, userName, storedPassword);  // Đăng nhập thành công
        } else {
          System.out.println("Wrong password.");
        }
      } else {
        System.out.println("User not exists.");
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return null;  // Đăng nhập thất bại
  }

  public static boolean borrowDocument(Borrower borrower, String documentId, int quantity) {
    Document doc = User.getDocumentById(documentId);
    if (doc != null && quantity <= doc.getTotalDocument() - doc.getBorrowedDocument()) {
      String type = (doc instanceof Book) ? "Book" : "Thesis";
      if (addBorrowedBookRecord(borrower.getId(), doc.getTitle(), documentId, quantity, type)) {
        incrementBorrowedBooks(documentId, quantity);
        return true;
      }
    }
    return false;
  }

  private static boolean addBorrowedBookRecord(int borrowerId, String title, String documentId,
      int quantity, String type) {
    String sql = "INSERT INTO BorrowedDocumentRecord (borrowerId, title, documentId, quantity, borrowedDate, returnDate, type) VALUES (?, ?, ?, ?, ?, ?, ?)";
    try (Connection conn = DatabaseHelper.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {
      LocalDate borrowDate = LocalDate.now();
      LocalDate returnDate = borrowDate.plusWeeks(1);

      stmt.setInt(1, borrowerId);
      stmt.setString(2, title);
      stmt.setString(3, documentId);
      stmt.setInt(4, quantity);
      stmt.setDate(5, java.sql.Date.valueOf(borrowDate));
      stmt.setDate(6, java.sql.Date.valueOf(returnDate));
      stmt.setString(7, type);

      stmt.executeUpdate();
      return true;
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return false;
  }

  private static void incrementBorrowedBooks(String documentId, int quantity) {
    Document doc = User.getDocumentById(documentId);
    if (doc != null) {
      try (Connection conn = DatabaseHelper.getConnection()) {
        String sql = (doc instanceof Book)
            ? "UPDATE Books SET borrowedBooks = ? WHERE isbn = ?"
            : "UPDATE Thesis SET borrowedTheses = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
          stmt.setInt(1, doc.getBorrowedDocument() + quantity);
          stmt.setString(2, (doc instanceof Book) ? ((Book) doc).getIsbn()
              : Long.toString(((Thesis) doc).getId()));
          stmt.executeUpdate();
        }
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
  }

  public static boolean returnBook(int recordId) {
    BorrowedBookRecord record = findBorrowedRecordById(recordId);
    if (record != null) {
      Document doc = User.getDocumentById(record.getDocumentId());
      if (doc != null) {
        decreaseBorrowedBooks(doc, record.getQuantity());
      }
      return deleteBorrowedBookRecord(recordId);
    }
    return false;
  }

  private static boolean deleteBorrowedBookRecord(int recordId) {
    String sql = "DELETE FROM BorrowedDocumentRecord WHERE id = ?";
    try (Connection conn = DatabaseHelper.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setInt(1, recordId);
      stmt.executeUpdate();
      return true;
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return false;
  }

  private static void decreaseBorrowedBooks(Document doc, int quantity) {
    try (Connection conn = DatabaseHelper.getConnection()) {
      String sql = (doc instanceof Book)
          ? "UPDATE Books SET borrowedBooks = ? WHERE isbn = ?"
          : "UPDATE Thesis SET borrowedTheses = ? WHERE id = ?";
      try (PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setInt(1, doc.getBorrowedDocument() - quantity);
        stmt.setString(2,
            (doc instanceof Book) ? ((Book) doc).getIsbn() : Long.toString(((Thesis) doc).getId()));
        stmt.executeUpdate();
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public static List<BorrowedBookRecord> listBorrowedBooks(int borrowerId) {
    List<BorrowedBookRecord> records = new ArrayList<>();
    String sql = "SELECT * FROM BorrowedDocumentRecord WHERE borrowerId = ?";
    try (Connection conn = DatabaseHelper.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setInt(1, borrowerId);
      ResultSet rs = stmt.executeQuery();
      while (rs.next()) {
        int recordId = rs.getInt("id");
        String title = rs.getString("title");
        String documentId = rs.getString("documentId");
        int quantity = rs.getInt("quantity");
        LocalDate borrowedDate = rs.getDate("borrowedDate").toLocalDate();
        LocalDate returnDate = rs.getDate("returnDate").toLocalDate();
        String type = rs.getString("type");
        records.add(
            new BorrowedBookRecord(recordId, title, documentId, quantity, borrowedDate, returnDate,
                type));
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return records;
  }

  // Check if a user exists
  public static boolean userExists(String userName) {
    String sql = "SELECT * FROM Borrowers WHERE userName = ?";
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

  // Get borrower status (whether the borrower has overdue books)
  public static String getBorrowerStatus(int borrowerId) {
    String sql = "SELECT returnDate FROM BorrowedDocumentRecord WHERE borrowerId = ?";
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
      return "No active loans";
    }
    if (hasOverdue) {
      return "Has overdue loans";
    }
    return "In good standing";
  }

  // Find a borrowed book record by ID
  private static BorrowedBookRecord findBorrowedRecordById(int recordId) {
    String sql = "SELECT * FROM BorrowedDocumentRecord WHERE id = ?";
    try (Connection conn = DatabaseHelper.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setInt(1, recordId);
      ResultSet rs = stmt.executeQuery();
      if (rs.next()) {
        String title = rs.getString("title");
        String documentId = rs.getString("documentId");
        int quantity = rs.getInt("quantity");
        LocalDate borrowedDate = rs.getDate("borrowedDate").toLocalDate();
        LocalDate returnDate = rs.getDate("returnDate").toLocalDate();
        String type = rs.getString("type");
        return new BorrowedBookRecord(recordId, title, documentId, quantity, borrowedDate,
            returnDate, type);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return null;
  }
}
