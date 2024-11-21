package classes;

import java.sql.Timestamp;
import java.util.Objects;

public class BookReview {
    private int reviewerId;
    private String bookISBN;
    private int rating;
    private String reviewerName;
    private String reviewText;
    private Timestamp createdAt;

    public BookReview() {
    }

    public BookReview(int reviewerId, String bookISBN, int rating, String reviewerName, String reviewText, Timestamp createdAt) {
        this.reviewerId = reviewerId;
        this.bookISBN = bookISBN;
        this.rating = rating;
        this.reviewerName = reviewerName;
        this.reviewText = reviewText;
        this.createdAt = createdAt;
    }

    public String getBookISBN() {
        return bookISBN;
    }

    public void setBookISBN(String bookISBN) {
        this.bookISBN = bookISBN;
    }

    public int getReviewerId() {
        return reviewerId;
    }

    public void setReviewerId(int reviewerId) {
        this.reviewerId = reviewerId;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException();
        }
        this.rating = rating;
    }

    public String getReviewerName() {
        return reviewerName;
    }

    public void setReviewerName(String reviewerName) {
        this.reviewerName = reviewerName;
    }

    public String getReviewText() {
        return reviewText;
    }

    public void setReviewText(String reviewText) {
        this.reviewText = reviewText;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "BookReview{" +
                "bookISBN='" + bookISBN + '\'' +
                ", reviewerId=" + reviewerId +
                ", rating=" + rating +
                ", reviewerName='" + reviewerName +
                ", reviewText='" + reviewText + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BookReview that = (BookReview) o;
        return reviewerId == that.reviewerId && rating == that.rating && Objects.equals(reviewerName, that.reviewerName) &&
                Objects.equals(bookISBN, that.bookISBN) && Objects.equals(reviewText, that.reviewText) &&
                Objects.equals(createdAt, that.createdAt);
    }
}
