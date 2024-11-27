package services;

import static org.junit.jupiter.api.Assertions.*;

import Model.BorrowedBookRecord;
import Model.Borrower;
import Model.Book;
import java.time.LocalDate;
import java.util.List;
import services.BorrowerService;
import services.DatabaseHelper;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BorrowerServiceTest {

  private static final String TEST_USER_NAME = "testuser";
  private static final String TEST_PASSWORD = "testpass";
  private static final String TEST_FULL_NAME = "Test User";

  private Borrower borrower;

  @BeforeEach
  public void setUp() throws SQLException {
    // Set up database state before each test, for example, creating a user
    String sqlInsertBorrower = "INSERT INTO Borrowers (fullName, userName, password) VALUES (?, ?, ?)";
    try (Connection conn = DatabaseHelper.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sqlInsertBorrower)) {
      stmt.setString(1, TEST_FULL_NAME);
      stmt.setString(2, TEST_USER_NAME);
      stmt.setString(3, TEST_PASSWORD);
      stmt.executeUpdate();
    }

    // After inserting, login the user
    borrower = BorrowerService.loginUser(TEST_USER_NAME, TEST_PASSWORD);
  }

  @AfterEach
  public void tearDown() throws SQLException {
    // Clean up after each test, remove the created borrower
    String sqlDeleteBorrower = "DELETE FROM Borrowers WHERE userName = ?";
    try (Connection conn = DatabaseHelper.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sqlDeleteBorrower)) {
      stmt.setString(1, TEST_USER_NAME);
      stmt.executeUpdate();
    }
  }

  @Test
  void testRegister() {
    String testFullName = "Test Borrower";
    String testUserName = "testUser";
    String testPassword = "testPassword";

    String deleteSql = "DELETE FROM borrowers WHERE userName = ?";

    try (Connection conn = DatabaseHelper.getConnection()) {

      try (PreparedStatement stmt = conn.prepareStatement(deleteSql)) {
        stmt.setString(1, testUserName);
        stmt.executeUpdate();
      }

      boolean registerResult = BorrowerService.registerUser(testFullName, testUserName,
          testPassword);
      assertTrue(registerResult, "User registration should succeed.");

      boolean duplicateRegisterResult = BorrowerService.registerUser(testFullName, testUserName,
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
  void testLoginSuccess() {
    assertTrue(BorrowerService.loginUser(TEST_USER_NAME, TEST_PASSWORD) != null,
        "Login should succeed with valid credentials.");
  }

  @Test
  void testLoginFailureWrongPassword() {
    assertNull(LibrarianService.login(TEST_USER_NAME, "wrongpassword"),
        "Login should fail with invalid password.");
  }

  @Test
  void testLoginFailureNonExistentUser() {
    assertNull(LibrarianService.login("nonexistentuser", "anyPassword"),
        "Login should fail for non-existent user.");
  }

  @Test
  void testLoginFailureEmptyUsername() {
    assertNull(LibrarianService.login("", TEST_PASSWORD),
        "Login should fail with empty username.");
  }

  @Test
  void testLoginFailureEmptyPassword() {
    assertNull(LibrarianService.login(TEST_USER_NAME, ""),
        "Login should fail with empty password.");
  }

  @Test
  void testBorrowDocumentForBook() {
    // Prepare the Book record
    String isbn = "12345";
    String title = "Sample Book";
    int totalBooks = 10;
    int borrowedBooks = 2;

    String sqlInsertBook = "INSERT INTO Books (isbn, title, author, description, totalBooks, borrowedBooks) VALUES (?, ?, 'Author Name', 'Sample Description', ?, ?)";
    try (Connection conn = DatabaseHelper.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sqlInsertBook)) {
      stmt.setString(1, isbn);
      stmt.setString(2, title);
      stmt.setInt(3, totalBooks);
      stmt.setInt(4, borrowedBooks);
      stmt.executeUpdate();
    } catch (SQLException e) {
      fail("Failed to set up Book record: " + e.getMessage());
    }

    // Perform the borrowing
    boolean result = BorrowerService.borrowDocument(borrower, isbn, 3);
    assertTrue(result, "Borrowing a book should succeed.");

    // Validate the borrowed books count
    String sqlCheckBorrowed = "SELECT borrowedBooks FROM Books WHERE isbn = ?";
    try (Connection conn = DatabaseHelper.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sqlCheckBorrowed)) {
      stmt.setString(1, isbn);
      ResultSet rs = stmt.executeQuery();
      if (rs.next()) {
        int updatedBorrowedBooks = rs.getInt("borrowedBooks");
        assertEquals(5, updatedBorrowedBooks, "Borrowed books count should be updated correctly.");
      }
    } catch (SQLException e) {
      fail("Failed to validate Book record: " + e.getMessage());
    }

    // Clean up
    String deleterecord = "DELETE FROM borroweddocumentrecord WHERE borrowerId = ?";
    try (Connection conn = DatabaseHelper.getConnection();
        PreparedStatement stmt = conn.prepareStatement(deleterecord)) {
      stmt.setInt(1, borrower.getId());
      stmt.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    }
    String sqlDeleteBook = "DELETE FROM Books WHERE isbn = ?";
    try (Connection conn = DatabaseHelper.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sqlDeleteBook)) {
      stmt.setString(1, isbn);
      stmt.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  @Test
  void testBorrowDocumentForThesis() {
    // Prepare the Thesis record
    int thesisId = 345678910;
    String title = "Sample Thesis";
    int totalTheses = 5;
    int borrowedTheses = 1;

    String sqlInsertThesis = "INSERT INTO Thesis (id, title, author, university, description, totalTheses, borrowedTheses) VALUES (?, ?, 'Author Name', 'University Name', 'Sample Description', ?, ?)";
    try (Connection conn = DatabaseHelper.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sqlInsertThesis)) {
      stmt.setInt(1, thesisId);
      stmt.setString(2, title);
      stmt.setInt(3, totalTheses);
      stmt.setInt(4, borrowedTheses);
      stmt.executeUpdate();
    } catch (SQLException e) {
      fail("Failed to set up Thesis record: " + e.getMessage());
    }

    // Perform the borrowing
    boolean result = BorrowerService.borrowDocument(borrower, String.valueOf(thesisId), 2);
    assertTrue(result, "Borrowing a thesis should succeed.");

    // Validate the borrowed theses count
    String sqlCheckBorrowed = "SELECT borrowedTheses FROM Thesis WHERE id = ?";
    try (Connection conn = DatabaseHelper.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sqlCheckBorrowed)) {
      stmt.setInt(1, thesisId);
      ResultSet rs = stmt.executeQuery();
      if (rs.next()) {
        int updatedBorrowedTheses = rs.getInt("borrowedTheses");
        assertEquals(3, updatedBorrowedTheses,
            "Borrowed theses count should be updated correctly.");
      }
    } catch (SQLException e) {
      fail("Failed to validate Thesis record: " + e.getMessage());
    }

    // Clean up
    String deleterecord = "DELETE FROM borroweddocumentrecord WHERE borrowerId = ?";
    try (Connection conn = DatabaseHelper.getConnection();
        PreparedStatement stmt = conn.prepareStatement(deleterecord)) {
      stmt.setInt(1, borrower.getId());
      stmt.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    }
    String sqlDeleteThesis = "DELETE FROM Thesis WHERE id = ?";
    try (Connection conn = DatabaseHelper.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sqlDeleteThesis)) {
      stmt.setInt(1, thesisId);
      stmt.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  @Test
  void testReturnBookSuccess() {
    // Prepare a Book record
    String isbn = "98765";
    String title = "Test Book";
    int totalBooks = 10;
    int borrowedBooks = 2;

    String sqlInsertBook = "INSERT INTO Books (isbn, title, author, description, totalBooks, borrowedBooks) VALUES (?, ?, 'Author Name', 'Description', ?, ?)";
    String sqlInsertBorrowedRecord = "INSERT INTO BorrowedDocumentRecord (borrowerId, documentId, quantity, borrowedDate, returnDate, title, type) VALUES (?, ?, ?, ?, ?, ?, ?)";

    int recordId = -1;

    try (Connection conn = DatabaseHelper.getConnection()) {
      // Insert the book
      try (PreparedStatement stmt = conn.prepareStatement(sqlInsertBook)) {
        stmt.setString(1, isbn);
        stmt.setString(2, title);
        stmt.setInt(3, totalBooks);
        stmt.setInt(4, borrowedBooks);
        stmt.executeUpdate();
      }

      // Insert a borrowed record
      try (PreparedStatement stmt = conn.prepareStatement(sqlInsertBorrowedRecord,
          PreparedStatement.RETURN_GENERATED_KEYS)) {
        stmt.setInt(1, borrower.getId());
        stmt.setString(2, isbn);
        stmt.setInt(3, 2); // Quantity borrowed
        stmt.setDate(4, java.sql.Date.valueOf(LocalDate.now()));
        stmt.setDate(5, java.sql.Date.valueOf(LocalDate.now().plusDays(7)));
        stmt.setString(6, title);
        stmt.setString(7, "Book");
        stmt.executeUpdate();

        // Get the generated record ID
        try (ResultSet rs = stmt.getGeneratedKeys()) {
          if (rs.next()) {
            recordId = rs.getInt(1);
          }
        }
      }
    } catch (SQLException e) {
      fail("Failed to set up test data: " + e.getMessage());
    }

    // Test returning the book
    assertTrue(BorrowerService.returnBook(recordId), "Returning the book should succeed.");

    // Validate the borrowedBooks count
    String sqlCheckBorrowedBooks = "SELECT borrowedBooks FROM Books WHERE isbn = ?";
    try (Connection conn = DatabaseHelper.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sqlCheckBorrowedBooks)) {
      stmt.setString(1, isbn);
      ResultSet rs = stmt.executeQuery();
      if (rs.next()) {
        int updatedBorrowedBooks = rs.getInt("borrowedBooks");
        assertEquals(0, updatedBorrowedBooks, "Borrowed books count should decrease.");
      }
    } catch (SQLException e) {
      fail("Failed to validate Book record: " + e.getMessage());
    }

    // Validate the record deletion
    String sqlCheckBorrowedRecord = "SELECT COUNT(*) FROM BorrowedDocumentRecord WHERE id = ?";
    try (Connection conn = DatabaseHelper.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sqlCheckBorrowedRecord)) {
      stmt.setInt(1, recordId);
      ResultSet rs = stmt.executeQuery();
      if (rs.next()) {
        int count = rs.getInt(1);
        assertEquals(0, count, "The borrowed record should be deleted.");
      }
    } catch (SQLException e) {
      fail("Failed to validate BorrowedDocumentRecord: " + e.getMessage());
    }

    // Cleanup
    String sqlDeleteBook = "DELETE FROM Books WHERE isbn = ?";
    try (Connection conn = DatabaseHelper.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sqlDeleteBook)) {
      stmt.setString(1, isbn);
      stmt.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  @Test
  void testReturnBookNonExistentRecord() {
    // Attempt to return a non-existent borrowed record
    assertFalse(BorrowerService.returnBook(9999), "Returning a non-existent record should fail.");
  }

  @Test
  void testReturnThesisSuccess() {
    // Prepare a Thesis record
    long thesisId = 12345L;
    String title = "Test Thesis";
    int totalTheses = 5;
    int borrowedTheses = 1;

    String sqlInsertThesis = "INSERT INTO Thesis (id, title, author, university, description, totalTheses, borrowedTheses) VALUES (?, ?, 'Author Name', 'Test University', 'Description', ?, ?)";
    String sqlInsertBorrowedRecord = "INSERT INTO BorrowedDocumentRecord (borrowerId, documentId, quantity, borrowedDate, returnDate, title, type) VALUES (?, ?, ?, ?, ?, ?, ?)";

    int recordId = -1;

    try (Connection conn = DatabaseHelper.getConnection()) {
      // Insert the thesis
      try (PreparedStatement stmt = conn.prepareStatement(sqlInsertThesis)) {
        stmt.setLong(1, thesisId);
        stmt.setString(2, title);
        stmt.setInt(3, totalTheses);
        stmt.setInt(4, borrowedTheses);
        stmt.executeUpdate();
      }

      // Insert a borrowed record
      try (PreparedStatement stmt = conn.prepareStatement(sqlInsertBorrowedRecord,
          PreparedStatement.RETURN_GENERATED_KEYS)) {
        stmt.setInt(1, borrower.getId());
        stmt.setString(2, Long.toString(thesisId));
        stmt.setInt(3, 1); // Quantity borrowed
        stmt.setDate(4, java.sql.Date.valueOf(LocalDate.now()));
        stmt.setDate(5, java.sql.Date.valueOf(LocalDate.now().plusDays(7)));
        stmt.setString(6, title);
        stmt.setString(7, "Thesis");
        stmt.executeUpdate();

        // Get the generated record ID
        try (ResultSet rs = stmt.getGeneratedKeys()) {
          if (rs.next()) {
            recordId = rs.getInt(1);
          }
        }
      }
    } catch (SQLException e) {
      fail("Failed to set up test data: " + e.getMessage());
    }

    // Test returning the thesis
    assertTrue(BorrowerService.returnBook(recordId), "Returning the thesis should succeed.");

    // Validate the borrowedTheses count
    String sqlCheckBorrowedTheses = "SELECT borrowedTheses FROM Thesis WHERE id = ?";
    try (Connection conn = DatabaseHelper.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sqlCheckBorrowedTheses)) {
      stmt.setLong(1, thesisId);
      ResultSet rs = stmt.executeQuery();
      if (rs.next()) {
        int updatedBorrowedTheses = rs.getInt("borrowedTheses");
        assertEquals(0, updatedBorrowedTheses, "Borrowed theses count should decrease.");
      }
    } catch (SQLException e) {
      fail("Failed to validate Thesis record: " + e.getMessage());
    }

    // Validate the record deletion
    String sqlCheckBorrowedRecord = "SELECT COUNT(*) FROM BorrowedDocumentRecord WHERE id = ?";
    try (Connection conn = DatabaseHelper.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sqlCheckBorrowedRecord)) {
      stmt.setInt(1, recordId);
      ResultSet rs = stmt.executeQuery();
      if (rs.next()) {
        int count = rs.getInt(1);
        assertEquals(0, count, "The borrowed record should be deleted.");
      }
    } catch (SQLException e) {
      fail("Failed to validate BorrowedDocumentRecord: " + e.getMessage());
    }

    // Cleanup
    String sqlDeleteThesis = "DELETE FROM Thesis WHERE id = ?";
    try (Connection conn = DatabaseHelper.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sqlDeleteThesis)) {
      stmt.setLong(1, thesisId);
      stmt.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  @Test
  void testReturnThesisNonExistentRecord() {
    // Attempt to return a non-existent borrowed record
    assertFalse(BorrowerService.returnBook(9999), "Returning a non-existent record should fail.");
  }

  @Test
  void testListBorrowedBooks() {
    int borrowerId = borrower.getId(); // Example borrower ID
    String documentId1 = "B001";
    String documentId2 = "T001";

    // Test data for a Book and a Thesis
    String sqlInsertBorrowedRecord = "INSERT INTO BorrowedDocumentRecord (borrowerId, documentId, quantity, borrowedDate, returnDate, title, type) VALUES (?, ?, ?, ?, ?, ?, ?)";

    try (Connection conn = DatabaseHelper.getConnection()) {
      // Insert test record for a Book
      try (PreparedStatement stmt = conn.prepareStatement(sqlInsertBorrowedRecord)) {
        stmt.setInt(1, borrowerId);
        stmt.setString(2, documentId1);
        stmt.setInt(3, 2);
        stmt.setDate(4, java.sql.Date.valueOf(LocalDate.now().minusDays(5))); // Borrowed 5 days ago
        stmt.setDate(5, java.sql.Date.valueOf(LocalDate.now().plusDays(10))); // Return in 10 days
        stmt.setString(6, "Test Book Title");
        stmt.setString(7, "Book");
        stmt.executeUpdate();
      }

      // Insert test record for a Thesis
      try (PreparedStatement stmt = conn.prepareStatement(sqlInsertBorrowedRecord)) {
        stmt.setInt(1, borrowerId);
        stmt.setString(2, documentId2);
        stmt.setInt(3, 1);
        stmt.setDate(4, java.sql.Date.valueOf(LocalDate.now().minusDays(3))); // Borrowed 3 days ago
        stmt.setDate(5, java.sql.Date.valueOf(LocalDate.now().plusDays(7))); // Return in 7 days
        stmt.setString(6, "Test Thesis Title");
        stmt.setString(7, "Thesis");
        stmt.executeUpdate();
      }
    } catch (SQLException e) {
      fail("Failed to set up test data: " + e.getMessage());
    }

    // Call the method and verify the results
    List<BorrowedBookRecord> borrowedBooks = BorrowerService.listBorrowedBooks(borrowerId);
    assertEquals(2, borrowedBooks.size(), "The number of borrowed records should be 2.");

    // Verify the Book record
    BorrowedBookRecord bookRecord = borrowedBooks.stream()
        .filter(record -> record.getDocumentId().equals(documentId1))
        .findFirst()
        .orElse(null);
    assertNotNull(bookRecord, "Book record should exist.");
    assertEquals("Test Book Title", bookRecord.getTitle());
    assertEquals(2, bookRecord.getQuantity());
    assertEquals("Book", bookRecord.getType());

    // Verify the Thesis record
    BorrowedBookRecord thesisRecord = borrowedBooks.stream()
        .filter(record -> record.getDocumentId().equals(documentId2))
        .findFirst()
        .orElse(null);
    assertNotNull(thesisRecord, "Thesis record should exist.");
    assertEquals("Test Thesis Title", thesisRecord.getTitle());
    assertEquals(1, thesisRecord.getQuantity());
    assertEquals("Thesis", thesisRecord.getType());

    // Cleanup
    String sqlDeleteBorrowedRecord = "DELETE FROM BorrowedDocumentRecord WHERE borrowerId = ?";
    try (Connection conn = DatabaseHelper.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sqlDeleteBorrowedRecord)) {
      stmt.setInt(1, borrowerId);
      stmt.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  @Test
  void testListBorrowedBooksEmpty() {
    int borrowerId = 9999; // Non-existent borrower ID

    // Call the method and verify the result
    List<BorrowedBookRecord> borrowedBooks = BorrowerService.listBorrowedBooks(borrowerId);
    assertTrue(borrowedBooks.isEmpty(), "The borrowed books list should be empty.");
  }

  @Test
  void testUserExists() {
    String nonExistentUserName = "nonExistentUser";

    // Test Case 1: Check for an existing user
    boolean exists = BorrowerService.userExists(TEST_USER_NAME);
    assertTrue(exists, "The user should exist in the database.");

    // Test Case 2: Check for a non-existent user
    boolean doesNotExist = BorrowerService.userExists(nonExistentUserName);
    assertFalse(doesNotExist, "The user should not exist in the database.");
  }

  @Test
  void testGetBorrowerStatusNoActiveLoans() {
    int borrowerId = borrower.getId(); // Placeholder for test borrower ID
    String status = BorrowerService.getBorrowerStatus(borrowerId);
    assertEquals("No active loans", status,
        "Borrower with no loans should have 'No active loans' status.");
    // Cleanup: Remove test data
    cleanupTestBorrowerData(borrowerId);
  }

  @Test
  void testGetBorrowerStatusActiveLoans() {
    int borrowerId = borrower.getId(); // Placeholder for test borrower ID
    LocalDate borrowedDate = LocalDate.now().minusWeeks(2); // 2 weeks ago
    LocalDate returnDate = LocalDate.now().minusDays(1);    // Overdue by 1 day
    insertBorrowedDocumentRecord(borrowerId, "Test Document 1", "DOC001", 1, borrowedDate,
        returnDate, "Book");
    String status = BorrowerService.getBorrowerStatus(borrowerId);
    assertEquals("Has overdue loans", status,
        "Borrower with overdue loans should have 'Has overdue loans' status.");
    // Cleanup: Remove test data
    cleanupTestBorrowerData(borrowerId);
  }

  @Test
  void testGetBorrowerInGoodStanding() {
    int borrowerId = borrower.getId(); // Placeholder for test borrower ID
    LocalDate borrowedDate2 = LocalDate.now().minusDays(3); // Borrowed 3 days ago
    LocalDate returnDate2 = LocalDate.now().plusWeeks(1);   // Due in 1 week
    insertBorrowedDocumentRecord(borrowerId, "Test Document 2", "DOC002", 1, borrowedDate2,
        returnDate2, "Thesis");

    String status = BorrowerService.getBorrowerStatus(borrowerId);
    assertEquals("In good standing", status,
        "Borrower should still have 'Has overdue loans' due to the overdue record.");
// Cleanup: Remove test data
    cleanupTestBorrowerData(borrowerId);
  }

  // Helper Method: Insert a Borrowed Document Record
  private void insertBorrowedDocumentRecord(int borrowerId, String title, String documentId,
      int quantity, LocalDate borrowedDate, LocalDate returnDate, String type) {
    String sqlInsertRecord = "INSERT INTO BorrowedDocumentRecord (borrowerId, title, documentId, quantity, borrowedDate, returnDate, type) VALUES (?, ?, ?, ?, ?, ?, ?)";
    try (Connection conn = DatabaseHelper.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sqlInsertRecord)) {
      stmt.setInt(1, borrowerId);
      stmt.setString(2, title);
      stmt.setString(3, documentId);
      stmt.setInt(4, quantity);
      stmt.setDate(5, java.sql.Date.valueOf(borrowedDate));
      stmt.setDate(6, java.sql.Date.valueOf(returnDate));
      stmt.setString(7, type);
      stmt.executeUpdate();
    } catch (SQLException e) {
      fail("Failed to insert borrowed document record: " + e.getMessage());
    }
  }

  // Helper Method: Cleanup Test Data
  private void cleanupTestBorrowerData(int borrowerId) {
    String sqlDeleteRecords = "DELETE FROM BorrowedDocumentRecord WHERE borrowerId = ?";
    try (Connection conn = DatabaseHelper.getConnection()) {
      try (PreparedStatement stmt = conn.prepareStatement(sqlDeleteRecords)) {
        stmt.setInt(1, borrowerId);
        stmt.executeUpdate();
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }
}
