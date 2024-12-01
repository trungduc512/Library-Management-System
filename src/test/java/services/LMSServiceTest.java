package services;

import static org.junit.jupiter.api.Assertions.*;

import Model.Book;
import Model.Borrower;
import Model.Librarian;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class LMSServiceTest {

  @BeforeEach
  public void setUp() {
    // Thêm dữ liệu mẫu cho các bài kiểm tra
    try (Connection conn = DatabaseHelper.getConnection()) {
      String addBorrowerSql = "INSERT INTO Borrowers (fullName, userName, password) VALUES (?, ?, ?)";
      String addLibrarianSql = "INSERT INTO Librarians (fullName, userName, password) VALUES (?, ?, ?)";
      String addBookSql = "INSERT INTO Books (title, author, isbn, description, totalBooks, borrowedBooks, thumbnailURL) VALUES (?, ?, ?, ?, ?, ?, ?)";

      try (PreparedStatement borrowerStmt = conn.prepareStatement(addBorrowerSql);
          PreparedStatement librarianStmt = conn.prepareStatement(addLibrarianSql);
          PreparedStatement bookStmt = conn.prepareStatement(addBookSql)) {

        // Thêm borrower
        for (int i = 1; i <= 5; i++) {
          borrowerStmt.setString(1, "Test Borrower " + i);
          borrowerStmt.setString(2, "borrower" + i);
          borrowerStmt.setString(3, "password" + i);
          borrowerStmt.addBatch();
        }
        borrowerStmt.executeBatch();

        // Thêm librarian
        for (int i = 1; i <= 3; i++) {
          librarianStmt.setString(1, "Test Librarian " + i);
          librarianStmt.setString(2, "librarian" + i);
          librarianStmt.setString(3, "password" + i);
          librarianStmt.addBatch();
        }
        librarianStmt.executeBatch();

        // Thêm sách
        for (int i = 1; i <= 5; i++) {
          bookStmt.setString(1, "Test Book " + i);
          bookStmt.setString(2, "Test Author " + i);
          bookStmt.setString(3, "1234567890" + i);
          bookStmt.setString(4, "Test Description " + i);
          bookStmt.setInt(5, 10);
          bookStmt.setInt(6, 2);
          bookStmt.setString(7, "Test URL " + i);
          bookStmt.addBatch();
        }
        bookStmt.executeBatch();
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  @AfterEach
  public void tearDown() {
    try (Connection conn = DatabaseHelper.getConnection()) {
      String deleteBorrowerSql = "DELETE FROM Borrowers WHERE userName LIKE 'borrower%'";
      String deleteLibrarianSql = "DELETE FROM Librarians WHERE userName LIKE 'librarian%'";
      String deleteBookSql = "DELETE FROM Books WHERE isbn LIKE '1234567890%'";

      try (PreparedStatement stmt = conn.prepareStatement(deleteBorrowerSql)) {
        stmt.executeUpdate();
      }

      try (PreparedStatement stmt = conn.prepareStatement(deleteLibrarianSql)) {
        stmt.executeUpdate();
      }

      try (PreparedStatement stmt = conn.prepareStatement(deleteBookSql)) {
        stmt.executeUpdate();
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  @Test
  public void testSetBorrowerList() {
    List<Borrower> borrowers = LMSService.setBorrowerList();
    assertNotNull(borrowers, "Borrowers list should not be null");
    assertFalse(borrowers.isEmpty(), "Borrowers list should not be empty");
  }

  @Test
  public void testSetLibrarianList() {
    List<Librarian> librarians = LMSService.setLibrarianList();
    assertNotNull(librarians, "Librarians list should not be null");
    assertFalse(librarians.isEmpty(), "Librarians list should not be empty");
  }

  @Test
  public void testSetBookList() {
    List<Book> books = LMSService.setBookList();
    assertNotNull(books, "Books list should not be null");
    assertFalse(books.isEmpty(), "Books list should not be empty");
  }
}

