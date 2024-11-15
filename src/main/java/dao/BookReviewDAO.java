package dao;
import classes.BookReview;
import classes.DatabaseHelper;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookReviewDAO {

    public List<BookReview> getReviews(String bookISBN) {
        List<BookReview> reviews = new ArrayList<>();
        String query = "SELECT bookISBN, reviewerId, rating, reviewerName, reviewText, createdAt FROM BookReview WHERE bookISBN = ?";

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement statement = conn.prepareStatement(query)) {

            statement.setString(1, bookISBN);

            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                BookReview review = new BookReview();
                review.setBookISBN(rs.getString("bookISBN"));
                review.setReviewerId(rs.getInt("reviewerId"));
                review.setRating(rs.getInt("rating"));
                review.setReviewerName(rs.getString("reviewerName"));
                review.setReviewText(rs.getString("reviewText"));
                review.setCreatedAt(rs.getTimestamp("createdAt"));

                reviews.add(review);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return reviews;
    }

    public double getAverageRating(String bookISBN) {
        String query = "SELECT AVG(rating) AS averageRating FROM BookReview WHERE bookISBN = ?";
        double averageRating = 0.0;

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, bookISBN);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                averageRating = rs.getDouble("averageRating");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return (double) Math.round(averageRating * 10) / 10  ;
    }

    public int getNumberOfReview(String bookISBN) {
        String query = "SELECT COUNT(*) AS totalReview FROM BookReview WHERE bookISBN = ?";
        int total = 0;

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, bookISBN);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                total = rs.getInt("totalReview");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return total;
    }

    // mỗi độc giả được nhận xét tối đa 1 lần với mỗi cuốn sách họ mượn
    public boolean addReview(int reviewerId, String bookISBN, int rating, String reviewerName, String reviewText, Timestamp timestamp) {
        if (hasAlreadyReviewed(reviewerId, bookISBN)) {
            System.out.println("You have already reviewed this book before!");
            return false;
        }

        if (!hasBorrowedBook(reviewerId, bookISBN)) {
            System.out.println("You have to borrow the book first to write a review!");
            return false;
        }

        return insertReview(reviewerId, bookISBN, rating, reviewerName, reviewText, timestamp);
    }

    private boolean hasAlreadyReviewed(int reviewerId, String bookISBN) {
        String query = "SELECT COUNT(*) FROM BookReview WHERE reviewerId = ? AND bookISBN = ?";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, reviewerId);
            stmt.setString(2, bookISBN);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }

        } catch (SQLException e) {
            System.err.println("Error checking if already reviewed: " + e.getMessage());
        }
        return false;
    }

    private boolean hasBorrowedBook(int reviewerId, String bookISBN) {
        String query = "SELECT COUNT(*) FROM BorrowedBookRecord WHERE borrowerId = ? AND isbn = ?";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, reviewerId);
            stmt.setString(2, bookISBN);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }

        } catch (SQLException e) {
            System.err.println("Error checking if book borrowed: " + e.getMessage());
        }
        return false;
    }

    private boolean insertReview(int reviewerId, String bookISBN, int rating, String reviewerName, String reviewText, Timestamp timestamp) {
        String insertQuery = "INSERT INTO BookReview (reviewerId, bookISBN, rating, reviewerName, reviewText, createdAt) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(insertQuery)) {

            stmt.setInt(1, reviewerId);
            stmt.setString(2, bookISBN);
            stmt.setInt(3, rating);
            stmt.setString(4, reviewerName);
            stmt.setString(5, reviewText);
            stmt.setTimestamp(6, timestamp);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error inserting review: " + e.getMessage());
        }
        return false;
    }
}
