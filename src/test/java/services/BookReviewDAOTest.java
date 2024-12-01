package services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import Model.BookReview;
import Model.Borrower;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class BookReviewDAOTest {

  private static final String testFullNameBorrower = "Test Borrower";
  private static final String testUsernameBorrower = "borrowertest";
  private static final String testPasswordBorrower = "passwordtest";
  private Borrower borrower;
  private static final String testDocId = "DOC12345";
  private static final String testDocTitle = "Test Document Title";
  private static final String testDocType = "Book";
  private static final int testQuantity = 1;
  private static final LocalDate borrowedDate = LocalDate.now();
  private static final LocalDate returnDate = borrowedDate.plusWeeks(1);
  private static final int TEST_RATING = 5;
  private static final String TEST_REVIEWER_NAME = "Test Reviewer";
  private static final String TEST_REVIEW_TEXT = "Great book!";

  @BeforeEach
  void setUp() throws SQLException {
    // Thêm Borrower vào bảng Borrowers
    String sqlInsertBorrower = "INSERT INTO Borrowers (fullName, userName, password) VALUES (?, ?, ?)";
    try (
        Connection conn = DatabaseHelper.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sqlInsertBorrower)) {
      stmt.setString(1, testFullNameBorrower);
      stmt.setString(2, testUsernameBorrower);
      stmt.setString(3, testPasswordBorrower);
      stmt.executeUpdate();
    }

    // Đăng nhập để lấy thông tin borrower
    borrower = BorrowerService.loginUser(testUsernameBorrower, testPasswordBorrower);

    // Thêm bản ghi vào bảng BorrowedDocumentRecord
    String sqlInsertBorrowedRecord = "INSERT INTO BorrowedDocumentRecord (borrowerId, documentId, quantity, borrowedDate, returnDate, title, type) VALUES (?, ?, ?, ?, ?, ?, ?)";
    try (
        Connection conn = DatabaseHelper.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sqlInsertBorrowedRecord)) {
      stmt.setInt(1, borrower.getId()); // ID của người mượn
      stmt.setString(2, testDocId); // ID của tài liệu
      stmt.setInt(3, testQuantity); // Số lượng tài liệu mượn
      stmt.setDate(4, Date.valueOf(borrowedDate)); // Ngày mượn
      stmt.setDate(5, Date.valueOf(returnDate)); // Ngày trả
      stmt.setString(6, testDocTitle); // Tên tài liệu
      stmt.setString(7, testDocType); // Loại tài liệu
      stmt.executeUpdate();
    }
  }

  @AfterEach
  void tearDown() throws SQLException {
    String deleteReview = "DELETE FROM DocumentReview WHERE documentId = ? AND reviewerId = ?";
    try (Connection conn = DatabaseHelper.getConnection();
        PreparedStatement stmt = conn.prepareStatement(deleteReview)) {
      stmt.setString(1, testDocId);
      stmt.setInt(2, borrower.getId());
      stmt.executeUpdate();
    }
    String sqlDeleteBorrowedRecord = "DELETE FROM BorrowedDocumentRecord WHERE borrowerId = ?";
    String sqlDeleteBorrower = "DELETE FROM Borrowers WHERE userName = ?";

    try (
        Connection conn = DatabaseHelper.getConnection();
        PreparedStatement stmt1 = conn.prepareStatement(sqlDeleteBorrowedRecord);
        PreparedStatement stmt2 = conn.prepareStatement(sqlDeleteBorrower)) {

      // Xóa bản ghi mượn tài liệu
      stmt1.setInt(1, borrower.getId());
      stmt1.executeUpdate();

      // Xóa Borrower
      stmt2.setString(1, testUsernameBorrower);
      stmt2.executeUpdate();
    }
  }

  @Test
  void testAddReview() throws SQLException {
    BookReviewDAO dao = new BookReviewDAO();

    // Add a review
    Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
    BookReviewDAO.ReviewStatus status = dao.addReview(
        borrower.getId(),
        testDocId,
        5,
        "Test Reviewer",
        "Great book!",
        currentTimestamp
    );

    assertEquals(BookReviewDAO.ReviewStatus.APPROVED, status,
        "Review should be added successfully.");

    // Verify review was added
    String query = "SELECT COUNT(*) AS total FROM DocumentReview WHERE documentId = ? AND reviewerId = ?";
    try (Connection conn = DatabaseHelper.getConnection();
        PreparedStatement stmt = conn.prepareStatement(query)) {

      stmt.setString(1, testDocId);
      stmt.setInt(2, borrower.getId());
      ResultSet rs = stmt.executeQuery();

      if (rs.next()) {
        int total = rs.getInt("total");
        assertEquals(1, total, "Review count should be 1 after adding a review.");
      }
    } finally {
      // Clean up
      String deleteReview = "DELETE FROM DocumentReview WHERE documentId = ? AND reviewerId = ?";
      try (Connection conn = DatabaseHelper.getConnection();
          PreparedStatement stmt = conn.prepareStatement(deleteReview)) {
        stmt.setString(1, testDocId);
        stmt.setInt(2, borrower.getId());
        stmt.executeUpdate();
      }
    }
  }

  @Test
  void testGetAllReviews() {
    BookReviewDAO dao = new BookReviewDAO();
    List<BookReview> reviews = dao.getAllReviews(testDocId);
    assertTrue(reviews.isEmpty(), "No reviews should exist initially.");
  }

  @Test
  void testGetReviewsOffset() throws SQLException {
    // Khởi tạo đối tượng DAO
    BookReviewDAO dao = new BookReviewDAO();
    // Thêm một bài đánh giá vào bảng DocumentReview
    String insertReviewSQL =
        "INSERT INTO DocumentReview (documentId, reviewerId, rating, reviewerName, reviewText, createdAt) "
            +
            "VALUES (?, ?, ?, ?, ?, ?)";
    try (Connection conn = DatabaseHelper.getConnection();
        PreparedStatement stmt = conn.prepareStatement(insertReviewSQL)) {
      stmt.setString(1, testDocId);
      stmt.setInt(2, borrower.getId()); // reviewerId
      stmt.setInt(3, TEST_RATING); // rating
      stmt.setString(4, TEST_REVIEWER_NAME); // reviewerName
      stmt.setString(5, TEST_REVIEW_TEXT); // reviewText
      stmt.setTimestamp(6, new Timestamp(System.currentTimeMillis())); // createdAt
      stmt.executeUpdate();
    }
    // Gọi phương thức getReviewsOffset để lấy reviews
    List<BookReview> reviews = dao.getReviewsOffset(testDocId, 0, 5);

    // Kiểm tra số lượng reviews trả về
    assertEquals(1, reviews.size(), "There should be 1 review returned.");

    // Kiểm tra các thông tin của review
    BookReview review = reviews.get(0);
    assertEquals(testDocId, review.getBookISBN(), "The document ID should match.");
    assertEquals(borrower.getId(), review.getReviewerId(), "The reviewer ID should match.");
    assertEquals(TEST_RATING, review.getRating(), "The rating should match.");
    assertEquals(TEST_REVIEWER_NAME, review.getReviewerName(), "The reviewer name should match.");
    assertEquals(TEST_REVIEW_TEXT, review.getReviewText(), "The review text should match.");
    assertTrue(review.getCreatedAt() != null, "The createdAt timestamp should not be null.");
  }

  @Test
  void testGetAverageRating() throws SQLException {
    BookReviewDAO dao = new BookReviewDAO();

    // Add a review
    dao.addReview(borrower.getId(), testDocId, 4, "Reviewer1", "Good book",
        new Timestamp(System.currentTimeMillis()));

    // Verify average rating
    double avgRating = dao.getAverageRating(testDocId);
    assertEquals(4.0, avgRating, "Average rating should match the added review's rating.");
  }

  @Test
  void testGetNumberOfReview() throws SQLException {
    BookReviewDAO dao = new BookReviewDAO();

    // Add a review
    dao.addReview(borrower.getId(), testDocId, 3, "Reviewer2", "Average book",
        new Timestamp(System.currentTimeMillis()));

    // Verify number of reviews
    int totalReviews = dao.getNumberOfReview(testDocId);
    assertEquals(1, totalReviews, "There should be one review for the document.");
  }
}
