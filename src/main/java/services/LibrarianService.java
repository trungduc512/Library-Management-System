package services;

import Model.Book;
import Model.Borrower;
import Model.Librarian;
import Model.Thesis;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class LibrarianService {

  public static Librarian login(String userName, String password) {
    String sql = "SELECT * FROM Librarians WHERE userName = ?";
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
          return new Librarian(userId, fullName, userName, storedPassword);
        } else {
          System.out.println("Wrong password.");
        }
      } else {
        System.out.println("User not exists.");
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return null;
  }

  public static void saveBook(Book book) {
    String sql = "INSERT INTO Books (title, author, isbn, description, totalBooks, borrowedBooks, thumbnailURL) VALUES (?, ?, ?, ?, ?, ?, ?)";
    try (Connection conn = DatabaseHelper.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setString(1, book.getTitle());
      stmt.setString(2, book.getAuthor());
      stmt.setString(3, book.getIsbn());
      stmt.setString(4, book.getDescription());
      stmt.setInt(5, book.getTotalDocument());
      stmt.setInt(6, book.getBorrowedDocument());
      stmt.setString(7, book.getThumbnailURL());
      stmt.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public static void saveThesis(Thesis thesis) {
    String sql = "INSERT INTO Thesis (title, author, university, description, totalTheses, borrowedTheses, thumbnailURL) VALUES (?, ?, ?, ?, ?, ?, ?)";
    try (Connection conn = DatabaseHelper.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setString(1, thesis.getTitle());
      stmt.setString(2, thesis.getAuthor());
      stmt.setString(3, thesis.getUniversity());
      stmt.setString(4, thesis.getDescription());
      stmt.setInt(5, thesis.getTotalDocument());
      stmt.setInt(6, thesis.getBorrowedDocument());
      stmt.setString(7, thesis.getThumbnailURL());
      stmt.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public static void updateTotalBook(Book book) {
    String sql = "UPDATE Books SET totalBooks = ? WHERE isbn = ?";
    try (Connection conn = DatabaseHelper.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setInt(1, book.getTotalDocument());
      stmt.setString(2, book.getIsbn());
      stmt.executeUpdate();
      System.out.println("Update book successfully.");
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public static void updateTotalThesis(Thesis thesis) {
    String sql = "UPDATE Thesis SET totalTheses = ? WHERE id = ?";
    try (Connection conn = DatabaseHelper.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setInt(1, thesis.getTotalDocument());
      stmt.setLong(2, thesis.getId());
      stmt.executeUpdate();
      System.out.println("Update thesis successfully.");
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public static void deleteBook(String isbn) {
    String sql = "DELETE FROM Books WHERE isbn = ?";
    try (Connection conn = DatabaseHelper.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setString(1, isbn);
      stmt.executeUpdate();
      System.out.println("Delete book successfully.");
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

  public static String returnStatus(int borrowerId) {
    String sql = "SELECT returnDate FROM BorrowedDocumentRecord  WHERE borrowerId = ?";
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

  public static boolean register(String fullName, String userName, String password) {
    if (userExists(userName)) {
      System.out.println("User already exists.");
      return false;
    }

    String sql = "INSERT INTO Librarians (fullName, userName, password) VALUES (?, ?, ?)";
    try (Connection conn = DatabaseHelper.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql,
            PreparedStatement.RETURN_GENERATED_KEYS)) {
      stmt.setString(1, fullName);
      stmt.setString(2, userName);
      stmt.setString(3, password);
      stmt.executeUpdate();
      System.out.println("Register successfully.");
      return true;
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return false;
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
