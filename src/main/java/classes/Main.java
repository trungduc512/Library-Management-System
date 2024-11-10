package classes;

import dao.BookReviewDAO;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        BookReviewDAO reviewDAO = new BookReviewDAO();
        Scanner scanner = new Scanner(System.in);

        // Test adding a review
        System.out.println("Enter reviewer ID:");
        int reviewerId = scanner.nextInt();
        scanner.nextLine(); // consume the newline

        System.out.println("Enter book ISBN:");
        String bookISBN = scanner.nextLine();

        System.out.println("Enter rating (1-5):");
        int rating = scanner.nextInt();
        scanner.nextLine(); // consume the newline

        System.out.println("Enter your review text:");
        String reviewText = scanner.nextLine();

        // Use current timestamp for the review
        Timestamp timestamp = Timestamp.valueOf(LocalDateTime.now());

        // Try to add a review
        boolean isAdded = reviewDAO.addReview(reviewerId, bookISBN, rating, reviewText, timestamp);
        if (isAdded) {
            System.out.println("Review added successfully!");
        } else {
            System.out.println("Failed to add review.");
        }

        // Fetch all reviews for a specific book
        System.out.println("\nFetching reviews for book ISBN: " + bookISBN);
        List<BookReview> reviews = reviewDAO.getReviews(bookISBN);
        if (reviews.isEmpty()) {
            System.out.println("No reviews found for this book.");
        } else {
            for (BookReview review : reviews) {
                System.out.println("Reviewer ID: " + review.getReviewerId());
                System.out.println("Rating: " + review.getRating());
                System.out.println("Review: " + review.getReviewText());
                System.out.println("Date: " + review.getCreatedAt());
                System.out.println("-------------------------");
            }
        }

        // Get average rating for a specific book
        double averageRating = reviewDAO.getAverageRating(bookISBN);
        System.out.println("\nAverage Rating for book ISBN " + bookISBN + ": " + averageRating);

        // Get total number of reviews for a specific book
        int totalReviews = reviewDAO.getNumberOfReview(bookISBN);
        System.out.println("Total Number of Reviews: " + totalReviews);
    }
}
