package services;

import static org.junit.jupiter.api.Assertions.*;

import Model.Book;
import Model.Borrower;
import Model.Librarian;
import Model.Thesis;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LibrarianServiceTest {

  private static final String testFullName = "Test Librarian";
  private static final String testUsername = "librariantest";
  private static final String testPassword = "passwordtest";
  private Librarian librarian;

  private void deleteTestBook() {
    String deleteSql = "DELETE FROM Books WHERE isbn = '1234567890'";
    try (Connection conn = DatabaseHelper.getConnection(); PreparedStatement stmt = conn.prepareStatement(
        deleteSql)) {
      stmt.executeUpdate();
    } catch (SQLException e) {
      fail("Failed to delete test book: " + e.getMessage());
    }
  }

  private void deleteTestThesis() {
    String deleteSql = "DELETE FROM Thesis WHERE title = 'Test Thesis'";
    try (Connection conn = DatabaseHelper.getConnection(); PreparedStatement stmt = conn.prepareStatement(
        deleteSql)) {
      stmt.executeUpdate();
    } catch (SQLException e) {
      fail("Failed to delete test thesis: " + e.getMessage());
    }
  }

  @BeforeEach
  void setUp() throws SQLException {
    String sqlInsert = "INSERT INTO Librarians (fullName, userName, password) VALUES (?, ?, ?)";
    try (Connection conn = DatabaseHelper.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sqlInsert)) {
      stmt.setString(1, testFullName);
      stmt.setString(2, testUsername);
      stmt.setString(3, testPassword);
      stmt.executeUpdate();
    }
    librarian = LibrarianService.login(testUsername, testPassword);
    assertNotNull(librarian, "Librarian should not be null after successful login");
  }

  @AfterEach
  void tearDown() throws SQLException {

    String sqlDelete = "DELETE FROM Librarians WHERE userName = ?";
    try (Connection conn = DatabaseHelper.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sqlDelete)) {
      stmt.setString(1, testUsername);
      stmt.executeUpdate();
    }
  }

  @Test
  void testLoginSuccess() {
    assertTrue(LibrarianService.login(testUsername, testPassword) != null,
        "Login should succeed with valid credentials.");
  }

  @Test
  void testLoginFailureWrongPassword() {
    assertNull(LibrarianService.login(testUsername, "wrongpassword"),
        "Login should fail with invalid password.");
  }

  @Test
  void testLoginFailureNonExistentUser() {
    assertNull(LibrarianService.login("nonexistentuser", "anyPassword"),
        "Login should fail for non-existent user.");
  }

  @Test
  void testLoginFailureEmptyUsername() {
    assertNull(LibrarianService.login("", testPassword),
        "Login should fail with empty username.");
  }

  @Test
  void testLoginFailureEmptyPassword() {
    assertNull(LibrarianService.login(testUsername, ""),
        "Login should fail with empty password.");
  }

  @Test
  void testSaveBook() {
    Book book = new Book("Test Book", "Test Author", "1234567890", "Test Description", 10, 0,
        "Test URL");
    LibrarianService.saveBook(book);

    String sqlSelect = "SELECT * FROM Books WHERE isbn = ?";
    try (Connection conn = DatabaseHelper.getConnection(); PreparedStatement stmt = conn.prepareStatement(
        sqlSelect)) {
      stmt.setString(1, "1234567890");
      ResultSet rs = stmt.executeQuery();

      assertTrue(rs.next(), "Book should be saved in the database");
      assertEquals("Test Book", rs.getString("title"), "Book title should match");
      assertEquals("Test Author", rs.getString("author"), "Book author should match");
      assertEquals("1234567890", rs.getString("isbn"), "Book ISBN should match");
      assertEquals("Test Description", rs.getString("description"),
          "Book description should match");
      assertEquals(10, rs.getInt("totalBooks"), "Total books should match");
      assertEquals(0, rs.getInt("borrowedBooks"), "Borrowed books count should match");
      assertEquals("Test URL", rs.getString("thumbnailURL"), "Thumbnail URL should match");

    } catch (SQLException e) {
      fail("SQL exception occurred: " + e.getMessage());
    } finally {
      deleteTestBook();
    }
  }

  @Test
  void testUpdateTotalBook() {
    Book book = new Book("Test Book", "Test Author", "1234567890", "Test Description", 10, 0,
        "Test URL");
    LibrarianService.saveBook(book);

    book.setTotalDocument(20);
    LibrarianService.updateTotalBook(book);

    String sqlSelect = "SELECT * FROM Books WHERE isbn = ?";
    try (Connection conn = DatabaseHelper.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sqlSelect)) {
      stmt.setString(1, "1234567890");
      ResultSet rs = stmt.executeQuery();
      assertTrue(rs.next(), "Book should be present in the database");
      assertEquals(20, rs.getInt("totalBooks"), "Total books should be updated");
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      deleteTestBook();
    }
  }

  @Test
  void testSaveThesis() {
    Thesis thesis = new Thesis("Test Thesis", "Test Author", "Test Description", 10, 0, "Test URL",
        "Test University");
    LibrarianService.saveThesis(thesis);

    String sqlSelect = "SELECT * FROM Thesis WHERE title = ?";
    try (Connection conn = DatabaseHelper.getConnection(); PreparedStatement stmt = conn.prepareStatement(
        sqlSelect)) {
      stmt.setString(1, "Test Thesis");
      ResultSet rs = stmt.executeQuery();

      assertTrue(rs.next(), "Thesis should be saved in the database");
      assertEquals("Test Thesis", rs.getString("title"), "Thesis title should match");
    } catch (SQLException e) {
      fail("SQL exception occurred: " + e.getMessage());
    } finally {
      deleteTestThesis();
    }
  }

  @Test
  void testUpdateTotalThesis() {
    Thesis thesis = new Thesis("Test Thesis", "Test Author", "Test Description", 10, 0, "Test URL",
        "Test University");
    LibrarianService.saveThesis(thesis);
    List<Thesis> listthesis = UserService.searchThesisByTitle("Test Thesis");
    thesis = listthesis.get(0);
    thesis.setTotalDocument(20);
    LibrarianService.updateTotalThesis(thesis);

    String sqlSelect = "SELECT * FROM Thesis WHERE title = ?";
    try (Connection conn = DatabaseHelper.getConnection(); PreparedStatement stmt = conn.prepareStatement(
        sqlSelect)) {
      stmt.setString(1, "Test Thesis");
      ResultSet rs = stmt.executeQuery();
      assertTrue(rs.next(), "Thesis should be present in the database");
      assertEquals(20, rs.getInt("totalTheses"), "Total theses should be updated");
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      deleteTestThesis();
    }
  }

  @Test
  void testDeleteBook() {
    Book book = new Book("Test Book", "Test Author", "1234567890", "Test Description", 10, 0,
        "Test URL");
    LibrarianService.saveBook(book);

    LibrarianService.deleteBook("1234567890");

    String sqlSelect = "SELECT * FROM Books WHERE isbn = ?";
    try (Connection conn = DatabaseHelper.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sqlSelect)) {
      stmt.setString(1, "1234567890");
      ResultSet rs = stmt.executeQuery();
      assertFalse(rs.next(), "Book should be deleted from the database");
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  @Test
  void testListBorrowers() {
    String sqlInsertBorrower = "INSERT INTO Borrowers (fullName, userName, password) VALUES (?, ?, ?)";
    try (Connection conn = DatabaseHelper.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sqlInsertBorrower)) {
      for (int i = 1; i <= 3; i++) {
        stmt.setString(1, "Test Borrower " + i);
        stmt.setString(2, "borrower" + i);
        stmt.setString(3, "password" + i);
        stmt.executeUpdate();
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }

    List<Borrower> borrowers = LibrarianService.listBorrowers();
    String sqlDeleteBorrower = "DELETE FROM Borrowers WHERE userName LIKE 'borrower%'";
    try (Connection conn = DatabaseHelper.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sqlDeleteBorrower)) {
      stmt.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    }
    assertNotNull(borrowers, "Borrowers list should not be null");
    assertFalse(borrowers.isEmpty(), "Borrowers list should not be empty");
  }

  @Test
  void returnStatusTest() throws SQLException {
    String sqlInsert = "INSERT INTO Borrowers (fullName, userName, password) VALUES (?, ?, ?)";
    try (Connection conn = DatabaseHelper.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sqlInsert)) {
      stmt.setString(1, testFullName);
      stmt.setString(2, testUsername);
      stmt.setString(3, testPassword);
      stmt.executeUpdate();
    }
    Borrower borrowertest = BorrowerService.loginUser(testUsername, testPassword);
    int borrowerId = borrowertest.getId();
    String insertSql =
        "INSERT INTO BorrowedDocumentRecord (borrowerId, documentId, quantity, borrowedDate, returnDate, title, type) "
            +
            "VALUES (?, ?, ?, ?, ?, ?, ?)";
    String deleteSql = "DELETE FROM BorrowedDocumentRecord WHERE borrowerId = ?";

    try (Connection conn = DatabaseHelper.getConnection()) {
      // Case 1: No Borrowed
      String status = LibrarianService.returnStatus(borrowerId);
      assertEquals("No Borrowed", status, "Borrower should have no borrowed records initially.");

      // Insert overdue borrowed record
      try (PreparedStatement stmt = conn.prepareStatement(insertSql)) {
        stmt.setInt(1, borrowerId);
        stmt.setString(2, "12345"); // Arbitrary documentId
        stmt.setInt(3, 1); // Quantity
        stmt.setDate(4, Date.valueOf(LocalDate.now().minusDays(10))); // Borrowed 10 days ago
        stmt.setDate(5, Date.valueOf(LocalDate.now().minusDays(5))); // Overdue by 5 days
        stmt.setString(6, "Overdue Document");
        stmt.setString(7, "Book");
        stmt.executeUpdate();
      }

      // Case 2: Overdue
      status = LibrarianService.returnStatus(borrowerId);
      assertEquals("Overdue", status, "Borrower should be marked as 'Overdue'.");

      // Delete the overdue record and insert non-overdue record
      try (PreparedStatement stmt = conn.prepareStatement(deleteSql)) {
        stmt.setInt(1, borrowerId);
        stmt.executeUpdate();
      }

      try (PreparedStatement stmt = conn.prepareStatement(insertSql)) {
        stmt.setInt(1, borrowerId);
        stmt.setString(2, "67890"); // Arbitrary documentId
        stmt.setInt(3, 1); // Quantity
        stmt.setDate(4, Date.valueOf(LocalDate.now().minusDays(5))); // Borrowed 5 days ago
        stmt.setDate(5, Date.valueOf(LocalDate.now().plusDays(5))); // Not overdue
        stmt.setString(6, "Non-Overdue Document");
        stmt.setString(7, "Book");
        stmt.executeUpdate();
      }

      // Case 3: Good Borrower
      status = LibrarianService.returnStatus(borrowerId);
      assertEquals("Good Borrower", status, "Borrower should be marked as 'Good Borrower'.");

    } catch (SQLException e) {
      e.printStackTrace();
      fail("Database error occurred: " + e.getMessage());
    } finally {
      // Clean up all test data
      try (Connection conn = DatabaseHelper.getConnection(); PreparedStatement stmt = conn.prepareStatement(
          deleteSql)) {
        stmt.setInt(1, borrowerId);
        stmt.executeUpdate();
      } catch (SQLException e) {
        e.printStackTrace();
      }
      String sqlDelete = "DELETE FROM Borrowers WHERE userName = ?";
      try (Connection conn = DatabaseHelper.getConnection();
          PreparedStatement stmt = conn.prepareStatement(sqlDelete)) {
        stmt.setString(1, testUsername);
        stmt.executeUpdate();
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }

  }

  @Test
  void testRegister() {
    String testFullName = "Test Librarian";
    String testUserName = "testUser";
    String testPassword = "testPassword";

    String deleteSql = "DELETE FROM Librarians WHERE userName = ?";

    try (Connection conn = DatabaseHelper.getConnection()) {

      try (PreparedStatement stmt = conn.prepareStatement(deleteSql)) {
        stmt.setString(1, testUserName);
        stmt.executeUpdate();
      }

      boolean registerResult = LibrarianService.register(testFullName, testUserName, testPassword);
      assertTrue(registerResult, "User registration should succeed.");

      boolean duplicateRegisterResult = LibrarianService.register(testFullName, testUserName,
          testPassword);
      assertFalse(duplicateRegisterResult, "Duplicate registration should fail.");

    } catch (SQLException e) {
      fail("Database error occurred during the test: " + e.getMessage());
    } finally {
      try (Connection conn = DatabaseHelper.getConnection();
          PreparedStatement stmt = conn.prepareStatement(deleteSql)) {
        stmt.setString(1, testUserName);
        stmt.executeUpdate();
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
  }

  @Test
  void testUserExists() {
    String testFullName = "Test Librarian";
    String testUserName = "testUser";
    String testPassword = "testPassword";

    String deleteSql = "DELETE FROM Librarians WHERE userName = ?";

    try (Connection conn = DatabaseHelper.getConnection()) {
      try (PreparedStatement stmt = conn.prepareStatement(deleteSql)) {
        stmt.setString(1, testUserName);
        stmt.executeUpdate();
      }

      boolean userExistsBefore = LibrarianService.userExists(testUserName);
      assertFalse(userExistsBefore, "User should not exist before registration.");

      String insertSql = "INSERT INTO Librarians (fullName, userName, password) VALUES (?, ?, ?)";
      try (PreparedStatement stmt = conn.prepareStatement(insertSql)) {
        stmt.setString(1, testFullName);
        stmt.setString(2, testUserName);
        stmt.setString(3, testPassword);
        stmt.executeUpdate();
      }

      boolean userExistsAfter = LibrarianService.userExists(testUserName);
      assertTrue(userExistsAfter, "User should exist after manual insertion.");

    } catch (SQLException e) {
      fail("Database error occurred during the test: " + e.getMessage());
    } finally {
      try (Connection conn = DatabaseHelper.getConnection();
          PreparedStatement stmt = conn.prepareStatement(deleteSql)) {
        stmt.setString(1, testUserName);
        stmt.executeUpdate();
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
  }


}

