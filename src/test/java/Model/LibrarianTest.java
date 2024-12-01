package Model;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import services.DatabaseHelper;

public class LibrarianTest {

  private Librarian librarian;

  @BeforeEach
  public void setUp() {
    librarian = new Librarian(1, "John Doe", "johndoe", "password123");
  }

  @AfterEach
  public void tearDown() {
    try (Connection conn = DatabaseHelper.getConnection()) {
      String deleteBookSql = "DELETE FROM Books WHERE isbn = ?";
      try (PreparedStatement deleteBookStmt = conn.prepareStatement(deleteBookSql)) {
        deleteBookStmt.setString(1, "1234567890");
        deleteBookStmt.executeUpdate();
      }
      String deleteThesisSql = "DELETE FROM Thesis WHERE title = ?";
      try (PreparedStatement deleteThesisStmt = conn.prepareStatement(deleteThesisSql)) {
        deleteThesisStmt.setString(1, "Sample Thesis");
        deleteThesisStmt.executeUpdate();
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  @Test
  public void testAddBook_NewBook() {
    Book book = User.getBookByIsbn("1234567890");
    assertNull(book, "Book should not exist initially");

    librarian.addBook("Sample Book", "Author Name", "1234567890", "Description", 10,
        "thumbnailURL");

    book = User.getBookByIsbn("1234567890");
    assertNotNull(book, "Book should be added");
    assertEquals("Sample Book", book.getTitle(), "Book title should match");
  }

  @Test
  public void testAddBook_ExistingBook() {
    librarian.addBook("Sample Book", "Author Name", "1234567890", "Description", 10,
        "thumbnailURL");

    librarian.addBook("Sample Book", "Author Name", "1234567890", "Description", 5, "thumbnailURL");

    Book book = User.getBookByIsbn("1234567890");
    assertNotNull(book, "Book should exist");
    assertEquals(15, book.getTotalDocument(), "Total books should be updated");
  }

  @Test
  public void testAddThesis_NewThesis() {
    ArrayList<Thesis> theses = User.searchThesisByTitle("Sample Thesis");
    assertTrue(theses.isEmpty(), "Thesis should not exist initially");

    librarian.addThesis("Sample Thesis", "Author Name", "University", "Description", 10,
        "thumbnailURL");

    theses = User.searchThesisByTitle("Sample Thesis");
    assertFalse(theses.isEmpty(), "Thesis should be added");
    Thesis thesis = theses.get(0);
    assertEquals("Sample Thesis", thesis.getTitle(), "Thesis title should match");
  }

  @Test
  public void testUpdateBook() {
    librarian.addBook("Sample Book", "Author Name", "1234567890", "Description", 10,
        "thumbnailURL");

    librarian.updateBook("1234567890", 5);

    Book book = User.getBookByIsbn("1234567890");
    assertEquals(15, book.getTotalDocument(), "Total books should be updated");
  }

  @Test
  public void testUpdateThesis() {
    librarian.addThesis("Sample Thesis", "Author Name", "University", "Description", 10,
        "thumbnailURL");
    Thesis thesis = User.searchThesisByTitle("Sample Thesis").get(0);

    librarian.updateThesis(thesis.getId(), 5);

    Thesis updatedThesis = User.getThesisById(thesis.getId());
    assertEquals(15, updatedThesis.getTotalDocument(), "Total theses should be updated");
  }

  @Test
  public void testDecreaseBook() {
    librarian.addBook("Sample Book", "Author Name", "1234567890", "Description", 10,
        "thumbnailURL");

    librarian.decreaseBook("1234567890", 5);

    Book book = User.getBookByIsbn("1234567890");
    assertEquals(5, book.getTotalDocument(), "Total books should be decreased");
  }

  @Test
  public void testDecreaseThesis() {
    librarian.addThesis("Sample Thesis", "Author Name", "University", "Description", 10,
        "thumbnailURL");
    Thesis thesis = User.searchThesisByTitle("Sample Thesis").get(0);

    librarian.decreaseThesis(thesis.getId(), 5);

    Thesis updatedThesis = User.getThesisById(thesis.getId());
    assertEquals(5, updatedThesis.getTotalDocument(), "Total theses should be decreased");
  }

  @Test
  public void testGetDeleteQuantity_Book() {
    librarian.addBook("Sample Book", "Author Name", "1234567890", "Description", 10,
        "thumbnailURL");
    int deleteQuantity = librarian.getDeleteQuantity("1234567890");
    assertEquals(10, deleteQuantity, "Delete quantity should match total books");
  }

  @Test
  public void testGetDeleteQuantity_Thesis() {
    librarian.addThesis("Sample Thesis", "Author Name", "University", "Description", 10,
        "thumbnailURL");
    Thesis thesis = User.searchThesisByTitle("Sample Thesis").get(0);
    int deleteQuantity = librarian.getDeleteQuantity(thesis.getId());
    assertEquals(10, deleteQuantity, "Delete quantity should match total theses");
  }
}
