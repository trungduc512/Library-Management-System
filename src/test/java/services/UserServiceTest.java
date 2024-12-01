package services;

import static org.junit.jupiter.api.Assertions.*;

import Model.Book;
import Model.Borrower;
import Model.Document;
import Model.Thesis;
import java.util.List;
import org.junit.Before;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

public class UserServiceTest {

  @BeforeEach
  public void setUp() {
    // Thêm dữ liệu mẫu cho các bài kiểm tra
    try (Connection conn = DatabaseHelper.getConnection()) {
      String addBookSql = "INSERT INTO Books (title, author, isbn, description, totalBooks, borrowedBooks, thumbnailURL) VALUES (?, ?, ?, ?, ?, ?, ?)";
      try (PreparedStatement stmt = conn.prepareStatement(addBookSql)) {
        stmt.setString(1, "Test Book");
        stmt.setString(2, "Test Author");
        stmt.setString(3, "1234567890");
        stmt.setString(4, "Test Description");
        stmt.setInt(5, 10);
        stmt.setInt(6, 2);
        stmt.setString(7, "Test URL");
        stmt.executeUpdate();
      }

      String addThesisSql = "INSERT INTO Thesis (title, author, university, description, totalTheses, borrowedTheses, thumbnailURL) VALUES (?, ?, ?, ?, ?, ?, ?)";
      try (PreparedStatement stmt = conn.prepareStatement(addThesisSql)) {
        stmt.setString(1, "Test Thesis");
        stmt.setString(2, "Test Author");
        stmt.setString(3, "Test University");
        stmt.setString(4, "Test Description");
        stmt.setInt(5, 5);
        stmt.setInt(6, 1);
        stmt.setString(7, "Test URL");
        stmt.executeUpdate();
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  @AfterEach
  public void tearDown() {
    // Xóa dữ liệu mẫu sau các bài kiểm tra
    try (Connection conn = DatabaseHelper.getConnection()) {
      String deleteBookSql = "DELETE FROM Books WHERE isbn = ?";
      try (PreparedStatement stmt = conn.prepareStatement(deleteBookSql)) {
        stmt.setString(1, "1234567890");
        stmt.executeUpdate();
      }

      String deleteThesisSql = "DELETE FROM Thesis WHERE title = ?";
      try (PreparedStatement stmt = conn.prepareStatement(deleteThesisSql)) {
        stmt.setString(1, "Test Thesis");
        stmt.executeUpdate();
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  @Test
  public void testGetBookByIsbnValid() {
    Book book = UserService.getBookByIsbn("1234567890");
    assertNotNull(book, "Book should not be null");
    assertEquals("Test Book", book.getTitle(), "Book title should match");
  }

  @Test
  public void testGetBookByIsbnInvalid() {
    Book book = UserService.getBookByIsbn("1234567891");
    assertNull(book, "Book should be null");
  }

  @Test
  public void testSearchBooksByTitleValid() {
    ArrayList<Book> books = UserService.searchBooksByTitle("Test");
    assertFalse(books.isEmpty(), "Books list should not be empty");
    assertEquals("Test Book", books.get(0).getTitle(), "Book title should match");
  }

  @Test
  public void testSearchBooksByTitleInvalid() {
    ArrayList<Book> books = UserService.searchBooksByTitle("Invalid");
    assertTrue(books.isEmpty(), "Books list should be empty");
  }

  @Test
  public void testGetAllBooks() {
    ArrayList<Book> books = UserService.getAllBooks();
    assertFalse(books.isEmpty(), "Books list should not be empty");
    assertTrue(books.stream().anyMatch(book -> book.getIsbn().equals("1234567890")),
        "Books list should contain Test Book");
  }

  @Test
  public void testSearchThesisByTitleValid() {
    ArrayList<Thesis> theses = UserService.searchThesisByTitle("Test");
    assertFalse(theses.isEmpty(), "Theses list should not be empty");
    assertEquals("Test Thesis", theses.get(0).getTitle(), "Thesis title should match");
  }

  @Test
  public void testSearchThesisByTitleInvalid() {
    ArrayList<Thesis> theses = UserService.searchThesisByTitle("Invalid");
    assertTrue(theses.isEmpty(), "Theses list should be empty");
  }

  @Test
  public void testGetAllThesis() {
    ArrayList<Thesis> theses = UserService.getAllThesis();
    assertFalse(theses.isEmpty(), "Theses list should not be empty");
    assertTrue(theses.stream().anyMatch(thesis -> thesis.getTitle().equals("Test Thesis")),
        "Theses list should contain Test Thesis");
  }

  @Test
  public void testGetDocumentById_Book() {
    Document document = UserService.getDocumentById("1234567890");
    assertNotNull(document, "Document should not be null");
    assertTrue(document instanceof Book, "Document should be an instance of Book");
  }

  @Test
  public void testGetDocumentById_Thesis() {
    Document document = UserService.getDocumentById("1");
    assertNotNull(document, "Document should not be null");
    assertTrue(document instanceof Thesis, "Document should be an instance of Thesis");
  }

  @Test
  public void testGetTotalBooks() {
    int totalBooks = UserService.getTotalBooks();
    assertTrue(totalBooks > 0, "Total books should be greater than zero");
  }

  @Test
  public void testGetTotalBorrowedBooks() {
    int totalBorrowedBooks = UserService.getTotalBorrowedBooks();
    assertTrue(totalBorrowedBooks > 0, "Total borrowed books should be greater than zero");
  }

  @Test
  public void testListTopBorrowedBooks() {
    try (Connection conn = DatabaseHelper.getConnection()) {
      String addBookSql = "INSERT INTO Books (title, author, isbn, description, totalBooks, borrowedBooks, thumbnailURL) VALUES (?, ?, ?, ?, ?, ?, ?)";
      try (PreparedStatement stmt = conn.prepareStatement(addBookSql)) {
        for (int i = 1; i <= 5; i++) {
          stmt.setString(1, "Test Book " + i);
          stmt.setString(2, "Test Author " + i);
          stmt.setString(3, "1234567890" + i);
          stmt.setString(4, "Test Description " + i);
          stmt.setInt(5, 10);
          stmt.setInt(6, 99990 + i); // Varying number of borrowed books
          stmt.setString(7, "Test URL " + i);
          stmt.addBatch();
        }
        stmt.executeBatch();
      }
    } catch (SQLException e) {
      fail("Database insertion failed: " + e.getMessage());
    }
    List<Book> books = UserService.listTopBorrowedBooks();
    // Clean up the test data
    try (Connection conn = DatabaseHelper.getConnection()) {
      String deleteBookSql = "DELETE FROM Books WHERE isbn LIKE '1234567890%'";
      try (PreparedStatement stmt = conn.prepareStatement(deleteBookSql)) {
        stmt.executeUpdate();
      }
    } catch (SQLException e) {
      fail("Database cleanup failed: " + e.getMessage());
    }
    assertFalse(books.isEmpty(), "Books list should not be empty");
    assertEquals(5, books.size(), "Books list should contain 5 books");
  }

  @Test
  public void testGetTopBorrowers() {
    List<Borrower> borrowers = UserService.getTopBorrowers();
    assertFalse(borrowers.isEmpty(), "Borrowers list should not be empty");
  }
}

