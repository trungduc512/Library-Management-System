package classes;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BorrowerTest {

  private Borrower borrower;
  private final String testUsername = "testuser56789";
  private final String testPassword = "testpass56789";
  private final String testFullName = "John Doe";

  // Helper method to insert a test book
  private void insertTestBook(String title) throws SQLException {
    String sqlInsert = "INSERT INTO Books (title, author, isbn, description, total_books, borrowed_books) VALUES (?, ?, ?, ?, ?, ?)";
    try (Connection conn = DatabaseHelper.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sqlInsert)) {
      stmt.setString(1, title);
      stmt.setString(2, "Author Name");
      stmt.setString(3, "978-3-16-148410-0"); // Example ISBN
      stmt.setString(4, "Book description.");
      stmt.setInt(5, 10); // Total books
      stmt.setInt(6, 0);  // Borrowed books
      stmt.executeUpdate();
    }
  }

  // Helper method to delete a test book by title
  private void deleteTestBook(String title) throws SQLException {
    String sqlDelete = "DELETE FROM Books WHERE title = ?";
    try (Connection conn = DatabaseHelper.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sqlDelete)) {
      stmt.setString(1, title);
      stmt.executeUpdate();
    }
  }

  // Helper method to insert a test book with a specific ISBN
  private void insertTestBookWithIsbn(String isbn) throws SQLException {
    String sqlInsert = "INSERT INTO Books (title, author, isbn, description, total_books, borrowed_books) VALUES (?, ?, ?, ?, ?, ?)";
    try (Connection conn = DatabaseHelper.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sqlInsert)) {
      stmt.setString(1, "Sample Book");
      stmt.setString(2, "Author Name");
      stmt.setString(3, isbn);
      stmt.setString(4, "Sample book description.");
      stmt.setInt(5, 10); // Total books
      stmt.setInt(6, 0);  // Borrowed books
      stmt.executeUpdate();
    }
  }

  // Helper method to delete a test book by ISBN
  private void deleteTestBookByIsbn(String isbn) throws SQLException {
    String sqlDelete = "DELETE FROM books WHERE isbn = ?";
    try (Connection conn = DatabaseHelper.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sqlDelete)) {
      stmt.setString(1, isbn);
      stmt.executeUpdate();
    }
  }

  @BeforeEach
  void setUp() throws SQLException {
    // Create a test borrower in the database
    String sqlInsert = "INSERT INTO Borrowers (ho_ten, ten_tai_khoan, password) VALUES (?, ?, ?)";
    try (Connection conn = DatabaseHelper.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sqlInsert)) {
      stmt.setString(1, testFullName);
      stmt.setString(2, testUsername);
      stmt.setString(3, testPassword);
      stmt.executeUpdate();
    }

    // Initialize the Borrower object for testing
    LMS lms = LMS.getInstance();
    lms.loginBorrower(testUsername, testPassword);
    borrower = (Borrower) lms.getCurrentUser();
  }

  @AfterEach
  void tearDown() throws SQLException {
    // Remove the test borrower from the database
    String sqlDelete = "DELETE FROM Borrowers WHERE ten_tai_khoan = ?";
    try (Connection conn = DatabaseHelper.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sqlDelete)) {
      stmt.setString(1, testUsername);
      stmt.executeUpdate();
    }
  }

  @Test
  void testLoginSuccess() {
    // Case 1: Valid login
    assertTrue(LMS.getInstance().loginBorrower(testUsername, testPassword),
        "Login should succeed with valid credentials.");
  }

  @Test
  void testLoginFailureWrongPassword() {
    // Case 2: Invalid password
    assertFalse(LMS.getInstance().loginBorrower(testUsername, "wrongpassword"),
        "Login should fail with invalid password.");
  }

  @Test
  void testLoginFailureNonExistentUser() {
    // Case 3: Non-existent user
    assertFalse(LMS.getInstance().loginBorrower("nonexistentuser", "anyPassword"),
        "Login should fail for non-existent user.");
  }

  @Test
  void testLoginFailureEmptyUsername() {
    // Case 4: Empty username
    assertFalse(LMS.getInstance().loginBorrower("", testPassword),
        "Login should fail with empty username.");
  }

  @Test
  void testLoginFailureEmptyPassword() {
    // Case 5: Empty password
    assertFalse(LMS.getInstance().loginBorrower(testUsername, ""),
        "Login should fail with empty password.");
  }

  @Test
  void testLoginFailureNullPassword() {
    // Case 7: Null password
    assertThrows(NullPointerException.class, () -> {
      LMS.getInstance().loginBorrower(testUsername, null);
    }, "Login should throw NullPointerException for null password.");
  }

  @Test
  void testGetFullName() {
    // Test getFullName method
    assertEquals(testFullName, borrower.getFullName(),
        "getFullName should return the correct full name.");
  }

  @Test
  void testSetFullName() {
    // Test setFullName method
    String newFullName = "Jane Smith";
    borrower.setFullName(newFullName);
    assertEquals(newFullName, borrower.getFullName(), "setFullName should update the full name.");
  }

  @Test
  void testGetUserName() {
    // Test getUserName method
    assertEquals(testUsername, borrower.getUserName(),
        "getUserName should return the correct username.");
  }

  @Test
  void testSetUserName() {
    // Test setUserName method
    String newUserName = "janesmith";
    borrower.setUserName(newUserName);
    assertEquals(newUserName, borrower.getUserName(), "setUserName should update the username.");
  }

  @Test
  void testGetPassword() {
    // Test getPassword method
    assertEquals(testPassword, borrower.getPassword(),
        "getPassword should return the correct password.");
  }

  @Test
  void testSetPassword() {
    // Test setPassword method
    String newPassword = "newpassword";
    borrower.setPassword(newPassword);
    assertEquals(newPassword, borrower.getPassword(), "setPassword should update the password.");
  }

  @Test
  void testSearchBooksByTitleValid() throws SQLException {
    // Assuming there is a book with title 'Java Programming' in the database
    String title = "Java Programming";
    // You may need to insert a book record for this test case
    insertTestBook(title);

    // Search for the book
    ArrayList<Book> results = Borrower.searchBooksByTitle(title);
    assertFalse(results.isEmpty(), "Search should return results for the title.");
    assertEquals(title, results.get(0).getTitle(),
        "The searched book title should match the expected title.");

    // Cleanup the test book
    deleteTestBook(title);
  }

  @Test
  void testSearchBooksByTitleInValid() throws SQLException {
    // Assuming there is a book with title 'Java Programming' in the database
    String title = "Test";

    // Search for the book
    ArrayList<Book> results = Borrower.searchBooksByTitle(title);
    assertTrue(results.isEmpty(), "Search should not return results for an invalid title.");
  }

  @Test
  void testGetAllBooks() {
    // Fetch all books
    ArrayList<Book> books = Borrower.getAllBooks();
    assertNotNull(books, "getAllBooks should return a non-null list.");
    assertTrue(books.size() > 0, "getAllBooks should return a list with at least one book.");
  }

  @Test
  void testGetBookByIsbnValid() throws SQLException {
    // Assuming there is a book with a specific ISBN in the database
    String isbn = "978-3-16-148410-0"; // Example ISBN
    // You may need to insert a book record for this test case
    insertTestBookWithIsbn(isbn);

    // Get the book by ISBN
    Book book = Borrower.getBookByIsbn(isbn);
    assertNotNull(book, "getBookByIsbn should return a non-null book.");
    assertEquals(isbn, book.getIsbn(), "The fetched book ISBN should match the expected ISBN.");

    // Cleanup the test book
    deleteTestBookByIsbn(isbn);
  }

  @Test
  void testGetBookByIsbnInValid() throws SQLException {
    // Assuming there is a book with a specific ISBN in the database
    String isbn = "978-3-16-148410-0"; // Example ISBN
    // Get the book by ISBN
    Book book = Borrower.getBookByIsbn(isbn);
    assertNull(book, "getBookByIsbn should return null for an invalid ISBN.");
  }

  @Test
  void testUpdateProfile() {
    String newFullName = "Jane Doe";
    LMS lms = LMS.getInstance();
    lms.loginBorrower(testUsername, testPassword);
    borrower = (Borrower) lms.getCurrentUser();
    borrower.updateProfile(newFullName);
    // Verify that the profile was updated in the database
    String sqlQuery = "SELECT ho_ten FROM Borrowers WHERE ten_tai_khoan = ?";
    try (Connection conn = DatabaseHelper.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sqlQuery)) {
      stmt.setString(1, testUsername);
      ResultSet rs = stmt.executeQuery();
      if (rs.next()) {
        assertEquals(newFullName, rs.getString("ho_ten"),
            "Full name should be updated in the database.");
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  @Test
  void testBorrowBookByIsbnValid() throws SQLException {
    // Prepare a book to be borrowed
    String isbn = "12345123456789123"; // Example ISBN
    insertTestBookWithIsbn(isbn);
    boolean result = borrower.borrowBookByIsbn(isbn, 1); // Attempt to borrow the book
// Verify that the borrowed book record was added to the database
    List<BorrowedBookRecord> records = borrower.listBorrowedBooks();
    borrower.returnBook(records.getFirst().getBorrowerId());
    deleteTestBookByIsbn(isbn);

    assertTrue(result, "Should be able to borrow the book.");

  }

  @Test
  void testBorrowBookByIsbnWithNegativeQuantity() throws SQLException {
    // Prepare a book to be borrowed
    String isbn = "12345123456789123"; // Example ISBN
    insertTestBookWithIsbn(isbn);

    boolean result = borrower.borrowBookByIsbn(isbn, -1); // Attempt to borrow the book
    // Verify that the borrowed book record was added to the database
    deleteTestBookByIsbn(isbn);

    assertFalse(result, "Should not be able to borrow a book with a negative quantity.");

  }

  @Test
  void testListBorrowedBooks() throws SQLException {
    // Assuming that there are already borrowed books

    // Prepare a book to be borrowed
    String isbn = "12345123456789123"; // Example ISBN
    insertTestBookWithIsbn(isbn);
    boolean result = borrower.borrowBookByIsbn(isbn, 1); // Attempt to borrow the book
    isbn = "1234512345678912345"; // Example ISBN
    insertTestBookWithIsbn(isbn);
    result = borrower.borrowBookByIsbn(isbn, 1);
    List<BorrowedBookRecord> records = borrower.listBorrowedBooks();
    assertNotNull(records, "The list of borrowed books should not be null.");
    assertTrue(records.size() > 0, "There should be at least one borrowed book.");
    // Cleanup the test book
    for (BorrowedBookRecord record : records) {
      borrower.returnBook(record.getBorrowerId());
    }
    deleteTestBookByIsbn("12345123456789123");
    deleteTestBookByIsbn("1234512345678912345");
  }

  @Test
  void testReturnBook() throws SQLException {

    String isbn = "12345123456789123"; // Example ISBN
    insertTestBookWithIsbn(isbn);
    boolean result = borrower.borrowBookByIsbn(isbn, 1);
    // Now return the book
    List<BorrowedBookRecord> records = borrower.listBorrowedBooks();
    assertFalse(records.isEmpty(), "Borrowed records should not be empty before returning.");
    int recordId = records.getFirst().getBorrowerId();

    result = borrower.returnBook(recordId);
    assertTrue(result, "The book should be returned successfully.");

    // Verify that the book record no longer exists
    records = borrower.listBorrowedBooks();
    assertTrue(records.isEmpty(), "The borrowed records should be empty after returning.");

    // Cleanup
    deleteTestBookByIsbn(isbn);
  }

  @Test
  void testRegister() {
    String newUser = "newUser123";
    String newPassword = "newPassword123";
    String newFullName = "New User";

    boolean result = Borrower.register(newFullName, newUser, newPassword);
    assertTrue(result, "Registration should be successful.");

    // Check if user exists
    assertTrue(Borrower.userExists(newUser), "The newly registered user should exist.");

    // Cleanup the new user
    String sqlDelete = "DELETE FROM Borrowers WHERE ten_tai_khoan = ?";
    try (Connection conn = DatabaseHelper.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sqlDelete)) {
      stmt.setString(1, newUser);
      stmt.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

}
